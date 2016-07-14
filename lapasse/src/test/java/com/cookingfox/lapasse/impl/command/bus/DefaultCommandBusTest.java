package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.exception.NoRegisteredCommandLoggerException;
import com.cookingfox.lapasse.api.command.exception.UnsupportedCommandHandlerException;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.message.exception.NoMessageHandlersException;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.logging.DefaultLoggersHelper;
import fixtures.event.bus.FixtureEventBus;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import fixtures.state.manager.FixtureStateManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultCommandBus}.
 */
public class DefaultCommandBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultCommandBus<CountState> commandBus;
    private FixtureEventBus eventBus;
    private DefaultLoggersHelper<CountState> loggers;
    private FixtureMessageStore messageStore;
    private FixtureStateManager stateManager;

    @Before
    public void setUp() throws Exception {
        eventBus = new FixtureEventBus();
        loggers = new DefaultLoggersHelper<>();
        messageStore = new FixtureMessageStore();
        stateManager = new FixtureStateManager(new CountState(0));
        commandBus = new DefaultCommandBus<>(messageStore, eventBus, loggers, stateManager);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_message_store_null() throws Exception {
        new DefaultCommandBus<>(null, eventBus, loggers, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_event_bus_null() throws Exception {
        new DefaultCommandBus<>(messageStore, null, loggers, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_loggers_null() throws Exception {
        new DefaultCommandBus<>(messageStore, eventBus, null, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_state_manager_null() throws Exception {
        new DefaultCommandBus<>(messageStore, eventBus, loggers, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_remove_mapped_command_handlers() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return null;
            }
        });

        commandBus.dispose();

        try {
            commandBus.handleCommand(new IncrementCount(1));

            fail("Expected exception");
        } catch (NoMessageHandlersException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void dispose_should_shutdown_command_executor() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        commandBus.setCommandHandlerExecutor(executorService);

        assertFalse(executorService.isShutdown());

        commandBus.dispose();

        assertTrue(executorService.isShutdown());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: executeHandler
    //----------------------------------------------------------------------------------------------

    @Test
    public void executeHandler_should_pass_event_from_sync_handler() throws Exception {
        final int count = 1;
        final CountIncremented event = new CountIncremented(count);

        commandBus.mapCommandHandler(IncrementCount.class,
                new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public CountIncremented handle(CountState state, IncrementCount command) {
                        return event;
                    }
                });

        commandBus.handleCommand(new IncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    @Test
    public void executeHandler_should_pass_events_from_sync_multi_handler() throws Exception {
        final int count = 1;
        final CountIncremented event = new CountIncremented(count);

        commandBus.mapCommandHandler(IncrementCount.class,
                new SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public Collection<CountIncremented> handle(CountState state, IncrementCount command) {
                        return Collections.singletonList(event);
                    }
                });

        commandBus.handleCommand(new IncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    @Test
    public void executeHandler_should_pass_event_from_async_handler() throws Exception {
        final int count = 1;
        final CountIncremented event = new CountIncremented(count);

        commandBus.mapCommandHandler(IncrementCount.class,
                new AsyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public Callable<CountIncremented> handle(CountState state, IncrementCount command) {
                        return new Callable<CountIncremented>() {
                            @Override
                            public CountIncremented call() throws Exception {
                                return event;
                            }
                        };
                    }
                });

        commandBus.handleCommand(new IncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    @Test
    public void executeHandler_should_pass_events_from_async_multi_handler() throws Exception {
        final int count = 1;
        final CountIncremented event = new CountIncremented(count);

        commandBus.mapCommandHandler(IncrementCount.class,
                new AsyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public Callable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {
                        return new Callable<Collection<CountIncremented>>() {
                            @Override
                            public Collection<CountIncremented> call() throws Exception {
                                return Collections.singletonList(event);
                            }
                        };
                    }
                });

        commandBus.handleCommand(new IncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    @Test
    public void executeHandler_should_log_command_handler_result_of_single_handler() throws Exception {
        final AtomicReference<Command> calledCommand = new AtomicReference<>();
        final AtomicReference<Collection<Event>> calledEvents = new AtomicReference<>();

        commandBus.addCommandLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerResult(Command command, Collection<Event> events) {
                calledCommand.set(command);
                calledEvents.set(events);
            }
        });

        commandBus.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return new CountIncremented(command.getCount());
            }
        });

        final IncrementCount command = new IncrementCount(123);

        commandBus.handleCommand(command);

        assertSame(command, calledCommand.get());
        assertEquals(Collections.singleton(new CountIncremented(command.getCount())), calledEvents.get());
    }

    @Test
    public void executeHandler_should_log_error_of_throwing_single_handler() throws Exception {
        final AtomicReference<Throwable> calledError = new AtomicReference<>();
        final AtomicReference<Command> calledCommand = new AtomicReference<>();
        final AtomicReference<Collection<Event>> calledEvents = new AtomicReference<>();

        final RuntimeException targetException = new RuntimeException("Example error");

        commandBus.addCommandLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
                calledCommand.set(command);
                calledEvents.set(events);
            }
        });

        commandBus.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                throw targetException;
            }
        });

        final IncrementCount command = new IncrementCount(123);

        commandBus.handleCommand(command);

        assertSame(targetException, calledError.get());
        assertSame(command, calledCommand.get());
        assertNull(calledEvents.get());
    }

    @Test
    public void executeHandler_should_log_command_handler_result_of_multi_handler() throws Exception {
        final AtomicReference<Command> calledCommand = new AtomicReference<>();
        final AtomicReference<Collection<Event>> calledEvents = new AtomicReference<>();

        commandBus.addCommandLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerResult(Command command, Collection<Event> events) {
                calledCommand.set(command);
                calledEvents.set(events);
            }
        });

        commandBus.mapCommandHandler(IncrementCount.class, new SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Collection<CountIncremented> handle(CountState state, IncrementCount command) {
                // explicitly return null: this is a command handler valid result
                return null;
            }
        });

        final IncrementCount command = new IncrementCount(0);

        commandBus.handleCommand(command);

        assertSame(command, calledCommand.get());
        assertNull(calledEvents.get());
    }

    @Test
    public void executeHandler_should_log_error_of_throwing_multi_handler() throws Exception {
        final AtomicReference<Throwable> calledError = new AtomicReference<>();
        final AtomicReference<Command> calledCommand = new AtomicReference<>();
        final AtomicReference<Collection<Event>> calledEvents = new AtomicReference<>();

        final RuntimeException targetException = new RuntimeException("Example error");

        commandBus.addCommandLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
                calledCommand.set(command);
                calledEvents.set(events);
            }
        });

        commandBus.mapCommandHandler(IncrementCount.class, new SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Collection<CountIncremented> handle(CountState state, IncrementCount command) {
                throw targetException;
            }
        });

        final IncrementCount command = new IncrementCount(123);

        commandBus.handleCommand(command);

        assertSame(targetException, calledError.get());
        assertSame(command, calledCommand.get());
        assertNull(calledEvents.get());
    }

    @Test
    public void executeHandler_should_throw_for_unsupported_single_handler_implementation() throws Exception {
        try {
            commandBus.executeHandler(new IncrementCount(1), new CommandHandler<CountState, Command, Event>() {
            });

            fail("Expected exception");
        } catch (NoRegisteredCommandLoggerException e) {
            assertTrue(e.getCause() instanceof UnsupportedCommandHandlerException);
        }
    }

    @Test
    public void executeHandler_should_throw_for_unsupported_multi_handler_implementation() throws Exception {
        try {
            commandBus.executeHandler(new IncrementCount(1), new MultiCommandHandler<CountState, Command, Event>() {
            });

            fail("Expected exception");
        } catch (NoRegisteredCommandLoggerException e) {
            assertTrue(e.getCause() instanceof UnsupportedCommandHandlerException);
        }
    }

    @Test
    public void executeHandler_should_support_void_handler() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        commandBus.mapCommandHandler(IncrementCount.class, new VoidCommandHandler<CountState, IncrementCount>() {
            @Override
            public void handle(CountState state, IncrementCount command) {
                called.set(true);
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertTrue(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getCommandHandlerExecutor
    //----------------------------------------------------------------------------------------------

    @Test
    public void getCommandHandlerExecutor_defaults_to_single_thread_executor() throws Exception {
        assertNull(commandBus.commandHandlerExecutor);

        ExecutorService executor = commandBus.getCommandHandlerExecutor();

        assertNotNull(executor);
    }

    @Test
    public void getCommandHandlerExecutor_should_return_custom_executor() throws Exception {
        ExecutorService customExecutor = Executors.newCachedThreadPool();

        commandBus.setCommandHandlerExecutor(customExecutor);

        ExecutorService executor = commandBus.getCommandHandlerExecutor();

        assertSame(customExecutor, executor);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: mapCommandHandler
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void mapCommandHandler_should_throw_if_handler_null() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class, null);
    }

    @Test(expected = UnsupportedCommandHandlerException.class)
    public void mapCommandHandler_should_throw_if_implementation_unsupported() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class, new CommandHandler<CountState, IncrementCount, Event>() {
        });
    }

    @Test(expected = UnsupportedCommandHandlerException.class)
    public void mapCommandHandler_should_throw_if_multi_implementation_unsupported() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class, new MultiCommandHandler<CountState, IncrementCount, Event>() {
        });
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: setCommandHandlerExecutor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void setCommandHandlerExecutor_should_throw_if_executor_null() throws Exception {
        commandBus.setCommandHandlerExecutor(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: shouldHandleMessageType
    //----------------------------------------------------------------------------------------------

    @Test
    public void shouldHandleMessageType_should_return_true_for_command() throws Exception {
        boolean result = commandBus.shouldHandleMessageType(new IncrementCount(1));

        assertTrue(result);
    }

    @Test
    public void shouldHandleMessageType_should_return_false_for_non_command() throws Exception {
        boolean result = commandBus.shouldHandleMessageType(new FixtureMessage());

        assertFalse(result);
    }

}

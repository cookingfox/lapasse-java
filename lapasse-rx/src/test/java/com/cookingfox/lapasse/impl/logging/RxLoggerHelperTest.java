package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;
import com.cookingfox.lapasse.api.command.logging.CommandHandlerError;
import com.cookingfox.lapasse.api.command.logging.CommandHandlerResult;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.event.logging.EventHandlerError;
import com.cookingfox.lapasse.api.event.logging.EventHandlerResult;
import com.cookingfox.lapasse.impl.facade.LaPasseRxFacade;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;
import rx.Subscription;
import rx.functions.Action1;

import java.util.concurrent.atomic.AtomicReference;

import static com.cookingfox.lapasse.impl.logging.RxLoggerHelper.*;
import static org.junit.Assert.*;
import static testing.TestingUtils.assertPrivateConstructorInstantiationUnsupported;

/**
 * Unit tests for {@link RxLoggerHelper}.
 */
public class RxLoggerHelperTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_throw() throws Exception {
        assertPrivateConstructorInstantiationUnsupported(RxLoggerHelper.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeCommandHandlerErrors
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeCommandHandlerErrors_should_observe_command_error() throws Exception {
        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0)).build();

        final AtomicReference<CommandHandlerError> called = new AtomicReference<>();

        observeCommandHandlerErrors(facade).subscribe(new Action1<CommandHandlerError>() {
            @Override
            public void call(CommandHandlerError commandHandlerError) {
                called.set(commandHandlerError);
            }
        });

        final IncrementCount command = new IncrementCount(1);
        final RuntimeException error = new RuntimeException("Example error");

        facade.mapCommandHandler(IncrementCount.class, new VoidCommandHandler<CountState, IncrementCount>() {
            @Override
            public void handle(CountState state, IncrementCount command) {
                throw error;
            }
        });

        facade.handleCommand(command);

        CommandHandlerError vo = called.get();

        assertNotNull(vo);
        assertSame(command, vo.getCommand());
        assertSame(error, vo.getError());
    }

    @Test
    public void observeCommandHandlerErrors_should_remove_logger_on_unsubscribe() throws Exception {
        DefaultLoggersHelper<CountState> loggersHelper = new DefaultLoggersHelper<>();

        assertTrue(loggersHelper.commandLoggers.isEmpty());

        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0))
                .setLoggersHelper(loggersHelper)
                .build();

        Subscription subscription = observeCommandHandlerErrors(facade).subscribe();

        assertFalse(loggersHelper.commandLoggers.isEmpty());

        subscription.unsubscribe();

        assertTrue(loggersHelper.commandLoggers.isEmpty());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeCommandHandlerResults
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeCommandHandlerResults_should_observe_command_result() throws Exception {
        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0)).build();

        final AtomicReference<CommandHandlerResult> called = new AtomicReference<>();

        observeCommandHandlerResults(facade).subscribe(new Action1<CommandHandlerResult>() {
            @Override
            public void call(CommandHandlerResult commandHandlerResult) {
                called.set(commandHandlerResult);
            }
        });

        final IncrementCount command = new IncrementCount(1);
        final CountIncremented event = new CountIncremented(1);

        facade.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return event;
            }
        });

        facade.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(event.getCount());
            }
        });

        facade.handleCommand(command);

        CommandHandlerResult vo = called.get();

        assertNotNull(vo);
        assertSame(command, vo.getCommand());
        assertTrue(vo.getEvents().contains(event));
    }

    @Test
    public void observeCommandHandlerResults_should_remove_logger_on_unsubscribe() throws Exception {
        DefaultLoggersHelper<CountState> loggersHelper = new DefaultLoggersHelper<>();

        assertTrue(loggersHelper.commandLoggers.isEmpty());

        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0))
                .setLoggersHelper(loggersHelper)
                .build();

        Subscription subscription = observeCommandHandlerResults(facade).subscribe();

        assertFalse(loggersHelper.commandLoggers.isEmpty());

        subscription.unsubscribe();

        assertTrue(loggersHelper.commandLoggers.isEmpty());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeEventHandlerErrors
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeEventHandlerErrors_should_observe_event_error() throws Exception {
        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0)).build();

        final AtomicReference<EventHandlerError> called = new AtomicReference<>();

        observeEventHandlerErrors(facade).subscribe(new Action1<EventHandlerError>() {
            @Override
            public void call(EventHandlerError eventHandlerError) {
                called.set(eventHandlerError);
            }
        });

        final RuntimeException error = new RuntimeException("Example error");
        final CountIncremented event = new CountIncremented(1);

        facade.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                throw error;
            }
        });

        facade.handleEvent(event);

        EventHandlerError vo = called.get();

        assertNotNull(vo);
        assertSame(event, vo.getEvent());
        assertSame(error, vo.getError());
    }

    @Test
    public void observeEventHandlerErrors_should_remove_logger_on_unsubscribe() throws Exception {
        DefaultLoggersHelper<CountState> loggersHelper = new DefaultLoggersHelper<>();

        assertTrue(loggersHelper.eventLoggers.isEmpty());

        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0))
                .setLoggersHelper(loggersHelper)
                .build();

        Subscription subscription = observeEventHandlerErrors(facade).subscribe();

        assertFalse(loggersHelper.eventLoggers.isEmpty());

        subscription.unsubscribe();

        assertTrue(loggersHelper.eventLoggers.isEmpty());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeEventHandlerResults
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeEventHandlerResults_should_observe_event_result() throws Exception {
        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0)).build();

        final AtomicReference<EventHandlerResult> called = new AtomicReference<>();

        observeEventHandlerResults(facade).subscribe(new Action1<EventHandlerResult>() {
            @Override
            public void call(EventHandlerResult eventHandlerResult) {
                called.set(eventHandlerResult);
            }
        });

        final CountIncremented event = new CountIncremented(1);
        final CountState newState = new CountState(event.getCount());

        facade.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return newState;
            }
        });

        facade.handleEvent(event);

        EventHandlerResult vo = called.get();

        assertNotNull(vo);
        assertSame(event, vo.getEvent());
        assertSame(newState, vo.getNewState());
    }

    @Test
    public void observeEventHandlerResults_should_remove_logger_on_unsubscribe() throws Exception {
        DefaultLoggersHelper<CountState> loggersHelper = new DefaultLoggersHelper<>();

        assertTrue(loggersHelper.eventLoggers.isEmpty());

        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0))
                .setLoggersHelper(loggersHelper)
                .build();

        Subscription subscription = observeEventHandlerResults(facade).subscribe();

        assertFalse(loggersHelper.eventLoggers.isEmpty());

        subscription.unsubscribe();

        assertTrue(loggersHelper.eventLoggers.isEmpty());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: default logger implementations
    //----------------------------------------------------------------------------------------------

    @Test
    public void DefaultCommandLogger_should_not_throw_if_not_overridden() throws Exception {
        NoopCommandLogger logger = new NoopCommandLogger();
        logger.onCommandHandlerError(null, null);
        logger.onCommandHandlerResult(null, null);
    }

    @Test
    public void DefaultEventLogger_should_not_throw_if_not_overridden() throws Exception {
        NoopEventLogger<CountState> logger = new NoopEventLogger<>();
        logger.onEventHandlerError(null, null);
        logger.onEventHandlerResult(null, null);
    }

}

package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.logging.LoggersHelper;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LaPasseFacade}.
 */
public class LaPasseFacadeTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: public methods
    //----------------------------------------------------------------------------------------------

    @Test
    public void methods_should_not_throw() throws Exception {
        DefaultLogger<CountState> logger = new DefaultLogger<>();

        OnStateChanged<CountState> onStateChanged = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                // ignore
            }
        };

        LaPasseFacade<CountState> facade = new LaPasseFacade.Builder<>(new CountState(0)).build();

        /* COMBINED LOGGER */

        facade.addLogger(logger);

        /* COMMAND */

        facade.addCommandLogger(logger);
        facade.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return null;
            }
        });
        facade.handleCommand(new IncrementCount(1));
        facade.setCommandHandlerExecutor(Executors.newSingleThreadExecutor());

        /* EVENT */

        facade.addEventLogger(logger);
        facade.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });
        facade.handleEvent(new CountIncremented(1));

        /* STATE */

        facade.getCurrentState();
        facade.subscribe(onStateChanged);
        facade.unsubscribe(onStateChanged);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: Builder
    //----------------------------------------------------------------------------------------------

    @Test
    public void builder_should_set_correct_defaults() throws Exception {
        LaPasseFacade<CountState> facade = new LaPasseFacade.Builder<>(new CountState(0)).build();

        assertTrue(facade.commandBus instanceof DefaultCommandBus);
        assertTrue(facade.eventBus instanceof DefaultEventBus);
        assertTrue(facade.loggers instanceof LoggersHelper);
        assertTrue(facade.stateObserver instanceof DefaultStateManager);
    }

    @Test
    public void builder_should_apply_custom_settings() throws Exception {
        CountState initialState = new CountState(0);
        LoggerCollection<CountState> loggers = new LoggersHelper<>();
        MessageStore messageStore = new NoStorageMessageStore();
        StateManager<CountState> stateManager = new DefaultStateManager<>(initialState);
        EventBus<CountState> eventBus = new DefaultEventBus<>(messageStore, loggers, stateManager);
        CommandBus<CountState> commandBus = new DefaultCommandBus<>(messageStore, eventBus, loggers, stateManager);

        LaPasseFacade<CountState> facade = new LaPasseFacade.Builder<>(initialState)
                .setCommandBus(commandBus)
                .setEventBus(eventBus)
                .setLoggers(loggers)
                .setMessageStore(messageStore)
                .setStateManager(stateManager)
                .build();

        assertSame(commandBus, facade.commandBus);
        assertSame(eventBus, facade.eventBus);
        assertSame(loggers, facade.loggers);
        assertSame(stateManager, facade.stateObserver);
    }

}

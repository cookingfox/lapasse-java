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
import com.cookingfox.lapasse.impl.logging.LaPasseLoggers;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.command.FixtureIncrementCount;
import fixtures.event.FixtureCountIncremented;
import fixtures.state.FixtureState;
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
        DefaultLogger<FixtureState> logger = new DefaultLogger<>();

        OnStateChanged<FixtureState> onStateChanged = new OnStateChanged<FixtureState>() {
            @Override
            public void onStateChanged(FixtureState state, Event event) {
                // ignore
            }
        };

        LaPasseFacade<FixtureState> facade = LaPasseFacade.builder(new FixtureState(0)).build();

        /* COMMAND */

        facade.addCommandLogger(logger);
        facade.mapCommandHandler(FixtureIncrementCount.class, new SyncCommandHandler<FixtureState, FixtureIncrementCount, FixtureCountIncremented>() {
            @Override
            public FixtureCountIncremented handle(FixtureState state, FixtureIncrementCount command) {
                return null;
            }
        });
        facade.handleCommand(new FixtureIncrementCount(1));
        facade.setCommandHandlerExecutor(Executors.newSingleThreadExecutor());

        /* EVENT */

        facade.addEventLogger(logger);
        facade.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                return new FixtureState(previousState.count + event.count);
            }
        });
        facade.handleEvent(new FixtureCountIncremented(1));

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
        LaPasseFacade<FixtureState> facade = LaPasseFacade.builder(new FixtureState(0)).build();

        assertTrue(facade.commandBus instanceof DefaultCommandBus);
        assertTrue(facade.eventBus instanceof DefaultEventBus);
        assertTrue(facade.stateObserver instanceof DefaultStateManager);
    }

    @Test
    public void builder_should_apply_custom_settings() throws Exception {
        FixtureState initialState = new FixtureState(0);
        LoggerCollection<FixtureState> loggers = new LaPasseLoggers<>();
        MessageStore messageStore = new NoStorageMessageStore();
        StateManager<FixtureState> stateManager = new DefaultStateManager<>(initialState);
        EventBus<FixtureState> eventBus = new DefaultEventBus<>(messageStore, loggers, stateManager);
        CommandBus<FixtureState> commandBus = new DefaultCommandBus<>(messageStore, eventBus, loggers, stateManager);

        LaPasseFacade<FixtureState> facade = LaPasseFacade.builder(initialState)
                .setCommandBus(commandBus)
                .setEventBus(eventBus)
                .setLoggers(loggers)
                .setMessageStore(messageStore)
                .setStateManager(stateManager)
                .build();

        assertSame(commandBus, facade.commandBus);
        assertSame(eventBus, facade.eventBus);
        assertSame(stateManager, facade.stateObserver);
    }

}

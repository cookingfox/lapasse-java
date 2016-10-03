package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLoggersHelper;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.example.state.CountState;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LaPasseFacadeDelegate}.
 */
public class LaPasseFacadeDelegateTest extends AbstractFacadeTest {

    //----------------------------------------------------------------------------------------------
    // ABSTRACT TEST IMPLEMENTATIONS (SEE SUPER)
    //----------------------------------------------------------------------------------------------

    @Override
    Facade<CountState> createTestFacade() {
        return new LaPasseFacadeDelegate<>(facade);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_dispose_components() throws Exception {
        final AtomicBoolean messageStoreCalled = new AtomicBoolean(false);
        final AtomicBoolean loggersCalled = new AtomicBoolean(false);
        final AtomicBoolean stateManagerCalled = new AtomicBoolean(false);
        final AtomicBoolean eventBusCalled = new AtomicBoolean(false);
        final AtomicBoolean commandBusCalled = new AtomicBoolean(false);

        CountState initialState = new CountState(0);

        MessageStore messageStore = new NoStorageMessageStore() {
            @Override
            public void dispose() {
                messageStoreCalled.set(true);
            }
        };

        LoggersHelper<CountState> loggers = new DefaultLoggersHelper<CountState>() {
            @Override
            public void dispose() {
                loggersCalled.set(true);
            }
        };

        StateManager<CountState> stateManager = new DefaultStateManager<CountState>(initialState) {
            @Override
            public void dispose() {
                stateManagerCalled.set(true);
            }
        };

        EventBus<CountState> eventBus = new DefaultEventBus<CountState>(messageStore, loggers, stateManager) {
            @Override
            public void dispose() {
                eventBusCalled.set(true);
            }
        };

        CommandBus<CountState> commandBus = new DefaultCommandBus<CountState>(messageStore, eventBus, loggers, stateManager) {
            @Override
            public void dispose() {
                commandBusCalled.set(true);
            }
        };

        LaPasseFacade<CountState> facade = new LaPasseFacade.Builder<>(initialState)
                .setCommandBus(commandBus)
                .setEventBus(eventBus)
                .setLoggersHelper(loggers)
                .setMessageStore(messageStore)
                .setStateManager(stateManager)
                .build();

        LaPasseFacadeDelegate<CountState> delegate = new LaPasseFacadeDelegate<>(facade);
        delegate.dispose();

        assertTrue(commandBusCalled.get());
        assertTrue(eventBusCalled.get());
        assertTrue(loggersCalled.get());
        assertTrue(messageStoreCalled.get());
        assertTrue(stateManagerCalled.get());
    }

}

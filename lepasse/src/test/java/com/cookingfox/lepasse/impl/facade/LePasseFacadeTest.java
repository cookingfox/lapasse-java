package com.cookingfox.lepasse.impl.facade;

import com.cookingfox.lepasse.api.command.bus.CommandBus;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.state.manager.StateManager;
import com.cookingfox.lepasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lepasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lepasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lepasse.impl.state.manager.DefaultStateManager;
import fixtures.state.FixtureState;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LePasseFacade}.
 */
public class LePasseFacadeTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: Builder
    //----------------------------------------------------------------------------------------------

    @Test
    public void builder_should_set_correct_defaults() throws Exception {
        LePasseFacade<FixtureState> facade = LePasseFacade.builder(new FixtureState(0)).build();

        assertTrue(facade.commandBus instanceof DefaultCommandBus);
        assertTrue(facade.eventBus instanceof DefaultEventBus);
        assertTrue(facade.messageStore instanceof NoStorageMessageStore);
        assertTrue(facade.stateObserver instanceof DefaultStateManager);
    }

    @Test
    public void builder_should_apply_custom_settings() throws Exception {
        FixtureState initialState = new FixtureState(0);
        MessageStore messageStore = new NoStorageMessageStore();
        StateManager<FixtureState> stateManager = new DefaultStateManager<>(initialState);
        EventBus<FixtureState> eventBus = new DefaultEventBus<>(messageStore, stateManager);
        CommandBus<FixtureState> commandBus = new DefaultCommandBus<>(messageStore, eventBus, stateManager);

        LePasseFacade<FixtureState> facade = LePasseFacade.builder(initialState)
                .setCommandBus(commandBus)
                .setEventBus(eventBus)
                .setMessageStore(messageStore)
                .setStateManager(stateManager)
                .build();

        assertSame(commandBus, facade.commandBus);
        assertSame(eventBus, facade.eventBus);
        assertSame(messageStore, facade.messageStore);
        assertSame(stateManager, facade.stateObserver);
    }

}

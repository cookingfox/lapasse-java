package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import com.cookingfox.lapasse.impl.facade.LaPasseRxFacade.Builder;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.example.state.CountState;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link LaPasseRxFacade}.
 */
public class LaPasseRxFacadeTest extends AbstractRxFacadeTest<LaPasseRxFacade<CountState>> {

    //----------------------------------------------------------------------------------------------
    // ABSTRACT TEST IMPLEMENTATIONS (SEE SUPER)
    //----------------------------------------------------------------------------------------------

    @Override
    LaPasseRxFacade<CountState> createTestFacade() {
        return facade;
    }

    // TODO: add `dispose` tests

    //----------------------------------------------------------------------------------------------
    // TESTS: Builder
    //----------------------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void setCommandBus_should_throw_if_not_rx_impl() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));

        builder.setCommandBus(new DefaultCommandBus<>(builder.getMessageStore(),
                builder.getEventBus(), builder.getLoggersHelper(), builder.getStateManager()));
    }

    @Test
    public void setCommandBus_should_accept_rx_impl() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));

        DefaultRxCommandBus<CountState> commandBus = new DefaultRxCommandBus<>(builder.getMessageStore(),
                builder.getEventBus(), builder.getLoggersHelper(), builder.getStateManager());

        builder.setCommandBus(commandBus);

        assertSame(commandBus, builder.getCommandBus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStateManager_should_throw_if_not_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        Builder<CountState> builder = new Builder<>(initialState);

        builder.setStateManager(new DefaultStateManager<>(initialState));
    }

    @Test
    public void setStateManager_should_accept_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        Builder<CountState> builder = new Builder<>(initialState);

        DefaultRxStateManager<CountState> stateManager = new DefaultRxStateManager<>(initialState);

        builder.setStateManager(stateManager);

        assertSame(stateManager, builder.getStateManager());
    }

    @Test
    public void setters_should_return_rx_typed_builder() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));

        Builder<CountState> fromSetEventBus = builder.setEventBus(builder.getEventBus());
        Builder<CountState> fromSetLoggersHelper = builder.setLoggersHelper(builder.getLoggersHelper());
        Builder<CountState> fromSetMessageStore = builder.setMessageStore(builder.getMessageStore());

        assertSame(builder, fromSetEventBus);
        assertSame(builder, fromSetLoggersHelper);
        assertSame(builder, fromSetMessageStore);
    }

    @Test
    public void getters_should_create_defaults_if_null() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));
        builder.commandBus = null;
        builder.stateManager = null;

        RxCommandBus<CountState> commandBus = builder.getCommandBus();
        RxStateManager<CountState> stateManager = builder.getStateManager();

        assertNotNull(commandBus);
        assertNotNull(stateManager);
    }

}

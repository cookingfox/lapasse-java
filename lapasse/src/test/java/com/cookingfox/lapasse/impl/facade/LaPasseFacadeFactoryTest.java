package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.facade.FacadeBuilder;
import com.cookingfox.lapasse.api.facade.FacadeBuilderConfig;
import com.cookingfox.lapasse.api.facade.FacadeFactory;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link LaPasseFacadeFactory}.
 */
public class LaPasseFacadeFactoryTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private LaPasseFacadeFactory facadeFactory;

    @Before
    public void setUp() throws Exception {
        facadeFactory = new LaPasseFacadeFactory();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addBuilderConfig
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addBuilderConfig_should_throw_if_config_null() throws Exception {
        this.facadeFactory.addBuilderConfig(null);
    }

    @Test
    public void addBuilderConfig_should_add_config() throws Exception {
        assertFalse(facadeFactory.builderConfigs.contains(noopBuilderConfig));

        facadeFactory.addBuilderConfig(noopBuilderConfig);

        assertTrue(facadeFactory.builderConfigs.contains(noopBuilderConfig));
    }

    @Test
    public void addBuilderConfig_should_not_throw_if_already_added() throws Exception {
        facadeFactory.addBuilderConfig(noopBuilderConfig);
        facadeFactory.addBuilderConfig(noopBuilderConfig);
    }

    @Test
    public void addBuilderConfig_should_return_factory_instance() throws Exception {
        FacadeFactory returned = facadeFactory.addBuilderConfig(noopBuilderConfig);

        assertSame(facadeFactory, returned);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newBuilder
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void newBuilder_should_throw_if_initial_state_null() throws Exception {
        facadeFactory.newBuilder(null);
    }

    @Test
    public void newBuilder_should_return_facade_builder() throws Exception {
        FacadeBuilder<CountState> builder = facadeFactory.newBuilder(CountState.createInitialState());

        assertNotNull(builder);
    }

    @Test
    public void newBuilder_should_apply_builder_config() throws Exception {
        final MessageStore messageStore = new NoStorageMessageStore();

        FacadeBuilderConfig messageStoreConfig = new FacadeBuilderConfig() {
            @Override
            public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
                facadeBuilder.setMessageStore(messageStore);
            }
        };

        facadeFactory.addBuilderConfig(messageStoreConfig);

        FacadeBuilder<CountState> builder = facadeFactory.newBuilder(CountState.createInitialState());

        assertSame(messageStore, builder.getMessageStore());
    }

    @Test
    public void newBuilder_should_apply_builder_configs_in_sequence() throws Exception {
        final int[] expected = {100, 200, 300};
        final int[] actual = new int[expected.length];

        facadeFactory.addBuilderConfig(new FacadeBuilderConfig() {
            @Override
            public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
                actual[0] = expected[0];
            }
        });

        facadeFactory.addBuilderConfig(new FacadeBuilderConfig() {
            @Override
            public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
                actual[1] = expected[1];
            }
        });

        facadeFactory.addBuilderConfig(new FacadeBuilderConfig() {
            @Override
            public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
                actual[2] = expected[2];
            }
        });

        facadeFactory.newBuilder(CountState.createInitialState());

        assertArrayEquals(expected, actual);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newFacade
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void newFacade_should_throw_if_initial_state_null() throws Exception {
        facadeFactory.newFacade(null);
    }

    @Test
    public void newFacade_should_use_builder_to_create_facade() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        facadeFactory.addBuilderConfig(new FacadeBuilderConfig() {
            @Override
            public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
                called.set(true);
            }
        });

        Facade<CountState> facade = facadeFactory.newFacade(CountState.createInitialState());

        assertNotNull(facade);
        assertTrue(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    final FacadeBuilderConfig noopBuilderConfig = new FacadeBuilderConfig() {
        @Override
        public <S extends State> void configure(FacadeBuilder<S> facadeBuilder) {
            // no-op
        }
    };

}

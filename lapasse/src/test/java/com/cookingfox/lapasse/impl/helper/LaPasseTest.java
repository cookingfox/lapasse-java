package com.cookingfox.lapasse.impl.helper;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.facade.LaPasseFacade;
import com.cookingfox.lapasse.impl.helper.exception.GeneratedConstructorNotFoundException;
import com.cookingfox.lapasse.impl.helper.exception.HandlerMapperInstantiationException;
import com.cookingfox.lapasse.impl.helper.exception.NoGeneratedClassException;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;
import fixtures.annotations.FixtureAnnotated;
import fixtures.annotations.FixtureAnnotatedFacadeDelegate;
import fixtures.annotations.MissingConstructor$$LaPassGenerated;
import fixtures.annotations.ThrowingConstructor$$LaPasseGenerated;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LaPasse}.
 */
public class LaPasseTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private LaPasseFacade<CountState> facade;

    @Before
    public void setUp() throws Exception {
        facade = new LaPasseFacade.Builder<>(new CountState(0)).build();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: createHandlerMapperInstance
    //----------------------------------------------------------------------------------------------

    @Test(expected = HandlerMapperInstantiationException.class)
    public void createHandlerMapperInstance_should_throw_if_instantiation_error() throws Exception {
        FixtureAnnotated origin = new FixtureAnnotated(facade);

        Constructor<? extends HandlerMapper> handlerMapperConstructor =
                LaPasse.getHandlerMapperConstructor(ThrowingConstructor$$LaPasseGenerated.class, origin.getClass());

        LaPasse.createHandlerMapperInstance(handlerMapperConstructor, origin, facade);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getHandlerMapperConstructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = GeneratedConstructorNotFoundException.class)
    public void getHandlerMapperConstructor_should_throw_if_missing_constructor() throws Exception {
        LaPasse.getHandlerMapperConstructor(MissingConstructor$$LaPassGenerated.class, FixtureAnnotated.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: mapHandlers
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void mapHandlers_should_throw_if_origin_null() throws Exception {
        LaPasse.mapHandlers(null, facade);
    }

    @Test(expected = NullPointerException.class)
    public void mapHandlers_should_throw_if_facade_null() throws Exception {
        LaPasse.mapHandlers(this, null);
    }

    @Test(expected = NoGeneratedClassException.class)
    public void mapHandlers_should_throw_if_generated_class_does_not_exist() throws Exception {
        LaPasse.mapHandlers(this, facade);
    }

    @Test
    public void mapHandlers_should_map_handlers_from_generated_class() throws Exception {
        FixtureAnnotated origin = new FixtureAnnotated(facade);
        origin.mapHandlers();

        final AtomicReference<CountState> calledStateRef = new AtomicReference<>();
        final AtomicReference<Event> calledEventRef = new AtomicReference<>();

        facade.subscribe(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                calledStateRef.set(state);
                calledEventRef.set(event);
            }
        });

        final int incrementValue = 123;

        facade.handleCommand(new IncrementCount(incrementValue));

        assertEquals(incrementValue, calledStateRef.get().getCount());
        assertTrue(calledEventRef.get() instanceof CountIncremented);
    }

    @Test
    public void mapHandlers_should_accept_facade_origin_as_only_argument() throws Exception {
        FixtureAnnotatedFacadeDelegate facadeOrigin = new FixtureAnnotatedFacadeDelegate(facade);
        facadeOrigin.mapHandlers();

        final AtomicReference<CountState> calledStateRef = new AtomicReference<>();
        final AtomicReference<Event> calledEventRef = new AtomicReference<>();

        facade.subscribe(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                calledStateRef.set(state);
                calledEventRef.set(event);
            }
        });

        final int incrementValue = 123;

        facade.handleCommand(new IncrementCount(incrementValue));

        assertEquals(incrementValue, calledStateRef.get().getCount());
        assertTrue(calledEventRef.get() instanceof CountIncremented);
    }

}

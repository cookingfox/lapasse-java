package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;

import java.util.concurrent.Executors;

/**
 * Unit tests for {@link LaPasseFacadeDelegate}.
 */
public class LaPasseFacadeDelegateTest {

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
        LaPasseFacadeDelegate<CountState> delegate = new LaPasseFacadeDelegate<>(facade);

        /* COMBINED LOGGER */

        delegate.addLogger(logger);
        delegate.removeLogger(logger);

        /* COMMAND */

        delegate.addCommandLogger(logger);
        delegate.removeCommandLogger(logger);
        delegate.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return null;
            }
        });
        delegate.handleCommand(new IncrementCount(1));
        delegate.setCommandHandlerExecutor(Executors.newSingleThreadExecutor());

        /* EVENT */

        delegate.addEventLogger(logger);
        delegate.removeEventLogger(logger);
        delegate.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });
        delegate.handleEvent(new CountIncremented(1));

        /* STATE */

        delegate.getCurrentState();
        delegate.addStateChangedListener(onStateChanged);
        delegate.removeStateChangedListener(onStateChanged);

        /* DISPOSE */

        delegate.dispose();
    }

}

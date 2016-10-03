package testing;

import com.cookingfox.lapasse.api.command.logging.CommandLoggerHelper;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import rx.Scheduler;

/**
 * Implementation of {@link DefaultRxCommandBus} with extra methods for testing.
 *
 * @param <S> The concrete type of the state object.
 */
public class TestableRxCommandBus<S extends State> extends DefaultRxCommandBus<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public TestableRxCommandBus(MessageStore messageStore,
                                EventBus<S> eventBus,
                                CommandLoggerHelper loggerHelper,
                                RxStateObserver<S> stateObserver) {
        super(messageStore, eventBus, loggerHelper, stateObserver);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return The current value for the "observeOn" Scheduler.
     */
    public Scheduler getRawObserveOnScheduler() {
        return observeOnScheduler;
    }

    /**
     * @return The current value for the "subscribeOn" Scheduler.
     */
    public Scheduler getRawSubscribeOnScheduler() {
        return subscribeOnScheduler;
    }

}

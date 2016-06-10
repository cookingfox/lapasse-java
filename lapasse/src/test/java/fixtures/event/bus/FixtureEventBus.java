package fixtures.event.bus;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import fixtures.example.state.CountState;

import java.util.LinkedList;
import java.util.List;

/**
 * Minimal implementation of {@link EventBus} for testing purposes only.
 */
public class FixtureEventBus implements EventBus<CountState> {

    public final List<Event> handleEventCalls = new LinkedList<>();
    public final List<MapCall> mapEventHandlerCalls = new LinkedList<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addEventLogger(EventLogger<CountState> logger) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void handleEvent(Event event) {
        handleEventCalls.add(event);
    }

    @Override
    public <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<CountState, E> eventHandler) {
        // noinspection unchecked
        mapEventHandlerCalls.add(new MapCall(eventClass, eventHandler));
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASS: MapCall
    //----------------------------------------------------------------------------------------------

    public static class MapCall<E extends Event> {

        public final Class<E> eventClass;
        public final EventHandler<CountState, E> eventHandler;

        public MapCall(Class<E> eventClass, EventHandler<CountState, E> eventHandler) {
            this.eventClass = eventClass;
            this.eventHandler = eventHandler;
        }

    }

}

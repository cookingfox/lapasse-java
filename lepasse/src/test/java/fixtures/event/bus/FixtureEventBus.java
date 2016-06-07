package fixtures.event.bus;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.event.handler.EventHandler;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import fixtures.state.FixtureState;

import java.util.LinkedList;
import java.util.List;

/**
 * Minimal implementation of {@link EventBus} for testing purposes only.
 */
public class FixtureEventBus implements EventBus<FixtureState> {

    public final List<Event> handleEventCalls = new LinkedList<>();
    public final List<MapCall> mapEventHandlerCalls = new LinkedList<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addEventLogger(EventLogger<FixtureState> logger) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void handleEvent(Event event) {
        handleEventCalls.add(event);
    }

    @Override
    public <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<FixtureState, E> eventHandler) {
        // noinspection unchecked
        mapEventHandlerCalls.add(new MapCall(eventClass, eventHandler));
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASS: MapCall
    //----------------------------------------------------------------------------------------------

    public static class MapCall<E extends Event> {

        public final Class<E> eventClass;
        public final EventHandler<FixtureState, E> eventHandler;

        public MapCall(Class<E> eventClass, EventHandler<FixtureState, E> eventHandler) {
            this.eventClass = eventClass;
            this.eventHandler = eventHandler;
        }

    }

}

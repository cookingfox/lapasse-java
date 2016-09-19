package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.exception.NoRegisteredCommandLoggerException;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.exception.NoRegisteredEventLoggerException;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.state.State;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DefaultLoggersHelper}.
 */
public class DefaultLoggersHelperTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultLoggersHelper<CountState> loggers;

    @Before
    public void setUp() throws Exception {
        loggers = new DefaultLoggersHelper<>();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addCommandLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addCommandLogger_should_throw_if_logger_null() throws Exception {
        loggers.addCommandLogger(null);
    }

    @Test
    public void addCommandLogger_should_add_logger() throws Exception {
        final AtomicBoolean commandErrorCalled = new AtomicBoolean(false);
        final AtomicBoolean commandResultCalled = new AtomicBoolean(false);

        loggers.addCommandLogger(new CommandLogger() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command) {
                commandErrorCalled.set(true);
            }

            @Override
            public void onCommandHandlerResult(Command command, Collection<Event> events) {
                commandResultCalled.set(true);
            }
        });

        loggers.onCommandHandlerError(null, null);
        loggers.onCommandHandlerResult(null, null);

        assertTrue(commandErrorCalled.get());
        assertTrue(commandResultCalled.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeCommandLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeCommandLogger_should_throw_if_null() throws Exception {
        loggers.removeCommandLogger(null);
    }

    @Test
    public void removeCommandLogger_should_not_throw_if_not_added() throws Exception {
        loggers.removeCommandLogger(new DefaultLogger<>());
    }

    @Test
    public void removeCommandLogger_should_remove_logger() throws Exception {
        DefaultLogger<State> logger = new DefaultLogger<>();

        loggers.addCommandLogger(logger);

        assertTrue(loggers.commandLoggers.contains(logger));

        loggers.removeCommandLogger(logger);

        assertFalse(loggers.commandLoggers.contains(logger));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: onCommandHandlerError
    //----------------------------------------------------------------------------------------------

    @Test(expected = NoRegisteredCommandLoggerException.class)
    public void onCommandHandlerError_should_throw_if_no_command_loggers() throws Exception {
        loggers.onCommandHandlerError(null, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addEventLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addEventLogger_should_throw_if_logger_null() throws Exception {
        loggers.addEventLogger(null);
    }

    @Test
    public void addEventLogger_should_add_logger() throws Exception {
        final AtomicBoolean eventErrorCalled = new AtomicBoolean(false);
        final AtomicBoolean eventResultCalled = new AtomicBoolean(false);

        loggers.addEventLogger(new EventLogger<CountState>() {
            @Override
            public void onEventHandlerError(Throwable error, Event event) {
                eventErrorCalled.set(true);
            }

            @Override
            public void onEventHandlerResult(Event event, CountState newState) {
                eventResultCalled.set(true);
            }
        });

        loggers.onEventHandlerError(null, null);
        loggers.onEventHandlerResult(null, null);

        assertTrue(eventErrorCalled.get());
        assertTrue(eventResultCalled.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeEventLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeEventLogger_should_throw_if_null() throws Exception {
        loggers.removeEventLogger(null);
    }

    @Test
    public void removeEventLogger_should_not_throw_if_not_added() throws Exception {
        loggers.removeEventLogger(new DefaultLogger<CountState>());
    }

    @Test
    public void removeEventLogger_should_remove_logger() throws Exception {
        DefaultLogger<CountState> logger = new DefaultLogger<>();

        loggers.addEventLogger(logger);

        assertTrue(loggers.eventLoggers.contains(logger));

        loggers.removeEventLogger(logger);

        assertFalse(loggers.eventLoggers.contains(logger));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: onEventHandlerError
    //----------------------------------------------------------------------------------------------

    @Test(expected = NoRegisteredEventLoggerException.class)
    public void onEventHandlerError_should_throw_if_no_event_loggers() throws Exception {
        loggers.onEventHandlerError(null, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addLogger_should_throw_if_logger_null() throws Exception {
        loggers.addLogger(null);
    }

    @Test
    public void addLogger_should_add_multiple_loggers() throws Exception {
        final AtomicBoolean commandErrorCalled = new AtomicBoolean(false);
        final AtomicBoolean commandResultCalled = new AtomicBoolean(false);
        final AtomicBoolean eventErrorCalled = new AtomicBoolean(false);
        final AtomicBoolean eventResultCalled = new AtomicBoolean(false);

        loggers.addLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command) {
                commandErrorCalled.set(true);
            }

            @Override
            public void onCommandHandlerResult(Command command, Collection<Event> events) {
                commandResultCalled.set(true);
            }

            @Override
            public void onEventHandlerError(Throwable error, Event event) {
                eventErrorCalled.set(true);
            }

            @Override
            public void onEventHandlerResult(Event event, CountState newState) {
                eventResultCalled.set(true);
            }
        });

        loggers.onCommandHandlerError(null, null);
        loggers.onCommandHandlerResult(null, null);
        loggers.onEventHandlerError(null, null);
        loggers.onEventHandlerResult(null, null);

        assertTrue(commandErrorCalled.get());
        assertTrue(commandResultCalled.get());
        assertTrue(eventErrorCalled.get());
        assertTrue(eventResultCalled.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeLogger
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeLogger_should_throw_if_null() throws Exception {
        loggers.removeLogger(null);
    }

    @Test
    public void removeLogger_should_not_throw_if_not_added() throws Exception {
        loggers.removeLogger(new DefaultLogger<CountState>());
    }

    @Test
    public void removeLogger_should_remove_logger() throws Exception {
        DefaultLogger<CountState> logger = new DefaultLogger<>();

        loggers.addLogger(logger);

        assertTrue(loggers.commandLoggers.contains(logger));
        assertTrue(loggers.eventLoggers.contains(logger));

        loggers.removeLogger(logger);

        assertFalse(loggers.commandLoggers.contains(logger));
        assertFalse(loggers.eventLoggers.contains(logger));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_remove_event_listeners() throws Exception {
        DefaultLogger<CountState> logger = new DefaultLogger<>();

        loggers.addLogger(logger);

        assertTrue(loggers.commandLoggers.contains(logger));
        assertTrue(loggers.eventLoggers.contains(logger));

        loggers.dispose();

        assertFalse(loggers.commandLoggers.contains(logger));
        assertFalse(loggers.eventLoggers.contains(logger));
    }

}

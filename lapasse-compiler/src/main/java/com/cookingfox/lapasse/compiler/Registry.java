package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.command.HandleCommandInfo;
import com.cookingfox.lapasse.compiler.event.HandleEventInfo;
import com.cookingfox.lapasse.compiler.exception.HandlerTargetStateConflictException;
import com.squareup.javapoet.TypeName;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Manages parsed annotated method information for commands and events, for one target class.
 */
class Registry {

    protected final List<HandleCommandInfo> handleCommands = new LinkedList<>();
    protected final List<HandleEventInfo> handleEvents = new LinkedList<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public void addHandleCommandInfo(HandleCommandInfo info) {
        if (handleCommands.contains(Objects.requireNonNull(info))) {
            throw new IllegalStateException();
        }

        handleCommands.add(info);
    }

    public void addHandleEventInfo(HandleEventInfo info) {
        if (handleEvents.contains(Objects.requireNonNull(info))) {
            throw new IllegalStateException();
        }

        handleEvents.add(info);
    }

    /**
     * Compare the target state type ({@link #getTargetStateName()}) with the state type as defined
     * by all added command and event handlers. Throw if there is a mismatch.
     *
     * @throws HandlerTargetStateConflictException when the handler state type does not match the
     *                                             target type.
     */
    public void detectTargetStateConflict() throws HandlerTargetStateConflictException {
        TypeName targetState = getTargetStateName();

        // TODO: throw here if target is null, but there are mapped handlers (when command handlers can omit state param)

        for (HandleEventInfo handleEventInfo : handleEvents) {
            TypeName eventState = handleEventInfo.getStateName();

            if (!eventState.equals(targetState)) {
                String error = String.format("Mapped event handler does not match expected " +
                        "concrete State. Expected: %s. Received: %s", targetState, eventState);

                throw new HandlerTargetStateConflictException(error);
            }
        }

        for (HandleCommandInfo handleCommandInfo : handleCommands) {
            // TODO: this might return an empty future in the future
            TypeName commandState = handleCommandInfo.getStateName();

            if (!commandState.equals(targetState)) {
                String error = String.format("Mapped command handler does not match expected " +
                        "concrete State. Expected: %s. Received: %s", targetState, commandState);

                throw new HandlerTargetStateConflictException(error);
            }
        }
    }

    public List<HandleCommandInfo> getHandleCommands() {
        return handleCommands;
    }

    public List<HandleEventInfo> getHandleEvents() {
        return handleEvents;
    }

    /**
     * Attempts to determine the most accurate concrete {@link State} type, based on the present
     * command and event handler info. Since event handlers must always reference the State object,
     * the event handler return type is leading in this determination.
     *
     * @return The target State type name, based on the present command and event handler info.
     */
    public TypeName getTargetStateName() {
        if (!handleEvents.isEmpty()) {
            return handleEvents.get(0).getStateName();
        } else if (!handleCommands.isEmpty()) {
            // TODO: in the future this could return an empty value, when the command handler `state` param is optional
            return handleCommands.get(0).getStateName();
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Registry{" +
                "handleCommands=" + handleCommands +
                ", handleEvents=" + handleEvents +
                '}';
    }

}

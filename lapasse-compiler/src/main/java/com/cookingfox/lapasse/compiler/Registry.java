package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.compiler.command.HandleCommandInfo;
import com.cookingfox.lapasse.compiler.event.HandleEventInfo;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Manages parsed annotated method infomation for commands and events.
 */
class Registry {

    private final Set<HandleCommandInfo> handleCommands = new LinkedHashSet<>();
    private final Set<HandleEventInfo> handleEvents = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public Registry addHandleCommandInfo(HandleCommandInfo info) {
        handleCommands.add(Objects.requireNonNull(info));
        return this;
    }

    public Registry addHandleEventInfo(HandleEventInfo info) {
        handleEvents.add(Objects.requireNonNull(info));
        return this;
    }

    public Set<HandleCommandInfo> getHandleCommands() {
        return handleCommands;
    }

    public Set<HandleEventInfo> getHandleEvents() {
        return handleEvents;
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

package com.cookingfox.lapasse.samples.shared.counter.command;

import com.cookingfox.lapasse.api.command.Command;

/**
 * Created by abeldebeer on 16/02/17.
 */
public final class DecrementCount implements Command {

    @Override
    public String toString() {
        return "DecrementCount{}";
    }

}

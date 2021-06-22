package com.github.sanctum.retro.construct.trade.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * A base class for trader errors.
 */
public abstract class TraderException extends Exception {
    protected TraderException() {
        super();
    }
    protected TraderException(@Nullable String message) {
        super(message);
    }
    protected TraderException(@Nullable String message, Throwable cause) {
        super(message, cause);
    }
}

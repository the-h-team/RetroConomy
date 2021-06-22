package com.github.sanctum.retro.construct.trade.exceptions;

public final class InvalidTradeOfferException extends Exception {
    public InvalidTradeOfferException(String message) {
        super(message);
    }
    public InvalidTradeOfferException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTradeOfferException(Throwable cause) {
        super(cause);
    }
}

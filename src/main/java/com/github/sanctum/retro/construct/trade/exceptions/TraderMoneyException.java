package com.github.sanctum.retro.construct.trade.exceptions;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;

public class TraderMoneyException extends TraderException {
    private final BigDecimal amount;

    public TraderMoneyException() {
        this.amount = null;
    }
    public TraderMoneyException(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public Optional<BigDecimal> getAmount() {
        return Optional.ofNullable(amount);
    }
}

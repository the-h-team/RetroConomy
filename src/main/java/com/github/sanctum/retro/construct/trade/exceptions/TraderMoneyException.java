package com.github.sanctum.retro.construct.trade.exceptions;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;

public class TraderMoneyException extends TraderException {
    private final BigDecimal amount;

    public TraderMoneyException(String detail) {
        super(detail);
        this.amount = null;
    }
    public TraderMoneyException(@NotNull BigDecimal amount, String details) {
        super(details);
        this.amount = amount;
    }

    public Optional<BigDecimal> getAmount() {
        return Optional.ofNullable(amount);
    }
}

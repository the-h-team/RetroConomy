package com.github.sanctum.retro.construct.trade;

import com.github.sanctum.retro.construct.trade.exceptions.TraderMoneyException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * A trader that can send and receive money.
 */
public interface MoneyTrader extends Trader {
    void giveMoney(@NotNull BigDecimal amount) throws TraderMoneyException;
    void takeMoney(@NotNull BigDecimal amount) throws TraderMoneyException;

    default void giveMoneyTo(MoneyTrader to, @NotNull BigDecimal amount) throws TraderMoneyException {
        takeMoney(amount);
        to.giveMoney(amount);
    }
    default void takeMoneyFrom(MoneyTrader from, @NotNull BigDecimal amount) throws TraderMoneyException {
        from.takeMoney(amount);
        giveMoney(amount);
    }
}

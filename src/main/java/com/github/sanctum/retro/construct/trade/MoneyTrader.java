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
    void giveMoneyTo(Trader to, @NotNull BigDecimal amount) throws TraderMoneyException;
    void takeMoneyFrom(Trader from, @NotNull BigDecimal amount) throws TraderMoneyException;
}

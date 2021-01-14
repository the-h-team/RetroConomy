package com.youtube.hempfest.retro.construct;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;

import java.math.BigDecimal;

public class ActionUtil {
    public static EconomyAction unsuccessful(EconomyEntity holder) {
        return new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
    }
    public static EconomyAction notEnoughMoney(EconomyEntity holder) {
        return new EconomyAction(holder, false, "Not enough money.");
    }
    public static EconomyAction depositedAccount(EconomyEntity holder, BigDecimal amount, String accountId) {
        return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
    }
    public static EconomyAction withdrewAccount(EconomyEntity holder, BigDecimal amount, String accountId) {
        return new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
    }
}

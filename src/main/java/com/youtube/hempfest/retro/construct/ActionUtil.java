package com.youtube.hempfest.retro.construct;


import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.entity.EconomyEntity;
import java.math.BigDecimal;

public class ActionUtil {
    public static EconomyAction unsuccessful(EconomyEntity holder) {
        return new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
    }

    public static EconomyAction notEnoughMoney(EconomyEntity holder) {
        return new EconomyAction(holder, false, "Not enough money.");
    }

    public static EconomyAction depositedAccount(EconomyEntity holder, BigDecimal amount, String accountId) {
        return new EconomyAction(amount, holder, true, amount + " deposited to account " + accountId);
    }
    public static EconomyAction withdrewAccount(EconomyEntity holder, BigDecimal amount, String accountId) {
        return new EconomyAction(amount, holder, true, amount + " withdrawn from account " + accountId);
    }

    public static EconomyAction serverAccountAccess(EconomyEntity holder) {
        return new EconomyAction(holder, true, "Server account accessed.");
    }

    public static EconomyAction memberAccess(EconomyEntity holder, boolean isMember) {
        return new EconomyAction(holder, isMember, isMember ? "The given player has member access" : "The given player has no access.");
    }

    public static EconomyAction owner(EconomyEntity holder, boolean isOwner) {
        return new EconomyAction(holder, isOwner, holder.friendlyName() + " is" + (isOwner ? " not" : "") + " the account owner.");
    }
    public static EconomyAction jointOwner(EconomyEntity holder, boolean isJointOwner) {
        return new EconomyAction(holder, isJointOwner, holder.friendlyName() + " is" + (isJointOwner ? " not" : "") + " a joint account owner.");
    }

    public static EconomyAction depositedWallet(EconomyEntity holder, BigDecimal amount) {
        return new EconomyAction(amount, holder, true, amount + " deposited to Wallet");
    }
    public static EconomyAction withdrewWallet(EconomyEntity holder, BigDecimal amount) {
        return new EconomyAction(amount, holder, true, amount + " withdrawn from Wallet");
    }
    public static EconomyAction depositedWallet(EconomyEntity holder, BigDecimal amount, String world) {
        return new EconomyAction(amount, holder, true, amount + " deposited to Wallet in world " + world);
    }
    public static EconomyAction withdrewWallet(EconomyEntity holder, BigDecimal amount, String world) {
        return new EconomyAction(amount, holder, true, amount + " withdrawn from Wallet in world " + world);
    }
}

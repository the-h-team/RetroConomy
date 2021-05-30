/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.retro.command.CommandInformation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public enum RetroCommand implements CommandInformation {
	DEPOSIT, WITHDRAW, PAY, BALANCE, TOP, BUY, SELL, RETRO, ATM, BANK;


	@Override
	public @NotNull String getLabel() {
		switch (this) {
			case BUY:
				return "buy";
			case SELL:
				return "sell";
			case DEPOSIT:
				return "deposit";
			case WITHDRAW:
				return "withdraw";
			case PAY:
				return "pay";
			case RETRO:
				return "retro";
			case TOP:
				return "top";
			case BALANCE:
				return "balance";
			case ATM:
				return "atm";
			case BANK:
				return "bank";
			default:
				throw new IllegalStateException("");
		}
	}

	@Override
	public @NotNull String getDescription() {
		switch (this) {
			case ATM:
				return "Buy an atm for passive income generation.";
			case BUY:
				return "Buy a specified amount of an item";
			case SELL:
				return "Sell specified amounts of items.";
			case DEPOSIT:
				return "Deposit money into your wallet";
			case WITHDRAW:
				return "Withdraw money from your wallet";
			case PAY:
				return "Pay someone money";
			case BALANCE:
				return "Check your balance in your current world.";
			case TOP:
				return "Get the list of richest players.";
			case RETRO:
				return "Primary retry command";
			case BANK:
				return "Access bank commands.";
			default:
				throw new IllegalStateException("");
		}
	}

	@Override
	public @NotNull String getUsage() {
		switch (this) {
			case ATM:
				return "/atm buy";
			case BUY:
				return "/buy <itemName> <amount>";
			case SELL:
				return "/sell <hand, all>";
			case DEPOSIT:
				return "/deposit <amount>";
			case WITHDRAW:
				return "/withdraw <amount>";
			case PAY:
				return "/pay <playerName> <amount>";
			case TOP:
				return "/top | *optional [wallet, bank]";
			case BALANCE:
				return "/balance";
			case RETRO:
				return "/retro";
			case BANK:
				return "/bank";
			default:
				throw new IllegalStateException("");
		}
	}

	@Override
	public @NotNull String getPermission() {
		switch (this) {
			case ATM:
				return "retro.atm";
			case SELL:
				return "retro.sell";
			case BUY:
				return "retro.buy";
			case DEPOSIT:
				return "retro.deposit";
			case WITHDRAW:
				return "retro.withdraw";
			case PAY:
				return "retro.pay";
			case BALANCE:
				return "retro.balance";
			case TOP:
				return "retro.top";
			case RETRO:
				return "retro";
			case BANK:
				return "retro.bank";
			default:
				throw new IllegalStateException("");
		}
	}

	@Override
	public @NotNull List<String> getAliases() {
		switch (this) {
			case ATM:
			case SELL:
			case BUY:
			case TOP:
				return Collections.emptyList();
			case DEPOSIT:
				return Arrays.asList("d", "dp");
			case WITHDRAW:
				return Arrays.asList("w", "wd");
			case PAY:
				return Collections.singletonList("p");
			case BALANCE:
				return Collections.singletonList("bal");
			case RETRO:
				return Arrays.asList("retroconomy", "eco", "economy", "shop");
			case BANK:
				return Collections.singletonList("b");
			default:
				throw new IllegalStateException("");
		}
	}
}

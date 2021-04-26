/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.enterprise;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.account.PlayerWallet;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroWallet;
import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class EnterpriseWallet extends PlayerWallet {

	private final RetroWallet wallet;

	protected EnterpriseWallet(UUID player) {
		super(Bukkit.getOfflinePlayer(player));
		this.wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
	}

	protected EnterpriseWallet(OfflinePlayer player) {
		super(player);
		this.wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
	}

	@Override
	public EconomyAction setBalance(BigDecimal amount) {
		wallet.setBalance(amount);
		return new EconomyAction(getHolder(), true, "Adjusted account " + getHolder().friendlyName() + " to amount " + amount);
	}

	@Override
	public EconomyAction setBalance(BigDecimal amount, String world) {
		wallet.setBalance(amount, Bukkit.getWorld(world));
		return new EconomyAction(getHolder(), true, "Adjusted account " + getHolder().friendlyName() + " to amount " + amount + " in world " + world);
	}

	@Override
	public boolean exists() {
		return wallet != null;
	}

	@Override
	public boolean exists(String world) {
		return wallet != null;
	}

	@Override
	public @Nullable BigDecimal getBalance() {
		return wallet.getBalance();
	}

	@Override
	public @Nullable BigDecimal getBalance(String world) {
		return wallet.getBalance(Bukkit.getWorld(world));
	}

	@Override
	public boolean has(BigDecimal amount) {
		return wallet.has(amount.doubleValue());
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		return wallet.has(amount.doubleValue(), Bukkit.getWorld(world));
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		if (has(amount)) {
			wallet.withdraw(amount);
			return new EconomyAction(amount, getHolder(), true, "Withdrew " + amount.doubleValue());
		} else
		return new EconomyAction(amount, getHolder(), false, "Unable to withdraw " + amount.doubleValue());
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount, String world) {
		if (has(amount, world)) {
			wallet.withdraw(amount, Bukkit.getWorld(world));
			return new EconomyAction(amount, getHolder(), true, "Withdrew " + amount.doubleValue());
		} else
			return new EconomyAction(amount, getHolder(), false, "Unable to withdraw " + amount.doubleValue());
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		wallet.deposit(amount);
		return new EconomyAction(amount, getHolder(), true, "Deposited " + amount.doubleValue());
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		wallet.deposit(amount, Bukkit.getWorld(world));
		return new EconomyAction(amount, getHolder(), true, "Deposited " + amount.doubleValue());
	}
}

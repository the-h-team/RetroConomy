/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.FileType;
import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class WalletAccount implements RetroAccount {

	private static final long serialVersionUID = 193258191246087085L;
	private final HUID id;
	private final UUID owner;
	private final FileManager manager;
	private final boolean multiWorld = RetroConomy.getInstance().getManager().getMain().getConfig().getBoolean("Options.multi-world.enabled");
	private final String world = RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.multi-world.falsify");

	public WalletAccount(UUID owner, HUID id) {
		this.owner = owner;
		this.manager = FileType.ACCOUNT.get();
		this.id = id;
	}

	public HUID getId() {
		return id;
	}

	public OfflinePlayer getOwner() {
		return Bukkit.getOfflinePlayer(owner);
	}

	@Override
	public BigDecimal getBalance() {
		return BigDecimal.valueOf(manager.getConfig().getDouble("wallets." + owner.toString() + ".balance." + Bukkit.getWorld(this.world).getName()));
	}

	@Override
	public BigDecimal getBalance(World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		return BigDecimal.valueOf(manager.getConfig().getDouble("wallets." + owner.toString() + ".balance." + world.getName()));
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), amount.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double amount) {
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), amount);
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), amount.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), amount);
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public boolean has(double amount) {
		if (BigDecimal.valueOf(amount).compareTo(BigDecimal.ZERO) < 0)
			return false;
		return getBalance().doubleValue() >= amount;
	}

	@Override
	public boolean has(double amount, World world) {
		if (BigDecimal.valueOf(amount).compareTo(BigDecimal.ZERO) < 0)
			return false;
		return getBalance(world).doubleValue() >= amount;
	}

	@Override
	public boolean has(BigDecimal amount) {
		return has(amount.doubleValue());
	}

	@Override
	public boolean has(BigDecimal amount, World world) {
		return has(amount.doubleValue(), world);
	}

	@Override
	public RetroConomy.TransactionResult deposit(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().add(amount);
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult deposit(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).add(amount);
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().subtract(amount);
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).subtract(amount);
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

}

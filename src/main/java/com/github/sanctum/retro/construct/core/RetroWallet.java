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

public class RetroWallet {

	private final HUID id;
	private final UUID owner;
	private final FileManager manager;
	private final boolean multiWorld = RetroConomy.getInstance().getManager().getMain().getConfig().getBoolean("Options.multi-world.enabled");
	private final String world = RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.multi-world.falsify");

	public RetroWallet(UUID owner, HUID id) {
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

	public BigDecimal getBalance() {
		return BigDecimal.valueOf(manager.getConfig().getDouble("wallets." + owner.toString() + ".balance." + Bukkit.getWorlds().get(0).getName()));
	}

	public void setBalance(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), amount.doubleValue());
		manager.saveConfig();
	}

	public void setBalance(double amount) {
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), amount);
		manager.saveConfig();
	}

	public BigDecimal getBalance(World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		return BigDecimal.valueOf(manager.getConfig().getDouble("wallets." + owner.toString() + ".balance." + world.getName()));
	}

	public void setBalance(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), amount.doubleValue());
		manager.saveConfig();
	}

	public void setBalance(double amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), amount);
		manager.saveConfig();
	}

	public boolean has(double amount) {
		if (String.valueOf(amount).contains("-"))
			return false;
		return getBalance().doubleValue() >= amount;
	}

	public boolean has(double amount, World world) {
		if (String.valueOf(amount).contains("-"))
			return false;
		return getBalance(world).doubleValue() >= amount;
	}

	public void deposit(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().add(amount);
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void deposit(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).add(amount);
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void withdraw(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().subtract(amount);
		c.set("wallets." + owner.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void withdraw(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).subtract(amount);
		c.set("wallets." + owner.toString() + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
	}

}

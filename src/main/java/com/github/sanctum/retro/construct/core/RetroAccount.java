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
import com.github.sanctum.retro.construct.item.BankSlip;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RetroAccount {

	private final UUID uuid;
	private final HUID id;
	private final List<String> members;
	private final FileManager manager;
	private final boolean multiWorld = RetroConomy.getInstance().getManager().getMain().getConfig().getBoolean("Options.multi-world.enabled");
	private final String world = RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.multi-world.falsify");
	private UUID joint;

	public RetroAccount(UUID owner, UUID joint, HUID id, List<String> members) {
		this.uuid = owner;
		this.joint = joint;
		this.manager = FileType.ACCOUNT.get();
		this.members = new ArrayList<>(members);
		this.id = id;
		FileConfiguration c = manager.getConfig();
		if (!c.isDouble("accounts." + id.toString() + ".balance." + Bukkit.getWorlds().get(0).getName())) {
			setBalance(RetroConomy.getInstance().getManager().getMain().getConfig().getDouble("Options.accounts.starting-balance"));
		}
	}

	public UUID getOwner() {
		return this.uuid;
	}

	public void setOwner(UUID newOwner) {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString() + ".owner", newOwner.toString());
		manager.saveConfig();
		this.joint = newOwner;
	}

	public UUID getJointOwner() {
		return this.joint;
	}

	public void setJointOwner(UUID newJointOwner) {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString() + ".joint", newJointOwner.toString());
		manager.saveConfig();
		this.joint = newJointOwner;
	}

	public BigDecimal getBalance() {
		return BigDecimal.valueOf(manager.getConfig().getDouble("accounts." + id.toString() + ".balance." + Bukkit.getWorlds().get(0).getName()));
	}

	public void setBalance(BigDecimal balance) {
		manager.getConfig().set("accounts." + id.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), balance.doubleValue());
		manager.saveConfig();
	}

	public void setBalance(double balance) {
		manager.getConfig().set("accounts." + id.toString() + ".balance." + Bukkit.getWorlds().get(0).getName(), balance);
		manager.saveConfig();
	}

	public BigDecimal getBalance(World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		return BigDecimal.valueOf(manager.getConfig().getDouble("accounts." + id.toString() + ".balance." + world.getName()));
	}

	public void setBalance(BigDecimal balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getConfig().set("accounts." + id.toString() + ".balance." + world.getName(), balance.doubleValue());
		manager.saveConfig();
	}

	public void setBalance(double balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getConfig().set("accounts." + id.toString() + ".balance." + world.getName(), balance);
		manager.saveConfig();
	}

	public HUID getId() {
		return this.id;
	}

	public List<String> getMembers() {
		return this.members;
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

	public boolean addMember(UUID id) {
		if (!this.members.contains(id.toString())) {
			this.members.add(id.toString());
			return true;
		}
		return false;
	}

	public boolean removeMember(UUID id) {
		if (this.members.contains(id.toString())) {
			this.members.remove(id.toString());
			return true;
		}
		return false;
	}

	public void deposit(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorlds().get(0).getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void deposit(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void deposit(OfflinePlayer player, BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		BankSlip slip = BankSlip.from(player, amount, this, TransactionType.DEPOSIT);
		if (player.isOnline()) {
			Player target = player.getPlayer();
			target.getInventory().addItem(slip.toItem());
		}
		deposit(amount, world);
	}

	public void withdraw(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorlds().get(0).getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void withdraw(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
	}

	public void withdraw(OfflinePlayer player, BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		BankSlip slip = BankSlip.from(player, amount, this, TransactionType.WITHDRAW);
		if (player.isOnline()) {
			Player target = player.getPlayer();
			target.getInventory().addItem(slip.toItem());
		}
		withdraw(amount, world);
	}

	public BankSlip record(TransactionType type, OfflinePlayer player, BigDecimal amount, World world) {
		BankSlip slip;
		switch (type) {
			case WITHDRAW:
					if (!multiWorld)
						world = Bukkit.getWorld(this.world);
					if (has(amount.doubleValue(), world)) {
						RetroWallet wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
						if (wallet != null) {
							wallet.deposit(amount, world);
						}
						slip = BankSlip.from(player, amount, this, TransactionType.WITHDRAW);
						withdraw(amount, world);
						return slip;
					}
					return BankSlip.from(player, BigDecimal.ZERO, this, TransactionType.WITHDRAW);

			case DEPOSIT:
				RetroWallet wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
				if (wallet != null) {
					if (!multiWorld)
						world = Bukkit.getWorld(this.world);
					if (wallet.has(amount.doubleValue(), world)) {
						wallet.withdraw(amount, world);
						slip = BankSlip.from(player, amount, this, TransactionType.DEPOSIT);
						deposit(amount, world);
						return slip;
					}
				}
				return BankSlip.from(player, BigDecimal.ZERO, this, TransactionType.DEPOSIT);
			default:
				throw new IllegalStateException();
		}
	}

	public BankSlip record(TransactionType type, OfflinePlayer player, BigDecimal amount, BigDecimal tax, World world) {
		BankSlip slip;
		switch (type) {
			case WITHDRAW:
				if (!multiWorld)
					world = Bukkit.getWorld(this.world);
				if (has(amount.doubleValue(), world)) {
					RetroWallet wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
					if (wallet != null) {
						wallet.deposit(amount.subtract(tax), world);
					}
					slip = BankSlip.from(player, amount, tax, this, TransactionType.WITHDRAW);
					withdraw(amount, world);
					return slip;
				}
				return BankSlip.from(player, BigDecimal.ZERO, tax, this, TransactionType.WITHDRAW);

			case DEPOSIT:
				RetroWallet wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
				if (wallet != null) {
					if (!multiWorld)
						world = Bukkit.getWorld(this.world);
					if (wallet.has(amount.doubleValue(), world)) {
						wallet.withdraw(amount, world);
						slip = BankSlip.from(player, amount, tax, this, TransactionType.DEPOSIT);
						deposit(amount.subtract(tax), world);
						return slip;
					}
				}
				return BankSlip.from(player, BigDecimal.ZERO, tax, this, TransactionType.DEPOSIT);
			default:
				throw new IllegalStateException();
		}
	}

	public void remove() {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString(), null);
		manager.saveConfig();
	}


}

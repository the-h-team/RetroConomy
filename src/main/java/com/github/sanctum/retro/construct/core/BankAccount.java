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
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class BankAccount implements RetroAccount, Shareable {

	private static final long serialVersionUID = -356950482874065734L;
	private final UUID uuid;
	private final HUID id;
	private final List<String> members;
	private final FileManager manager;
	private final boolean multiWorld = RetroConomy.getInstance().getManager().getMain().getConfig().getBoolean("Options.multi-world.enabled");
	private final String world = RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.multi-world.falsify");
	private final DebitCard debitCard;
	private UUID joint;

	public BankAccount(UUID owner, UUID joint, HUID id, List<String> members) {
		this.uuid = owner;
		this.joint = joint;
		this.manager = FileType.ACCOUNT.get();
		this.members = new ArrayList<>(members);
		this.id = id;
		FileConfiguration c = manager.getConfig();
		if (!c.isDouble("accounts." + id.toString() + ".balance." + Bukkit.getWorld(this.world).getName())) {
			setBalance(RetroConomy.getInstance().getManager().getMain().getConfig().getDouble("Options.accounts.starting-balance"));
		}
		this.debitCard = new DebitCard(this);
	}

	@Override
	public UUID getOwner() {
		return this.uuid;
	}

	@Override
	public UUID getJointOwner() {
		return this.joint;
	}

	@Override
	public Savable getDebitCard() {
		return debitCard;
	}

	@Override
	public HUID getId() {
		return this.id;
	}

	@Override
	public List<UUID> getMembers() {
		return this.members.stream().map(UUID::fromString).collect(Collectors.toList());
	}

	@Override
	public BigDecimal getBalance() {
		return BigDecimal.valueOf(manager.getConfig().getDouble("accounts." + id.toString() + ".balance." + Bukkit.getWorld(this.world).getName()));
	}

	@Override
	public BigDecimal getBalance(World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		return BigDecimal.valueOf(manager.getConfig().getDouble("accounts." + id.toString() + ".balance." + world.getName()));
	}

	@Override
	public RetroConomy.TransactionResult setOwner(UUID newOwner) {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString() + ".owner", newOwner.toString());
		manager.saveConfig();
		this.joint = newOwner;
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setJointOwner(UUID newJointOwner) {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString() + ".joint", newJointOwner.toString());
		manager.saveConfig();
		this.joint = newJointOwner;
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal balance) {
		manager.getConfig().set("accounts." + id.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), balance.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double balance) {
		manager.getConfig().set("accounts." + id.toString() + ".balance." + Bukkit.getWorld(this.world).getName(), balance);
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getConfig().set("accounts." + id.toString() + ".balance." + world.getName(), balance.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getConfig().set("accounts." + id.toString() + ".balance." + world.getName(), balance);
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
	public RetroConomy.TransactionResult addMember(UUID id) {
		if (!this.members.contains(id.toString())) {
			this.members.add(id.toString());
			return RetroConomy.TransactionResult.SUCCESS;
		}
		return RetroConomy.TransactionResult.FAILED;
	}

	@Override
	public RetroConomy.TransactionResult removeMember(UUID id) {
		if (this.members.contains(id.toString())) {
			this.members.remove(id.toString());
			return RetroConomy.TransactionResult.SUCCESS;
		}
		return RetroConomy.TransactionResult.FAILED;
	}

	@Override
	public RetroConomy.TransactionResult test(UUID id) {
		return this.members.contains(id.toString()) ? RetroConomy.TransactionResult.SUCCESS : RetroConomy.TransactionResult.FAILED;
	}

	@Override
	public RetroConomy.TransactionResult deposit(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult deposit(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount) {
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance().subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		FileConfiguration c = manager.getConfig();
		BigDecimal after = getBalance(world).subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	public BankSlip record(TransactionType type, OfflinePlayer player, BigDecimal amount, World world) {
		BankSlip slip;
		switch (type) {
			case WITHDRAW:
					if (!multiWorld)
						world = Bukkit.getWorld(this.world);
					if (has(amount.doubleValue(), world)) {
						WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
						if (wallet != null) {
							wallet.deposit(amount, world);
						}
						slip = BankSlip.from(player, amount, this, TransactionType.WITHDRAW);
						withdraw(amount, world);
						return slip;
					}
					return BankSlip.from(player, BigDecimal.ZERO, this, TransactionType.WITHDRAW);

			case DEPOSIT:
				WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
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

	@Override
	public BankSlip record(ATM atm, TransactionType type, OfflinePlayer player, BigDecimal amount) {
		BankSlip slip;
		World world = atm.getLocation().getWorld();
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		switch (type) {
			case WITHDRAW:
				if (has(amount.doubleValue(), world)) {
					WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
					if (wallet != null) {
						wallet.deposit(amount.subtract(atm.getTax(player)), world);
					}
					slip = BankSlip.from(player, amount, atm.getTax(player), this, TransactionType.WITHDRAW);
					withdraw(amount, world);
					return atm.take(slip);
				}
				return atm.take(BankSlip.from(player, BigDecimal.ZERO, atm.getTax(player), this, TransactionType.WITHDRAW));

			case DEPOSIT:
				WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
				if (wallet != null) {
					if (wallet.has(amount.doubleValue(), world)) {
						wallet.withdraw(amount, world);
						slip = BankSlip.from(player, amount, atm.getTax(player), this, TransactionType.DEPOSIT);
						deposit(amount.subtract(atm.getTax(player)), world);
						return atm.take(slip);
					}
				}
				return atm.take(BankSlip.from(player, BigDecimal.ZERO, atm.getTax(player), this, TransactionType.DEPOSIT));
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public RetroConomy.TransactionResult remove() {
		FileConfiguration c = manager.getConfig();
		c.set("accounts." + id.toString(), null);
		manager.saveConfig();
		return RetroConomy.TransactionResult.SUCCESS;
	}


}
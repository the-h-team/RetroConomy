/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.data.Configurable;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.RetroAccount;
import com.github.sanctum.retro.api.Savable;
import com.github.sanctum.retro.api.Shareable;
import com.github.sanctum.retro.util.FileReader;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class BankAccount implements RetroAccount, Shareable {

	private static final long serialVersionUID = -356950482874065734L;
	private final UUID uuid;
	private final HUID id;
	private final List<String> members;
	private final FileManager manager;
	private final boolean multiWorld = RetroConomy.getInstance().getManager().getMain().getRoot().getBoolean("Options.multi-world.enabled");
	private final String world = RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.multi-world.falsify");
	private final DebitCard debitCard;
	private UUID joint;

	public BankAccount(UUID owner, UUID joint, HUID id, List<String> members) {
		this.uuid = owner;
		this.joint = joint;
		this.manager = FileReader.ACCOUNT.get();
		this.members = new ArrayList<>(members);
		this.id = id;
		Configurable c = manager.getRoot();
		if (!c.isDouble("accounts." + id.toString() + ".balance." + this.world)) {
			setBalance(RetroConomy.getInstance().getManager().getMain().getRoot().getDouble("Options.accounts.starting-balance"));
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
	public boolean isPrimary(UUID member) {
		Configurable c = manager.getRoot();
		return c.isString("wallets." + uuid.toString() + ".primary") && c.getString("wallets." + uuid + ".primary").equals(getId().toString());
	}

	@Override
	public void setPrimary(UUID member, boolean primary) {
		Configurable c = manager.getRoot();
		if (primary) {
			c.set("wallets." + uuid.toString() + ".primary", getId().toString());
		} else {
			c.set("wallets." + uuid.toString() + ".primary", null);
		}
		c.save();

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
		return BigDecimal.valueOf(manager.getRoot().getDouble("accounts." + id.toString() + ".balance." + this.world));
	}

	@Override
	public BigDecimal getBalance(World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		return BigDecimal.valueOf(manager.getRoot().getDouble("accounts." + id.toString() + ".balance." + world.getName()));
	}

	@Override
	public RetroConomy.TransactionResult setOwner(UUID newOwner) {
		Configurable c = manager.getRoot();
		c.set("accounts." + id.toString() + ".owner", newOwner.toString());
		c.save();
		this.joint = newOwner;
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setJointOwner(UUID newJointOwner) {
		Configurable c = manager.getRoot();
		c.set("accounts." + id.toString() + ".joint", newJointOwner.toString());
		c.save();
		this.joint = newJointOwner;
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal balance) {
		manager.getRoot().set("accounts." + id.toString() + ".owner", getOwner().toString());
		if (getJointOwner() != null) {
			manager.getRoot().set("accounts." + id + ".joint", getJointOwner().toString());
		}
		if (!getMembers().isEmpty()) {
			manager.getRoot().set("accounts." + id + ".members", getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
		}
		manager.getRoot().set("accounts." + id + ".balance." + this.world, balance.doubleValue());
		manager.getRoot().save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double balance) {
		manager.getRoot().set("accounts." + id.toString() + ".owner", getOwner().toString());
		if (getJointOwner() != null) {
			manager.getRoot().set("accounts." + id + ".joint", getJointOwner().toString());
		}
		if (!getMembers().isEmpty()) {
			manager.getRoot().set("accounts." + id + ".members", getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
		}
		manager.getRoot().set("accounts." + id + ".balance." + this.world, balance);
		manager.getRoot().save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(BigDecimal balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getRoot().set("accounts." + id.toString() + ".owner", getOwner().toString());
		if (getJointOwner() != null) {
			manager.getRoot().set("accounts." + id + ".joint", getJointOwner().toString());
		}
		if (!getMembers().isEmpty()) {
			manager.getRoot().set("accounts." + id + ".members", getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
		}
		manager.getRoot().set("accounts." + id + ".balance." + world.getName(), balance.doubleValue());
		manager.getRoot().save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult setBalance(double balance, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		manager.getRoot().set("accounts." + id.toString() + ".owner", getOwner().toString());
		if (getJointOwner() != null) {
			manager.getRoot().set("accounts." + id + ".joint", getJointOwner().toString());
		}
		if (!getMembers().isEmpty()) {
			manager.getRoot().set("accounts." + id + ".members", getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
		}
		manager.getRoot().set("accounts." + id + ".balance." + world.getName(), balance);
		manager.getRoot().save();
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
		Configurable c = manager.getRoot();
		BigDecimal after = getBalance().add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		c.save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult deposit(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		Configurable c = manager.getRoot();
		BigDecimal after = getBalance(world).add(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		c.save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount) {
		Configurable c = manager.getRoot();
		BigDecimal after = getBalance().subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + Bukkit.getWorld(this.world).getName(), after.doubleValue());
		c.save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.TransactionResult withdraw(BigDecimal amount, World world) {
		if (!multiWorld)
			world = Bukkit.getWorld(this.world);
		Configurable c = manager.getRoot();
		BigDecimal after = getBalance(world).subtract(amount);
		c.set("accounts." + id.toString() + "." + ".balance." + world.getName(), after.doubleValue());
		c.save();
		return RetroConomy.TransactionResult.SUCCESS;
	}

	public TransactionStatement record(TransactionType type, OfflinePlayer player, BigDecimal amount, World world) {
		TransactionStatement slip;
		switch (type) {
			case WITHDRAW:
				if (!multiWorld)
					world = Bukkit.getWorld(this.world);
				if (has(amount.doubleValue(), world)) {
					WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
					if (wallet != null) {
						wallet.deposit(amount, world);
					}
					slip = TransactionStatement.from(player, amount, this, TransactionType.WITHDRAW);
					withdraw(amount, world);
					return slip;
				}
				return TransactionStatement.from(player, BigDecimal.ZERO, this, TransactionType.WITHDRAW);

			case DEPOSIT:
				WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
				if (wallet != null) {
					if (!multiWorld)
						world = Bukkit.getWorld(this.world);
					if (wallet.has(amount.doubleValue(), world)) {
						wallet.withdraw(amount, world);
						slip = TransactionStatement.from(player, amount, this, TransactionType.DEPOSIT);
						deposit(amount, world);
						return slip;
					}
				}
				return TransactionStatement.from(player, BigDecimal.ZERO, this, TransactionType.DEPOSIT);
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public TransactionStatement record(Shop atm, TransactionType type, OfflinePlayer player, BigDecimal amount) {
		TransactionStatement slip;
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
					slip = TransactionStatement.from(player, amount, atm.getTax(player), this, TransactionType.WITHDRAW);
					withdraw(amount, world);
					return atm.take(slip);
				}
				return atm.take(TransactionStatement.from(player, BigDecimal.ZERO, atm.getTax(player), this, TransactionType.WITHDRAW));

			case DEPOSIT:
				WalletAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);
				if (wallet != null) {
					if (wallet.has(amount.doubleValue(), world)) {
						wallet.withdraw(amount, world);
						slip = TransactionStatement.from(player, amount, atm.getTax(player), this, TransactionType.DEPOSIT);
						deposit(amount.subtract(atm.getTax(player)), world);
						return atm.take(slip);
					}
				}
				return atm.take(TransactionStatement.from(player, BigDecimal.ZERO, atm.getTax(player), this, TransactionType.DEPOSIT));
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public RetroConomy.TransactionResult remove() {
		Configurable c = manager.getRoot();
		c.set("accounts." + id.toString(), null);
		c.save();
		return RetroConomy.TransactionResult.SUCCESS;
	}


}

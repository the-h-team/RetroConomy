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
import com.github.sanctum.economy.construct.account.Account;
import com.github.sanctum.economy.construct.account.permissive.AccountType;
import com.github.sanctum.economy.construct.entity.PlayerEconomyEntityBase;
import com.github.sanctum.economy.construct.entity.types.PlayerEntity;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.BankAccount;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class EnterpriseAccount extends Account {

	private final BankAccount account;

	protected EnterpriseAccount(UUID holder, PlayerEconomyEntityBase... members) {
		super(AccountType.BANK_ACCOUNT, new PlayerEntity(Bukkit.getOfflinePlayer(holder)), members);
		account = RetroConomy.getInstance().getManager().getAccount(holder).orElse(null);
	}

	protected EnterpriseAccount(OfflinePlayer holder, PlayerEconomyEntityBase... members) {
		super(AccountType.BANK_ACCOUNT, new PlayerEntity(holder), members);
		account = RetroConomy.getInstance().getManager().getAccount(holder).orElse(null);
	}

	@Override
	public List<String> getMembers() {
		return account.getMembers().stream().map(UUID::toString).collect(Collectors.toList());
	}

	@Override
	public EconomyAction isOwner(String name) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isOwner(String name, String world) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isOwner(OfflinePlayer player) {
		if (account.getOwner().equals(player.getUniqueId())) {
			return new EconomyAction(holder, true, "Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-owner detected.");
	}

	@Override
	public EconomyAction isOwner(OfflinePlayer player, String world) {
		if (account.getOwner().equals(player.getUniqueId())) {
			return new EconomyAction(holder, true, "Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-owner detected.");
	}

	@Override
	public EconomyAction isOwner(UUID uuid) {
		if (account.getOwner().equals(uuid)) {
			return new EconomyAction(holder, true, "Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-owner detected.");
	}

	@Override
	public EconomyAction isOwner(UUID uuid, String world) {
		if (account.getOwner().equals(uuid)) {
			return new EconomyAction(holder, true, "Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-owner detected.");
	}

	@Override
	public EconomyAction isJointOwner(String name) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isJointOwner(String name, String world) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isJointOwner(OfflinePlayer player) {
		if (account.getJointOwner().equals(player.getUniqueId())) {
			return new EconomyAction(holder, true, "Joint Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-Joint-owner detected.");
	}

	@Override
	public EconomyAction isJointOwner(OfflinePlayer player, String world) {
		if (account.getOwner().equals(player.getUniqueId())) {
			return new EconomyAction(holder, true, "Joint Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-Joint-owner detected.");
	}

	@Override
	public EconomyAction isJointOwner(UUID uuid) {
		if (account.getOwner().equals(uuid)) {
			return new EconomyAction(holder, true, "Joint Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-Joint-owner detected.");
	}

	@Override
	public EconomyAction isJointOwner(UUID uuid, String world) {
		if (account.getOwner().equals(uuid)) {
			return new EconomyAction(holder, true, "Joint Owner detected.");
		}
		return new EconomyAction(holder, false, "Non-Joint-owner detected.");
	}

	@Override
	public EconomyAction isMember(String name) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isMember(String name, String world) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction isMember(OfflinePlayer player) {
		if (getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Member detected.");
		}
		return new EconomyAction(holder, false, "Non-member detected.");
	}

	@Override
	public EconomyAction isMember(OfflinePlayer player, String world) {
		if (getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Member detected.");
		}
		return new EconomyAction(holder, false, "Non-member detected.");
	}

	@Override
	public EconomyAction isMember(UUID uuid) {
		if (getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Member detected.");
		}
		return new EconomyAction(holder, false, "Non-member detected.");
	}

	@Override
	public EconomyAction isMember(UUID uuid, String world) {
		if (getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Member detected.");
		}
		return new EconomyAction(holder, false, "Non-member detected.");
	}

	@Override
	public EconomyAction addMember(String name) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction addMember(String name, String world) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player) {
		if (!getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Adding new member " + player.getName());
		}
		return new EconomyAction(holder, false, "User is already a member.");
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player, String world) {
		if (!getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Adding new member " + player.getName());
		}
		return new EconomyAction(holder, false, "User is already a member.");
	}

	@Override
	public EconomyAction addMember(UUID uuid) {
		if (!getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Adding new member " + uuid);
		}
		return new EconomyAction(holder, false, "User is already a member.");
	}

	@Override
	public EconomyAction addMember(UUID uuid, String world) {
		if (!getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Adding new member " + uuid);
		}
		return new EconomyAction(holder, false, "User is already a member.");
	}

	@Override
	public EconomyAction removeMember(String name) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction removeMember(String name, String world) {
		return new EconomyAction(holder, false, "Only UUID format accepted.");
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player) {
		if (getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Removing old member " + player.getName());
		}
		return new EconomyAction(holder, false, "User is not a member.");
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player, String world) {
		if (getMembers().contains(player.getUniqueId().toString())) {
			return new EconomyAction(holder, true, "Removing old member " + player.getName());
		}
		return new EconomyAction(holder, false, "User is not a member.");
	}

	@Override
	public EconomyAction removeMember(UUID uuid) {
		if (getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Removing old member " + uuid);
		}
		return new EconomyAction(holder, false, "User is not a member.");
	}

	@Override
	public EconomyAction removeMember(UUID uuid, String world) {
		if (getMembers().contains(uuid.toString())) {
			return new EconomyAction(holder, true, "Removing old member " + uuid);
		}
		return new EconomyAction(holder, false, "User is not a member.");
	}

	@Override
	public EconomyAction setBalance(BigDecimal amount) {
		account.setBalance(amount);
		return new EconomyAction(getHolder(), true, "Adjusted account " + getId() + " to amount " + amount);
	}

	@Override
	public EconomyAction setBalance(BigDecimal amount, String world) {
		account.setBalance(amount, Bukkit.getWorld(world));
		return new EconomyAction(getHolder(), true, "Adjusted account " + getId() + " to amount " + amount + " in world " + world);
	}

	@Override
	public boolean exists() {
		return account != null;
	}

	@Override
	public boolean exists(String world) {
		return account != null;
	}

	@Override
	public @Nullable BigDecimal getBalance() {
		return account.getBalance();
	}

	@Override
	public @Nullable BigDecimal getBalance(String world) {
		return account.getBalance(Bukkit.getWorld(world));
	}

	@Override
	public boolean has(BigDecimal amount) {
		return account.has(amount.doubleValue());
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		return account.has(amount.doubleValue(), Bukkit.getWorld(world));
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		if (has(amount)) {
			account.withdraw(amount);
		}
		return new EconomyAction(amount, getHolder(), has(amount), has(amount) ? "Successfully withdrew " + amount.doubleValue() : "Could not withdraw " + amount.doubleValue());
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount, String world) {
		if (has(amount, world)) {
			account.withdraw(amount, Bukkit.getWorld(world));
		}
		return new EconomyAction(amount, getHolder(), has(amount, world), has(amount, world) ? "Successfully withdrew " + amount.doubleValue() : "Could not withdraw " + amount.doubleValue());
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		account.deposit(amount);
		return new EconomyAction(amount, getHolder(), true, "Successfully deposited " + amount.doubleValue());
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		account.deposit(amount, Bukkit.getWorld(world));
		return new EconomyAction(amount, getHolder(), true, "Successfully deposited " + amount.doubleValue() + " in world " + world);
	}
}

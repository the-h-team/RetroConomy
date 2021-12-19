/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.api;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.Shop;
import com.github.sanctum.retro.construct.core.TransactionStatement;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public interface Shareable extends RetroAccount {

	UUID getOwner();

	UUID getJointOwner();

	boolean isPrimary(UUID member);

	void setPrimary(UUID member, boolean primary);

	RetroConomy.TransactionResult setOwner(UUID newOwner);

	RetroConomy.TransactionResult setJointOwner(UUID newJointOwner);

	RetroConomy.TransactionResult addMember(UUID id);

	RetroConomy.TransactionResult removeMember(UUID id);

	RetroConomy.TransactionResult test(UUID id);

	RetroConomy.TransactionResult remove();

	TransactionStatement record(Shop atm, TransactionType type, OfflinePlayer player, BigDecimal amount);

	Savable getDebitCard();

	List<UUID> getMembers();

}

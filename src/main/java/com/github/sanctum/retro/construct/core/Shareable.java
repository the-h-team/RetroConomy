package com.github.sanctum.retro.construct.core;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public interface Shareable extends RetroAccount{

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

	TransactionStatement record(ATM atm, TransactionType type, OfflinePlayer player, BigDecimal amount);

	Savable getDebitCard();

	List<UUID> getMembers();

}

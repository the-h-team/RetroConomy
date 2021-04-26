/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.item;

import com.github.sanctum.retro.RetroConomy;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface Modifiable {

	RetroConomy.PlayerTransactionResult adjustMultiplier(Player who, double multiplier);

	RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result);

	RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result, int count);

	RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result);

	RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result, int count);

	enum TransactionResult {
		Buy, Sell
	}

}

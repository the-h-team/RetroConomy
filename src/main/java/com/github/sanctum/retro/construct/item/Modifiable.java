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

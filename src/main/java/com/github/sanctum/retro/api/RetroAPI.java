package com.github.sanctum.retro.api;

import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.RetroManager;
import com.github.sanctum.retro.construct.item.Currency;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RetroAPI {

	RetroManager getManager();

	int getTotalAmount(Player p, Currency c);

	RetroConomy.PlayerTransactionResult currencyRemoval(Player p, Currency c, int amount);

	RetroConomy.PlayerTransactionResult itemRemoval(Player p, ItemStack item, int amount);

	FileList getFiles();

}

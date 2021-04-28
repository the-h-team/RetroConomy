/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.api;

import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.RetroManager;
import com.github.sanctum.retro.construct.core.Currency;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RetroAPI {

	RetroManager getManager();

	int currencyTotal(Player p, Currency c);

	int itemStackTotal(Player p, ItemStack c);

	RetroConomy.PlayerTransactionResult currencyRemoval(Player p, Currency c, int amount);

	RetroConomy.PlayerTransactionResult itemRemoval(Player p, ItemStack item, int amount);

	FileList getFiles();

}

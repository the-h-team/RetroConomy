/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.util;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.Currency;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public enum CurrencyType {
	DOLLAR, CHANGE, ALT;

	public static Optional<Currency> match(ItemStack item) {
		return RetroConomy.getInstance().getManager().getAcceptableCurrencies().filter(c -> c.toItem().isSimilar(item)).findFirst();
	}

}

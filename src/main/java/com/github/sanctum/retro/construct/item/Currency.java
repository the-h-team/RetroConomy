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
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public class Currency {

	private final ItemStack item;
	private final CurrencyType type;
	private final double worth;

	public Currency(ItemStack item, CurrencyType type, double worth) {
		this.item = item;
		this.type = type;
		this.worth = worth;
	}

	public static Optional<Currency> match(ItemStack item) {
		return RetroConomy.getInstance().getManager().getCurrencies().filter(c -> c.getItem().isSimilar(item)).findFirst();
	}

	public ItemStack getItem() {
		return item;
	}

	public CurrencyType getType() {
		return type;
	}

	public double getWorth() {
		return worth;
	}

}

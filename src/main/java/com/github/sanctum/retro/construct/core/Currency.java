/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.util.CurrencyType;
import org.bukkit.inventory.ItemStack;

public class Currency implements Savable{

	private static final long serialVersionUID = -6267223132241350636L;
	private final ItemStack item;
	private final CurrencyType type;
	private final double worth;
	private final HUID id;

	public Currency(ItemStack item, CurrencyType type, double worth) {
		this.item = item;
		this.type = type;
		this.worth = worth;
		this.id = HUID.randomID();
	}

	@Override
	public HUID id() {
		return this.id;
	}

	@Override
	public ItemStack toItem() {
		return item;
	}

	public CurrencyType getType() {
		return type;
	}

	public double getWorth() {
		return worth;
	}

}

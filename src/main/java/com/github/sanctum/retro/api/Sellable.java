/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.api;

import org.bukkit.inventory.ItemStack;

public interface Sellable {

	ItemStack getItem();

	double getBasePrice();

	double getBuyPrice(int amount);

	double getSellPrice(int amount);

	double getCeiling();

	double getFloor();

	double getMultiplier();

}

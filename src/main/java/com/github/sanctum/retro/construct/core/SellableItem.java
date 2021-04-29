/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import org.bukkit.inventory.ItemStack;

public interface SellableItem{

	ItemStack getItem();

	double getPrice();

	double getCeiling();

	double getFloor();

	double getMultiplier();

}

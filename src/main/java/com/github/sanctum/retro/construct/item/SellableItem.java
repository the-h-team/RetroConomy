package com.github.sanctum.retro.construct.item;

import org.bukkit.inventory.ItemStack;

public interface SellableItem{

	ItemStack getItem();

	double getPrice();

	double getMultiplier();

}

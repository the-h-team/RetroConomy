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
import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public interface Savable extends Serializable {

	HUID id();

	ItemStack toItem();

}

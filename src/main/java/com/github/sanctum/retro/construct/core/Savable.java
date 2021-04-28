package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public interface Savable extends Serializable {

	HUID id();

	ItemStack toItem();

}

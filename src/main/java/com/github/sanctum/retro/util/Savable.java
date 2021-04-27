package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.library.HUID;
import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public abstract class Savable implements Serializable {
	private static final long serialVersionUID = 7644813080097822567L;

	public abstract HUID id();

	public abstract ItemStack get();

}

package com.github.sanctum.retro.construct.trade;

import com.github.sanctum.retro.construct.trade.exceptions.TraderItemException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A trader that can be given items and from which items may be taken.
 */
public interface ItemTrader extends Trader {
    void giveItem(@NotNull ItemStack item) throws TraderItemException;
    void takeItem(@NotNull ItemStack item) throws TraderItemException;
    void giveItems(@NotNull Iterable<ItemStack> items) throws TraderItemException;
    void takeItems(@NotNull Iterable<ItemStack> items) throws TraderItemException;
}

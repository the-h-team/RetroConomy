package com.github.sanctum.retro.construct.trade.exceptions;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TraderItemException extends TraderException {
    private final ImmutableList<@NotNull ItemStack> items;

    public TraderItemException(@NotNull ItemStack item) {
        this.items = ImmutableList.of(item);
    }
    public TraderItemException(@NotNull Iterable<@NotNull ItemStack> items) {
        this.items = ImmutableList.copyOf(items);
    }

    public ImmutableList<ItemStack> getItems() {
        return items;
    }
}

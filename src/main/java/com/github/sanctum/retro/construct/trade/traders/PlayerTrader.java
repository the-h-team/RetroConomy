package com.github.sanctum.retro.construct.trade.traders;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.construct.trade.ItemTrader;
import com.github.sanctum.retro.construct.trade.MoneyTrader;
import com.github.sanctum.retro.construct.trade.exceptions.TraderItemException;
import com.github.sanctum.retro.construct.trade.exceptions.TraderMoneyException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PlayerTrader implements MoneyTrader, ItemTrader {
    private final Player player;

    public PlayerTrader(Player player) {
        this.player = player;
    }

    @Override
    public void giveItem(@NotNull ItemStack item) throws TraderItemException {
        final Collection<ItemStack> missedItems = player.getInventory().addItem(item).values();
        if (missedItems.isEmpty()) return;
        throw new TraderItemException(missedItems);
    }

    @Override
    public void takeItem(@NotNull ItemStack item) throws TraderItemException {
        final Collection<ItemStack> missedItems = player.getInventory().removeItem(item).values();
        if (missedItems.isEmpty()) return;
        throw new TraderItemException(missedItems);
    }

    @Override
    public void giveItems(@NotNull Iterable<ItemStack> items) throws TraderItemException {
        final List<ItemStack> stacks;
        if (items instanceof List) {
            stacks = (List<ItemStack>) items;
        } else {
            stacks = new ArrayList<>();
            items.forEach(stacks::add);
        }
        final Collection<ItemStack> missedItems = player.getInventory().addItem(stacks.toArray(new ItemStack[0])).values();
        if (missedItems.isEmpty()) return;
        throw new TraderItemException(missedItems);
    }

    @Override
    public void takeItems(@NotNull Iterable<ItemStack> items) throws TraderItemException {
        final List<ItemStack> stacks;
        if (items instanceof List) {
            stacks = (List<ItemStack>) items;
        } else {
            stacks = new ArrayList<>();
            items.forEach(stacks::add);
        }
        final Collection<ItemStack> missedItems = player.getInventory().removeItem(stacks.toArray(new ItemStack[0])).values();
        if (missedItems.isEmpty()) return;
        throw new TraderItemException(missedItems);
    }

    @Override
    public void giveMoney(@NotNull BigDecimal amount) throws TraderMoneyException {
        final Optional<WalletAccount> wallet = RetroConomy.getInstance().getManager().getWallet(player);
        if (wallet.isPresent()) {
            if (wallet.get().deposit(amount).success()) {
                return;
            }
        }
        throw new TraderMoneyException(amount, "This player could not accept wallet funds");
    }

    @Override
    public void takeMoney(@NotNull BigDecimal amount) throws TraderMoneyException {
        final Optional<WalletAccount> wallet = RetroConomy.getInstance().getManager().getWallet(player);
        if (wallet.isPresent()) {
            if (wallet.get().withdraw(amount).success()) {
                return;
            }
        }
        throw new TraderMoneyException(amount, "This player does not have sufficient wallet funds");
    }

    @Override
    public @NotNull String getName() {
        return player.getDisplayName();
    }
}

/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.command;

import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.api.Modifiable;
import com.github.sanctum.retro.api.Sellable;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuyCommand extends CommandOrientation {

	final SimpleTabCompletion complete = SimpleTabCompletion.empty();

	public BuyCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return complete.fillArgs(args)
				.then(TabCompletionIndex.ONE, RetroConomy.getInstance().getManager().getInventory().stream().map(Sellable::getItem).map(ItemStack::getType).map(mat -> mat.name().toLowerCase().replace("_", "")).collect(Collectors.toList()))
				.then(TabCompletionIndex.TWO, RetroConomy.getInstance().getManager().getInventory().stream().map(Sellable::getItem).map(ItemStack::getType).map(mat -> mat.name().toLowerCase().replace("_", "")).collect(Collectors.toList()))
				.get();

	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {
			if (RetroConomy.getInstance().getManager().getWallet(player).isPresent()) {
				if (args.length == 0) {
					sendUsage(player);
					return;
				}

				if (args.length == 1) {
					Material request = Items.findMaterial(args[0]);
					if (request != null) {
						ItemDemand item = RetroConomy.getInstance().getManager().getDemand(request).filter(i -> i instanceof SystemItem).orElse(null);
						if (item != null) {
							if (item.invoke(player.getUniqueId(), Modifiable.TransactionResult.Buy).isTransactionSuccess()) {
								double price = item.getBuyPrice(1);
								String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-bought")).bought(request.name().toLowerCase(), price, price).replace("{AMOUNT}", "1");
								sendMessage(player, format);
							} else {
								sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
							}
						} else {
							// not for sale
							sendMessage(player, "&cThis item is not for sale.");
						}
					} else {
						sendMessage(player, "&cThis item was not found.");
					}
					return;
				}

				if (args.length == 2) {
					try {
						int amount = Integer.parseInt(args[0]);
						Material request = Items.findMaterial(args[1]);
						if (request != null) {
							ItemDemand item = RetroConomy.getInstance().getManager().getDemand(request).filter(i -> i instanceof SystemItem).orElse(null);
							if (item != null) {
								if (item.invoke(player.getUniqueId(), Modifiable.TransactionResult.Buy, amount).isTransactionSuccess()) {
									double price = item.getBuyPrice(amount);
									double each = item.getBuyPrice(1);
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-bought")).bought(request.name().toLowerCase(), price, each).replace("{AMOUNT}", amount + "");

									sendMessage(player, format);
								} else {
									sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
								}
							} else {
								// not for sale
								sendMessage(player, "&cThis item is not for sale.");
							}
						} else {
							sendMessage(player, "&cThis item was not found.");
						}
					} catch (NumberFormatException e) {
						// amount invalid
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
					}
				}
			}
		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

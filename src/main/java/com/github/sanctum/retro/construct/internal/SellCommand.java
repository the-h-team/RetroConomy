/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.item.Modifiable;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.PlaceHolder;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SellCommand extends CommandOrientation {

	public SellCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
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
					if (args[0].equalsIgnoreCase("all")) {
						for (ItemStack item : player.getInventory().getContents()) {
							RetroConomy.getInstance().getManager().getDemand(item).ifPresent(i -> {
								final int amount = item.getAmount();
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
									double each = (i.getPrice() * i.getMultiplier()) * 1 / 2;
									double total = (i.getPrice() * i.getMultiplier()) * amount / 2;
									String format = PlaceHolder.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
									sendMessage(player, format);
								} else {
									sendMessage(player, "&cNo wallet account was found to transfer the money to. Speak with an administrator.");
								}
							});
						}
						return;
					}
					if (args[0].equalsIgnoreCase("hand")) {
						ItemStack item = player.getInventory().getItemInMainHand();
						RetroConomy.getInstance().getManager().getDemand(item).ifPresent(i -> {
							final int amount = item.getAmount();
							if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
								double each = (i.getPrice() * i.getMultiplier()) * 1 / 2;
								double total = (i.getPrice() * i.getMultiplier()) * amount / 2;
								String format = PlaceHolder.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
								sendMessage(player, format);
							} else {
								sendMessage(player, "&cNo wallet account was found to transfer the money to. Speak with an administrator.");
							}
						});
						return;
					}
					try {
						int amount = Integer.parseInt(args[0]);
						ItemStack it = player.getInventory().getItemInMainHand();
						if (it != null) {
							RetroConomy.getInstance().getManager().getDemand(it).ifPresent(i -> {
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, amount).isTransactionSuccess()) {
									double each = (i.getPrice() * i.getMultiplier()) * 1 / 2;
									double total = (i.getPrice() * i.getMultiplier()) * amount / 2;
									String format = PlaceHolder.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
									sendMessage(player, format);
								} else {
									sendMessage(player, "&cInvalid request received. Is it possible you tried selling to much?");
								}
							});
						}
					} catch (NumberFormatException e) {
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
					}
					return;
				}

				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("hand")) {
						try {
							int amount = Integer.parseInt(args[1]);
							ItemStack item = player.getInventory().getItemInMainHand();
							RetroConomy.getInstance().getManager().getDemand(item).ifPresent(i -> {
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
									double each = (i.getPrice() * i.getMultiplier()) * 1 / 2;
									double total = (i.getPrice() * i.getMultiplier()) * amount / 2;
									String format = PlaceHolder.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
									sendMessage(player, format);
								} else {
									sendMessage(player, "&cInvalid request received. Is it possible you tried selling to much?");
								}
							});
						} catch (NumberFormatException e) {
							sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[1]));
						}
						return;
					}
					try {
						int amount = Integer.parseInt(args[0]);
						Material request = Items.getMaterial(args[1]);
						if (request != null) {
							RetroConomy.getInstance().getManager().getDemand(request).ifPresent(i -> {
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, amount).isTransactionSuccess()) {
									double each = (i.getPrice() * i.getMultiplier()) * 1 / 2;
									double total = (i.getPrice() * i.getMultiplier()) * amount / 2;
									String format = PlaceHolder.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
									sendMessage(player, format);
								} else {
									sendMessage(player, "&cInvalid request received. Is it possible you tried selling to much?");
								}
							});
						}
					} catch (NumberFormatException e) {
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

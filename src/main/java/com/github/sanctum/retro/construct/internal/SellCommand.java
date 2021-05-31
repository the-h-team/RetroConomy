/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.labyrinth.formatting.TabCompletion;
import com.github.sanctum.labyrinth.formatting.TabCompletionBuilder;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.ItemDemand;
import com.github.sanctum.retro.construct.core.MarketItem;
import com.github.sanctum.retro.construct.core.Modifiable;
import com.github.sanctum.retro.construct.core.SellableItem;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SellCommand extends CommandOrientation {

	public SellCommand(@NotNull CommandInformation information) {
		super(information);
	}

	private final TabCompletionBuilder builder = TabCompletion.build(getLabel());

	@Override
	public @NotNull List<String> complete(Player p, String[] args) {
		return builder.forArgs(args)
				.level(1)
				.completeAt(getLabel())
				.filter(() -> RetroConomy.getInstance().getManager().getMarket().map(SellableItem::getItem).map(ItemStack::getType).map(mat -> mat.name().toLowerCase().replace("_", "")).collect(Collectors.toList()))
				.collect()
				.level(2)
				.completeAt(getLabel())
				.filter(() -> RetroConomy.getInstance().getManager().getMarket().map(SellableItem::getItem).map(ItemStack::getType).map(mat -> mat.name().toLowerCase().replace("_", "")).collect(Collectors.toList()))
				.collect()
				.get(args.length);
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
							RetroConomy.getInstance().getManager().getDemand(item).filter(i -> i instanceof SystemItem).ifPresent(i -> {
								final int amount = item.getAmount();
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
									double each = i.getSellPrice(1);
									double total = i.getSellPrice(amount);
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
									sendMessage(player, format);
								} else {
									sendMessage(player, "&cNo wallet account was found to transfer the money to. Speak with an administrator.");
								}
							});
						}
						return;
					}
					if (args[0].equalsIgnoreCase("market")) {
						ItemStack item = player.getInventory().getItemInMainHand();
						Optional<ItemDemand> demand = RetroConomy.getInstance().getManager().getDemand(item).filter(i -> i instanceof MarketItem);

						if (demand.isPresent()) {
							MarketItem m = (MarketItem) demand.get();
							m.setAmount(m.getAmount() + item.getAmount());
							item.setAmount(0);
							ItemDemand.GUI.bid(player, MarketItem.getCategory(m.getItem().getType()));
						}
						return;
					}
					if (args[0].equalsIgnoreCase("hand")) {
						ItemStack item = player.getInventory().getItemInMainHand();
						RetroConomy.getInstance().getManager().getDemand(item).filter(i -> i instanceof SystemItem).ifPresent(i -> {
							final int amount = item.getAmount();
							if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
								double each = i.getSellPrice(1);
								double total = i.getSellPrice(amount);
								String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
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
						RetroConomy.getInstance().getManager().getDemand(it).filter(i -> i instanceof SystemItem).ifPresent(i -> {
							if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, amount).isTransactionSuccess()) {
								double each = i.getSellPrice(1);
								double total = i.getSellPrice(amount);
								String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
								sendMessage(player, format);
							} else {
								sendMessage(player, "&cInvalid request received. Is it possible you tried selling to much?");
							}
						});
					} catch (NumberFormatException e) {
						Material request = Items.getMaterial(args[0]);
						if (request != null) {
							ItemDemand item = RetroConomy.getInstance().getManager().getDemand(request).filter(i -> i instanceof SystemItem).orElse(null);
							if (item != null) {
								if (item.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell).isTransactionSuccess()) {
									double price = item.getBuyPrice(1);
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(request.name().toLowerCase(), price, price).replace("{AMOUNT}", "1");
									sendMessage(player, format);
								} else {
									sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
								}
							} else {
								// not for sale
								sendMessage(player, "&cThis item is not for sale.");
							}
						} else {
							sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
						}
					}
					return;
				}

				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("market")) {
						try {
							double amount = Double.parseDouble(args[1]);
							ItemStack item = player.getInventory().getItemInMainHand();
							Optional<ItemDemand> demand = RetroConomy.getInstance().getManager().getDemand(item).filter(i -> i instanceof MarketItem);

							if (demand.isPresent()) {
								MarketItem m = (MarketItem) demand.get();
								m.setAmount(m.getAmount() + item.getAmount());
								item.setAmount(0);
								m.setPrice(amount);
								sendMessage(player, "&aPrice adjusted to &f" + amount);
								ItemDemand.GUI.bid(player, MarketItem.getCategory(m.getItem().getType()));
							} else {
								if (!item.getType().isAir()) {
									MarketItem it = new MarketItem(new ItemStack(item), player.getUniqueId(), amount);
									it.setAmount(it.getAmount() + item.getAmount());
									item.setAmount(0);
									ItemDemand.GUI.bid(player, MarketItem.getCategory(it.getItem().getType()));
								}
							}
						} catch (NumberFormatException e) {
							sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[1]));
						}
						return;
					}
					if (args[0].equalsIgnoreCase("hand")) {
						try {
							int amount = Integer.parseInt(args[1]);
							ItemStack item = player.getInventory().getItemInMainHand();
							RetroConomy.getInstance().getManager().getDemand(item).filter(i -> i instanceof SystemItem).ifPresent(i -> {
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, item.getAmount()).isTransactionSuccess()) {
									double each = i.getSellPrice(1);
									double total = i.getSellPrice(amount);
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
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
							RetroConomy.getInstance().getManager().getDemand(request).filter(i -> i instanceof SystemItem).ifPresent(i -> {
								if (i.invoke(player.getUniqueId(), Modifiable.TransactionResult.Sell, amount).isTransactionSuccess()) {
									double each = i.getSellPrice(1);
									double total = i.getSellPrice(amount);
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(i.getItem().getType().name().toLowerCase(), total, each).replace("{AMOUNT}", amount + "");
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

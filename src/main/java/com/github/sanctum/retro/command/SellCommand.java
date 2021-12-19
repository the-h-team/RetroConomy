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
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.api.Modifiable;
import com.github.sanctum.retro.api.Sellable;
import com.github.sanctum.retro.construct.core.MarketItem;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import com.github.sanctum.retro.util.NotifiableEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SellCommand extends CommandOrientation {

	public SellCommand(@NotNull CommandInformation information) {
		super(information);
	}

	private final SimpleTabCompletion builder = SimpleTabCompletion.empty();

	@Override
	public @NotNull List<String> complete(Player p, String[] args) {
		return builder.fillArgs(args)
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
						Optional<MarketItem> demand = RetroConomy.getInstance().getManager().getMarketItem(item);

						if (demand.isPresent()) {
							MarketItem m = demand.get();
							if (m.getOwner().equals(player.getUniqueId())) {
								m.setAmount(m.getAmount() + item.getAmount());
								item.setAmount(0);
								ItemDemand.GUI.viewGlobalShopPage(player, MarketItem.getCategory(m.getItem().getType())).open(player);
								Sound s = Sound.ENTITY_GHAST_AMBIENT;
								for (Player p : Bukkit.getOnlinePlayers()) {
									NotifiableEntity entity = NotifiableEntity.pick(p);
									if (entity.has(NotifiableEntity.Notifications.MARKET_RE_STOCKED)) {
										p.playSound(p.getEyeLocation(), s, 10, 1);
										p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + player.getName() + " &6restocked &7an item.").translate(), 10, 120, 10);
									}
								}
							}
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
						Material request = Items.findMaterial(args[0]);
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
								if (!m.getOwner().equals(player.getUniqueId())) {
									if (!item.getType().isAir()) {
										MarketItem it = new MarketItem(new ItemStack(item), player.getUniqueId(), amount);
										it.setAmount(it.getAmount() + item.getAmount());
										item.setAmount(0);
										ItemDemand.GUI.viewGlobalShopPage(player, MarketItem.getCategory(it.getItem().getType())).open(player);
										Sound s = Sound.ENTITY_GHAST_AMBIENT;
										for (Player p : Bukkit.getOnlinePlayers()) {
											NotifiableEntity entity = NotifiableEntity.pick(p);
											if (entity.has(NotifiableEntity.Notifications.MARKET_ITEM_ADDED)) {
												p.playSound(p.getEyeLocation(), s, 10, 1);
												p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + player.getName() + " &7put an item up for sale in the &e" + MarketItem.getCategory(it.getItem().getType()).name() + " &7category.").translate(), 10, 120, 10);
											}
										}
									}
									return;
								}
								m.setAmount(m.getAmount() + item.getAmount());
								item.setAmount(0);
								m.setPrice(amount);
								sendMessage(player, "&aPrice adjusted to &f" + m.getBuyPrice(1));
								ItemDemand.GUI.viewGlobalShopPage(player, MarketItem.getCategory(m.getItem().getType())).open(player);
								Sound s = Sound.ENTITY_GHAST_AMBIENT;
								for (Player p : Bukkit.getOnlinePlayers()) {
									NotifiableEntity entity = NotifiableEntity.pick(p);
									if (entity.has(NotifiableEntity.Notifications.MARKET_PRICE_CHANGE)) {
										p.playSound(p.getEyeLocation(), s, 10, 1);
										p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + player.getName() + " &7adjusted an item price listing in the &e" + MarketItem.getCategory(m.getItem().getType()).name() + " &7category.").translate(), 10, 120, 10);
									}
								}
							} else {
								if (!item.getType().isAir()) {
									MarketItem it = new MarketItem(new ItemStack(item), player.getUniqueId(), amount);
									it.setAmount(it.getAmount() + item.getAmount());
									item.setAmount(0);
									ItemDemand.GUI.viewGlobalShopPage(player, MarketItem.getCategory(it.getItem().getType())).open(player);
									Sound s = Sound.ENTITY_GHAST_AMBIENT;
									for (Player p : Bukkit.getOnlinePlayers()) {
										NotifiableEntity entity = NotifiableEntity.pick(p);
										if (entity.has(NotifiableEntity.Notifications.MARKET_ITEM_ADDED)) {
											p.playSound(p.getEyeLocation(), s, 10, 1);
											p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + player.getName() + " &7put an item up for sale in the &e" + MarketItem.getCategory(it.getItem().getType()).name() + " &7category.").translate(), 10, 120, 10);
										}
									}
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
						Material request = Items.findMaterial(args[1]);
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

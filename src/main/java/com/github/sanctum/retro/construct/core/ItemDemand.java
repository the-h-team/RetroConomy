/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.menuman.MenuBuilder;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedBuilder;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedClickAction;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedCloseAction;
import com.github.sanctum.labyrinth.gui.printer.AnvilBuilder;
import com.github.sanctum.labyrinth.gui.printer.AnvilMenu;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.MathUtils;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.FormattedMessage;
import com.github.sanctum.retro.util.ItemModificationEvent;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemDemand extends Modifiable, SellableItem {

	Listener CONTROLLER = new Listener() {

		@EventHandler
		public void onItemExchange(ItemModificationEvent e) {
			Player p = e.getBuyer();
			ItemDemand i = e.getItem();
			TimeWatch watch = TimeWatch.start(i.getLastModified());
			TimeUnit unit = TimeUnit.valueOf(FileType.MISC.get("Config").getConfig().getString("Options.price-adjustment.config.threshold"));
			int time = FileType.MISC.get("Config").getConfig().getInt("Options.price-adjustment.config.time-span");
			long trigger = FileType.MISC.get("Config").getConfig().getLong("Options.price-adjustment.config.trigger-amount");
			double adjust = FileType.MISC.get("Config").getConfig().getDouble("Options.price-adjustment.config.adjustment");
			switch (e.getTransaction()) {
				case Sell:
					if (i.getLastModified() == 0) {
						if (i.getSold(unit, time) >= trigger) {
							final double before = i.getSellPrice(1);
							final double beforeB = i.getBuyPrice(1);
							i.adjustMultiplier(i.getMultiplier() - adjust);
							final double after = i.getSellPrice(1);
							final double afterB = i.getBuyPrice(1);
							for (Player pl : Bukkit.getOnlinePlayers()) {
								if (before != after) {
									if (pl.hasPermission("retro.admin")) {
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(beforeB) + " to " + RetroConomy.getInstance().getManager().format(afterB));
									}
								}
							}
						}
					} else {
						if (watch.hasElapsed(unit, time)) {
							if (i.getSold(unit, time) >= trigger) {
								final double before = i.getSellPrice(1);
								final double beforeB = i.getBuyPrice(1);
								i.adjustMultiplier(i.getMultiplier() - adjust);
								final double after = i.getSellPrice(1);
								final double afterB = i.getBuyPrice(1);
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (before != after) {
										if (pl.hasPermission("retro.admin")) {
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(beforeB) + " to " + RetroConomy.getInstance().getManager().format(afterB));
										}
									}
								}
							}
						}
					}
					break;
				case Buy:
					if (i.getLastModified() == 0) {
						if (i.getBought(unit, time) >= trigger) {
							final double before = i.getBuyPrice(1);
							final double beforeS = i.getSellPrice(1);
							i.adjustMultiplier(i.getMultiplier() + adjust);
							final double after = i.getBuyPrice(1);
							final double afterS = i.getSellPrice(1);
							for (Player pl : Bukkit.getOnlinePlayers()) {
								if (before != after) {
									if (pl.hasPermission("retro.admin")) {
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(beforeS) + " to " + RetroConomy.getInstance().getManager().format(afterS));
									}
								}
							}
						}
					} else {
						if (watch.hasElapsed(unit, time)) {
							if (i.getBought(unit, time) >= trigger) {
								final double before = i.getBuyPrice(1);
								final double beforeS = i.getSellPrice(1);
								i.adjustMultiplier(i.getMultiplier() + adjust);
								final double after = i.getBuyPrice(1);
								final double afterS = i.getSellPrice(1);
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (before != after) {
										if (pl.hasPermission("retro.admin")) {
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(beforeS) + " to " + RetroConomy.getInstance().getManager().format(afterS));
										}
									}
								}
							}
						}
					}
				break;
			}
		}

	};

	@Override
	String toString();

	boolean isBlacklisted();

	long getRecentBought();

	long getRecentSold();

	long getSold(UUID user);

	long getSoldLast(UUID user);

	long getSold(TimeUnit unit, int time);

	long getSold();

	long getBought(UUID user);

	long getBoughtLast(UUID user);

	long getBought(TimeUnit unit, int time);

	long getBought();

	double getPopularity();

	String getLastBuyer();

	String getLastSeller();

	Map<String, Long> getBuyerTimeMap();

	Map<String, Long> getSellerTimeMap();

	Map<Long, Long> getBuyerAmountMap();

	Map<Long, Long> getSellerAmountMap();

	Map<String, Long> getBuyerMap();

	Map<String, Long> getSellerMap();

	class GUI {

		public static AnvilMenu specify(ItemDemand item, Type type) {
			switch (type) {
				case SELL:
					if (item instanceof SystemItem) {
						if (item instanceof SpecialItem) {

						} else {
							return AnvilBuilder.from(StringUtils.use("&6Specify an amount to sell.").translate())
									.setLeftItem(builder -> {
										ItemStack paper = new Item.Edit(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to sell", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
										builder.setItem(paper);
										builder.setClick((player, text, args) -> {
											if (args.length == 0) {
												try {
													int amount = (int) Double.parseDouble(text.replace(",", "."));
													Bukkit.dispatchCommand(player, "sell " + amount + " " + item.toString());
												} catch (NumberFormatException e) {

													return;
												}
											}
											if (args.length > 0) {
												for (String arg : args) {
													try {
														int amount = (int) Double.parseDouble(arg.replace(",", "."));
														Bukkit.dispatchCommand(player, "sell " + amount + " " + item.toString());
													} catch (NumberFormatException ignored) {
													}
												}
											}
										});
									})
									.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
						}
					}
					if (item instanceof MarketItem) {
						return AnvilBuilder.from(StringUtils.use("&6Specify an amount to sell.").translate())
								.setLeftItem(builder -> {
									ItemStack paper = new Item.Edit(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to sell", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
									builder.setItem(paper);
									builder.setClick((player, text, args) -> {
										if (args.length == 0) {
											try {
												int amount = (int) Double.parseDouble(text.replace(",", "."));
												if (item.invoke(player.getUniqueId(), TransactionResult.Sell, amount).isTransactionSuccess()) {
													double pay = item.getSellPrice(amount);

													String title = item.getItem().getType().name().toLowerCase();

													if (item.getItem().hasItemMeta()) {
														title = item.getItem().getItemMeta().getDisplayName();
													}

													RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(pay), player.getWorld()));
													String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(title, pay, item.getSellPrice(1)).replace("{AMOUNT}", amount + "");
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send(format);
													bid(player, MarketItem.getCategory(item.getItem().getType())).open(player);
												} else {
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&cYou don't have enough of the item.");
												}
											} catch (NumberFormatException e) {

												return;
											}
										}
										if (args.length > 0) {
											for (String arg : args) {
												try {
													int amount = (int) Double.parseDouble(arg.replace(",", "."));
													if (item.invoke(player.getUniqueId(), TransactionResult.Sell, amount).isTransactionSuccess()) {
														double pay = item.getSellPrice(amount);

														String title = item.getItem().getType().name().toLowerCase();

														if (item.getItem().hasItemMeta()) {
															title = item.getItem().getItemMeta().getDisplayName();
														}

														RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(pay), player.getWorld()));
														String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-sold")).bought(title, pay, item.getSellPrice(1)).replace("{AMOUNT}", amount + "");
														Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send(format);
														bid(player, MarketItem.getCategory(item.getItem().getType())).open(player);
													} else {
														Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&cYou don't have enough of the item.");
													}
												} catch (NumberFormatException ignored) {
												}
											}
										}
									});
								})
								.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
					}
				case BUY:
					if (item instanceof SystemItem) {
						if (item instanceof SpecialItem) {

						} else {
							return AnvilBuilder.from(StringUtils.use("&6Specify an amount to buy.").translate())
									.setLeftItem(builder -> {
										ItemStack paper = new Item.Edit(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to buy", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
										builder.setItem(paper);
										builder.setClick((player, text, args) -> {
											if (args.length == 0) {
												try {
													int amount = (int) Double.parseDouble(text.replace(",", "."));
													Bukkit.dispatchCommand(player, "buy " + amount + " " + item.toString());
												} catch (NumberFormatException e) {

													return;
												}
											}
											if (args.length > 0) {
												for (String arg : args) {
													try {
														int amount = (int) Double.parseDouble(arg.replace(",", "."));
														Bukkit.dispatchCommand(player, "buy " + amount + " " + item.toString());
													} catch (NumberFormatException ignored) {
													}
												}
											}
										});
									})
									.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
						}
					}
					if (item instanceof MarketItem) {
						return AnvilBuilder.from(StringUtils.use("&6Specify an amount to buy.").translate())
								.setLeftItem(builder -> {
									ItemStack paper = new Item.Edit(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to buy", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
									builder.setItem(paper);
									builder.setClick((player, text, args) -> {
										if (args.length == 0) {
											try {
												int amount = (int) Double.parseDouble(text.replace(",", "."));
												if (amount <= ((MarketItem) item).getAmount()) {
													MarketItem i = (MarketItem) item;
													RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

													if (wallet != null && !wallet.has(i.getBuyPrice(amount), player.getWorld())) {
														player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
														return;
													}

													String title = item.getItem().getType().name().toLowerCase();

													if (item.getItem().hasItemMeta()) {
														title = item.getItem().getItemMeta().getDisplayName();
													}

													String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-bought")).bought(title, item.getBuyPrice(amount), item.getBuyPrice(1)).replace("{AMOUNT}", amount + "");
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send(format);

													if (i.invoke(player.getUniqueId(), TransactionResult.Buy, amount).isTransactionSuccess()) {

														RetroConomy.getInstance().getManager().getWallet(i.getOwner()).ifPresent(w -> {
															w.deposit(BigDecimal.valueOf(i.getBuyPrice(amount)));
															OfflinePlayer op = w.getOwner();
															if (op.isOnline()) {
																Message.form(op.getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&7[&b✉&7] &6Market &r&l&m→&r &3&ome &7[&a" + i.getBuyPrice(amount) + "&7]");
															}
														});

														ATM atm = ATM.pick(Bukkit.getOfflinePlayer(i.getOwner()));

														if (atm != null) {
															player.getWorld().dropItem(player.getLocation(), atm.take(TransactionStatement.from(player, BigDecimal.valueOf(i.getBuyPrice(amount)), wallet, TransactionType.WITHDRAW)).toItem());
														}
														if (i.getAmount() == 0) {
															Sound s = Sound.ENTITY_GHAST_AMBIENT;
															for (Player p : Bukkit.getOnlinePlayers()) {
																p.playSound(p.getEyeLocation(), s, 10, 1);
																p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + title + " &7out of stock in &e" + MarketItem.getCategory(i.getItem().getType())).translate(), 10, 120, 10);
															}
															RetroConomy.getInstance().getManager().deleteItem(i);
														}
														bid(player, MarketItem.getCategory(i.getItem().getType())).open(player);
													}
												}
											} catch (NumberFormatException e) {

												return;
											}
										}
										if (args.length > 0) {
											for (String arg : args) {
												try {
													int amount = (int) Double.parseDouble(arg.replace(",", "."));
													if (amount <= ((MarketItem) item).getAmount()) {
														MarketItem i = (MarketItem) item;

														RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

														if (wallet != null && !wallet.has(i.getBuyPrice(amount), player.getWorld())) {
															player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
															return;
														}

														String title = item.getItem().getType().name().toLowerCase();

														if (item.getItem().hasItemMeta()) {
															title = item.getItem().getItemMeta().getDisplayName();
														}

														String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-bought")).bought(title, item.getBuyPrice(amount), item.getBuyPrice(1)).replace("{AMOUNT}", amount + "");
														Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send(format);

														if (i.invoke(player.getUniqueId(), TransactionResult.Buy, amount).isTransactionSuccess()) {

															RetroConomy.getInstance().getManager().getWallet(i.getOwner()).ifPresent(w -> {
																w.deposit(BigDecimal.valueOf(i.getBuyPrice(amount)));
																OfflinePlayer op = w.getOwner();
																if (op.isOnline()) {
																	Message.form(op.getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&7[&b✉&7] &6Market &r&l&m→&r &3&ome &7[&a" + i.getBuyPrice(amount) + "&7]");
																}
															});

															ATM atm = ATM.pick(Bukkit.getOfflinePlayer(i.getOwner()));

															if (atm != null) {
																player.getWorld().dropItem(player.getLocation(), atm.take(TransactionStatement.from(player, BigDecimal.valueOf(i.getBuyPrice(amount)), wallet, TransactionType.WITHDRAW)).toItem());
															}
															if (i.getAmount() == 0) {
																Sound s = Sound.ENTITY_GHAST_AMBIENT;
																for (Player p : Bukkit.getOnlinePlayers()) {
																	p.playSound(p.getEyeLocation(), s, 10, 1);
																	p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + title + " &7out of stock in &e" + MarketItem.getCategory(i.getItem().getType())).translate(), 10, 120, 10);
																}
																RetroConomy.getInstance().getManager().deleteItem(i);
															}
															bid(player, MarketItem.getCategory(i.getItem().getType())).open(player);
														}
													}
												} catch (NumberFormatException ignored) {
												}
											}
										}
									});
								})
								.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
					}
				default:
					throw new IllegalStateException("Invalid menu type present!");
			}
		}

		public static Menu categorySelect() {
			return new MenuBuilder(InventoryRows.THREE, StringUtils.use("Select a category. ").translate())
					.addElement(Item.ColoredArmor.select(Item.ColoredArmor.Piece.HEAD).setColor(Color.MAROON).setTitle("&eClothing").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Clothing).open(click.getPlayer());
					})//9-17
					.assignToSlots(9)
					.addElement(new Item.Edit(Material.STONE_BRICK_WALL).setTitle(StringUtils.use("&7Building").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Building).open(click.getPlayer());
					})
					.assignToSlots(10)
					.addElement(new Item.Edit(Material.ACACIA_SAPLING).setTitle(StringUtils.use("&2Agriculture").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Agriculture).open(click.getPlayer());
					})
					.assignToSlots(11)
					.addElement(new Item.Edit(Material.BREAD).setTitle(StringUtils.use("&6Food").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Food).open(click.getPlayer());
					})
					.assignToSlots(12)
					.addElement(new Item.Edit(Material.DIAMOND_SWORD).setTitle(StringUtils.use("&bWeapon").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Weapons).open(click.getPlayer());
					})
					.assignToSlots(13)
					.addElement(new Item.Edit(Material.IRON_PICKAXE).setTitle(StringUtils.use("&b&oTool").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Tools).open(click.getPlayer());
					})
					.assignToSlots(14)
					.addElement(new Item.Edit(Material.GOLDEN_CHESTPLATE).setTitle(StringUtils.use("&3Armor").translate()).addEnchantment(Enchantment.LOYALTY, 1).setFlags(ItemFlag.HIDE_ENCHANTS).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Armor).open(click.getPlayer());
					})
					.assignToSlots(15)
					.addElement(new Item.Edit(SkullItem.Head.provide(SkullItem.COMMAND_BLOCK)).setTitle(StringUtils.use("&dHead").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Head).open(click.getPlayer());
					})
					.assignToSlots(16)
					.addElement(new Item.Edit(Material.ENCHANTED_BOOK).setTitle(StringUtils.use("&9Book").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Books).open(click.getPlayer());
					})
					.assignToSlots(17)
					.addElement(new Item.Edit(Material.POTION).setTitle(StringUtils.use("&5Potion").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Potions).open(click.getPlayer());
					})
					.assignToSlots(21)
					.addElement(new Item.Edit(Material.SHULKER_BOX).setTitle(StringUtils.use("&cPackage").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Package).open(click.getPlayer());
					})
					.assignToSlots(22)
					.addElement(new Item.Edit(Material.ANVIL).setTitle(StringUtils.use("&fMisc").translate()).setLore("").build())
					.setAction(click -> {
						bid(click.getPlayer(), MarketItem.Category.Misc).open(click.getPlayer());
					})
					.assignToSlots(23)
					.setFiller(new Item.Edit(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build())
					.set()
					.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
		}

		public static Menu transact(ItemDemand item, Type type) {
			switch (type) {
				case BUY:
					return new MenuBuilder(InventoryRows.THREE, StringUtils.use("Now buying " + item.getItem().getType().name()).translate())
							.addElement(new ItemStack(item.getItem().getType(), 1))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "buy 1 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to buy 1 " + item.getItem().getType().name()).translate())
							.assignToSlots(12)
							.addElement(new ItemStack(item.getItem().getType(), 32))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "buy 32 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to buy 32 " + item.getItem().getType().name()).translate())
							.assignToSlots(13)
							.addElement(new ItemStack(item.getItem().getType(), 64))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "buy 64 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to buy 64 " + item.getItem().getType().name()).translate())
							.assignToSlots(14)
							.addElement(new ItemStack(item.getItem().getType(), 1))
							.setAction(click -> specify(item, type).setViewer(click.getPlayer()).open())
							.setLore()
							.setText(StringUtils.use("Click to buy a specified amount of " + item.getItem().getType().name()).translate())
							.assignToSlots(16)
							.addElement(new ItemStack(Material.TOTEM_OF_UNDYING, 1))
							.setAction(click -> browse().open(click.getPlayer()))
							.setLore()
							.setText(StringUtils.use("Click to go back.").translate())
							.assignToSlots(22)
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case SELL:
					return new MenuBuilder(InventoryRows.THREE, StringUtils.use("Now selling " + item.getItem().getType().name()).translate())
							.addElement(new ItemStack(item.getItem().getType(), 1))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "sell 1 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to sell 1 " + item.getItem().getType().name()).translate())
							.assignToSlots(12)
							.addElement(new ItemStack(item.getItem().getType(), 32))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "sell 32 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to sell 32 " + item.getItem().getType().name()).translate())
							.assignToSlots(13)
							.addElement(new ItemStack(item.getItem().getType(), 64))
							.setAction(click -> Bukkit.dispatchCommand(click.getPlayer(), "sell 64 " + item.toString()))
							.setLore()
							.setText(StringUtils.use("Click to sell 64 " + item.getItem().getType().name()).translate())
							.assignToSlots(14)
							.addElement(new ItemStack(item.getItem().getType(), 1))
							.setAction(click -> specify(item, type).setViewer(click.getPlayer()).open())
							.setLore()
							.setText(StringUtils.use("Click to sell a specified amount of " + item.getItem().getType().name()).translate())
							.assignToSlots(16)
							.addElement(new ItemStack(Material.TOTEM_OF_UNDYING, 1))
							.setAction(click -> browse().open(click.getPlayer()))
							.setLore()
							.setText(StringUtils.use("Click to go back.").translate())
							.assignToSlots(22)
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				default:
					throw new IllegalStateException("Invalid menu type present.");
			}
		}

		public static Menu.Paginated<ItemDemand> bid(Player viewer, MarketItem.Category category) {
			return new PaginatedBuilder<>(RetroConomy.getInstance().getManager().getMarket().filter(i -> i instanceof MarketItem && MarketItem.getCategory(i.getItem().getType()) == category).collect(Collectors.toList()))
					.forPlugin(JavaPlugin.getProvidingPlugin(RetroConomy.class))
					.setTitle(StringUtils.use("The Market").translate())
					.setSize(InventoryRows.SIX)
					.setAlreadyFirst(StringUtils.use("&cYou are already on the first page.").translate())
					.setAlreadyLast(StringUtils.use("&cYou are already on the last page.").translate())
					.setCloseAction(PaginatedCloseAction::clear)
					.setNavigationBack(() -> Items.getItem(Material.BARRIER, "&aClick to refresh."), 49, click -> bid(click.getPlayer(), category).open(click.getPlayer()))
					.setNavigationLeft(() -> new Item.Edit(SkullItem.Head.provide(SkullItem.ARROW_BLACK_LEFT)).setTitle(StringUtils.use("&3← Go back a page").translate()).build(), 45, PaginatedClickAction::sync)
					.setNavigationRight(() -> new Item.Edit(SkullItem.Head.provide(SkullItem.ARROW_BLACK_RIGHT)).setTitle(StringUtils.use("&3Go to the next page →").translate()).build(), 53, PaginatedClickAction::sync)
					.setupProcess(e -> {
						e.setItem(() -> {
							MarketItem demand = (MarketItem) e.getContext();
							if (demand != null) {
								ItemStack i;
								if (demand.getItem().hasItemMeta()) {
									i = new Item.Edit(demand.getItem())
											.setTitle( demand.getItem().getItemMeta().getDisplayName())
											.build();
								} else {
									i = new Item.Edit(demand.getItem())
											.setTitle(demand.getItem().getType().name())
											.build();
								}
								i.setAmount(1);
								ItemMeta meta = i.getItemMeta();
								Map<Enchantment, Integer> enchantments = demand.getItem().getEnchantments();
								if (demand.getOwner().equals(viewer.getUniqueId())) {
									meta.setLore(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Right-click to &3buy&7.").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Left-Shift-click to &4remove&7.").translate(),
											StringUtils.use("&7Right-Shift-click to add to &6stock&7.").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Buy: " + MathUtils.use(demand.getBuyPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&cPopularity: &7(&2 + " + RetroConomy.getInstance().getManager().format(Math.min(demand.getPopularity(), 950)) + " &7)").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&eRecent bought &7(&21m&f, &25m&f, &215m&7): &f" + demand.getBought(TimeUnit.MINUTES, 1) + ", " + demand.getBought(TimeUnit.MINUTES, 5) + ", " + demand.getBought(TimeUnit.MINUTES, 15)).translate(),
											StringUtils.use(" ").translate()));
								} else {
									meta.setLore(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Right-click to &3buy&7.").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Buy: " + MathUtils.use(demand.getBuyPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&cPopularity: &7(&2 + " + RetroConomy.getInstance().getManager().format(Math.min(demand.getPopularity(), 950)) + " &7)").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&eRecent bought &7(&21m&f, &25m&f, &215m&7): &f" + demand.getBought(TimeUnit.MINUTES, 1) + ", " + demand.getBought(TimeUnit.MINUTES, 5) + ", " + demand.getBought(TimeUnit.MINUTES, 15)).translate(),
											StringUtils.use(" ").translate()));
								}
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								i.setItemMeta(meta);
								if (demand.getPopularity() >= 500) {
									i.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
								}
								return i;
							}
							throw new IllegalStateException("An invalid shop item was presented while generating the GUI.");
						}).setClick(click -> {
							MarketItem demand = (MarketItem) e.getContext();
							if (demand != null) {
								if (click.isLeftClick()) {
									if (click.isShiftClick()) {
										if (demand.getOwner().equals(click.getPlayer().getUniqueId())) {
											ItemStack copy = new ItemStack(demand.getItem());
											copy.setAmount(1);
											for (int i = 0; i < demand.getAmount(); i++) {
												click.getPlayer().getWorld().dropItem(click.getPlayer().getLocation(), copy);
											}
											RetroConomy.getInstance().getManager().deleteItem(demand);
											bid(click.getPlayer(), MarketItem.getCategory(demand.getItem().getType())).open(click.getPlayer());
											return;
										}
									}
								}
								if (click.isRightClick()) {
									if (click.isShiftClick()) {
										if (demand.getOwner().equals(click.getPlayer().getUniqueId())) {
											for (ItemStack i : click.getPlayer().getInventory().getContents()) {
												Optional<ItemDemand> d = RetroConomy.getInstance().getManager().getDemand(i).filter(it -> it instanceof MarketItem);

												if (d.isPresent()) {
													MarketItem m = (MarketItem) d.get();
													m.setAmount(m.getAmount() + i.getAmount());
													i.setAmount(0);
												}
											}
											bid(click.getPlayer(), MarketItem.getCategory(demand.getItem().getType())).open(click.getPlayer());
											return;
										}
									}
									specify(demand, Type.BUY).setViewer(click.getPlayer()).open();
								}
							}
						});
					})
					.extraElements()
					.invoke(new Item.Edit(Material.HEART_OF_THE_SEA).setTitle(StringUtils.use("&3[&eServer Market&3]").translate()).build(), 46, click -> {
						browse().open(click.getPlayer());
					})
					.invoke(new Item.Edit(SkullItem.Head.provide(SkullItem.ARROW_CYAN_UP)).setTitle(StringUtils.use("&cCategories.").translate()).build(), 52, click -> {
						categorySelect().open(click.getPlayer());
					})
					.add()
					.setupBorder()
					.setBorderType(Material.IRON_BARS)
					.setFillType(Material.GRAY_STAINED_GLASS_PANE)
					.build()
					.build();
		}

		public static Menu.Paginated<ItemDemand> browse() {
			return new PaginatedBuilder<>(RetroConomy.getInstance().getManager().getMarket().filter(i -> i instanceof SystemItem).collect(Collectors.toList()))
					.forPlugin(JavaPlugin.getProvidingPlugin(RetroConomy.class))
					.setTitle(StringUtils.use("The Shop").translate())
					.setSize(InventoryRows.SIX)
					.setAlreadyFirst(StringUtils.use("&cYou are already on the first page.").translate())
					.setAlreadyLast(StringUtils.use("&cYou are already on the last page.").translate())
					.setCloseAction(PaginatedCloseAction::clear)
					.setNavigationBack(() -> Items.getItem(Material.BARRIER, "&aClick to refresh."), 49, click -> browse().open(click.getPlayer()))
					.setNavigationLeft(() -> new Item.Edit(SkullItem.Head.provide(SkullItem.ARROW_BLACK_LEFT)).setTitle(StringUtils.use("&3← Go back a page").translate()).build(), 45, PaginatedClickAction::sync)
					.setNavigationRight(() -> new Item.Edit(SkullItem.Head.provide(SkullItem.ARROW_BLACK_RIGHT)).setTitle(StringUtils.use("&3Go to the next page →").translate()).build(), 53, PaginatedClickAction::sync)
					.setupProcess(e -> {
						e.setItem(() -> {
							ItemDemand demand = e.getContext();
							if (demand != null) {
								ItemStack i = Items.getItem(demand.getItem().getType(), "&e" + demand.getItem().getType().name().toLowerCase().replace("_", " "));
								ItemMeta meta = i.getItemMeta();
								meta.setLore(Arrays.asList(StringUtils.use(" ").translate(),
										StringUtils.use("&7Right-click to &3buy&7.").translate(),
										StringUtils.use("&7Left-click to &csell&7.").translate(),
										StringUtils.use(" ").translate(),
										StringUtils.use("&7Buy: " + MathUtils.use(demand.getBuyPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
										StringUtils.use("&7Sell: " + MathUtils.use(demand.getSellPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
										StringUtils.use(" ").translate(),
										StringUtils.use("&cPopularity: &7(&2 + " + RetroConomy.getInstance().getManager().format(Math.min(demand.getPopularity(), 950)) + " &7)").translate(),
										StringUtils.use(" ").translate(),
										StringUtils.use("&eRecent bought &7(&21m&f, &25m&f, &215m&7): &f" + demand.getBought(TimeUnit.MINUTES, 1) + ", " + demand.getBought(TimeUnit.MINUTES, 5) + ", " + demand.getBought(TimeUnit.MINUTES, 15)).translate(),
										StringUtils.use("&eRecent sold &7(&21m&f, &25m&f, &215m&7): &f" + demand.getSold(TimeUnit.MINUTES, 1) + ", " + demand.getSold(TimeUnit.MINUTES, 5) + ", " + demand.getSold(TimeUnit.MINUTES, 15)).translate(),
										StringUtils.use(" ").translate()));
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								i.setItemMeta(meta);
								if (demand.getPopularity() >= 500) {
									i.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
								}
								return i;
							}
							throw new IllegalStateException("An invalid shop item was presented while generating the GUI.");
						}).setClick(click -> {
							ItemDemand demand = e.getContext();
							if (demand != null) {
								if (click.isLeftClick()) {
									transact(demand, Type.SELL).open(click.getPlayer());
								}
								if (click.isRightClick()) {
									transact(demand, Type.BUY).open(click.getPlayer());
								}
							}
						});
					})
					.extraElements()
					.invoke(new Item.Edit(Material.HEART_OF_THE_SEA).setTitle(StringUtils.use("&3[&7Player Market&3]").translate()).build(), 46, click -> {
						categorySelect().open(click.getPlayer());
					})
					.add()
					.setupBorder()
					.setBorderType(Material.IRON_BARS)
					.setFillType(Material.GRAY_STAINED_GLASS_PANE)
					.build()
					.build();
		}

		public enum Type {
			SHOP, SELL, BUY
		}

	}

}

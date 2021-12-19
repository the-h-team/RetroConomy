/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.api;

import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.BorderElement;
import com.github.sanctum.labyrinth.gui.unity.impl.FillerElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ListElement;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.MathUtils;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.MarketItem;
import com.github.sanctum.retro.construct.core.Shop;
import com.github.sanctum.retro.construct.core.SpecialItem;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.construct.core.TransactionStatement;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FileReader;
import com.github.sanctum.retro.util.FormattedMessage;
import com.github.sanctum.retro.util.ItemModificationEvent;
import com.github.sanctum.retro.util.NotifiableEntity;
import com.github.sanctum.skulls.CustomHeadLoader;
import com.github.sanctum.skulls.SkullType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemDemand extends Modifiable, Sellable {

	Listener CONTROLLER = new Listener() {

		@EventHandler
		public void onItemExchange(ItemModificationEvent e) {
			Player p = e.getBuyer();
			ItemDemand i = e.getItem();
			TimeWatch watch = TimeWatch.start(i.getLastModified());
			TimeUnit unit = TimeUnit.valueOf(FileReader.MISC.get("Config").getRoot().getString("Options.price-adjustment.config.threshold"));
			int time = FileReader.MISC.get("Config").getRoot().getInt("Options.price-adjustment.config.time-span");
			long trigger = FileReader.MISC.get("Config").getRoot().getLong("Options.price-adjustment.config.trigger-amount");
			double adjust = FileReader.MISC.get("Config").getRoot().getDouble("Options.price-adjustment.config.adjustment");
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
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(beforeB) + " to " + RetroConomy.getInstance().getManager().format(afterB));
									}
								}
							}
						}
					} else {
						if (watch.isBetween(unit, time)) {
							if (i.getSold(unit, time) >= trigger) {
								final double before = i.getSellPrice(1);
								final double beforeB = i.getBuyPrice(1);
								i.adjustMultiplier(i.getMultiplier() - adjust);
								final double after = i.getSellPrice(1);
								final double afterB = i.getBuyPrice(1);
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (before != after) {
										if (pl.hasPermission("retro.admin")) {
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(beforeB) + " to " + RetroConomy.getInstance().getManager().format(afterB));
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
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
										Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(beforeS) + " to " + RetroConomy.getInstance().getManager().format(afterS));
									}
								}
							}
						}
					} else {
						if (watch.isBetween(unit, time)) {
							if (i.getBought(unit, time) >= trigger) {
								final double before = i.getBuyPrice(1);
								final double beforeS = i.getSellPrice(1);
								i.adjustMultiplier(i.getMultiplier() + adjust);
								final double after = i.getBuyPrice(1);
								final double afterS = i.getSellPrice(1);
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (before != after) {
										if (pl.hasPermission("retro.admin")) {
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " buy price from " + RetroConomy.getInstance().getManager().format(before) + " to " + RetroConomy.getInstance().getManager().format(after));
											Message.form(pl).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("Adjusted item " + i.getItem().getType().name() + " sell price from " + RetroConomy.getInstance().getManager().format(beforeS) + " to " + RetroConomy.getInstance().getManager().format(afterS));
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

		static final Plugin plugin = JavaPlugin.getProvidingPlugin(RetroConomy.class);

		public static Menu typeAmount(ItemDemand item, TransactionType type) {
			switch (type) {
				case SELL:
					if (item instanceof SystemItem) {
						if (item instanceof SpecialItem) {

						} else {
							return MenuType.PRINTABLE.build()
									.setHost(plugin)
									.setSize(Menu.Rows.ONE)
									.setTitle("&6Specify an amount to sell.")
									.setStock(i -> {
										i.addItem(it -> {
											it.setElement(ed -> ed.setType(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to sell", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build());
											it.setType(ItemElement.ControlType.DISPLAY);
											it.setSlot(0);
										});
									})
									.join().addAction(click -> {
										click.setCancelled(true);
										click.setHotbarAllowed(false);
										if (click.getSlot() == 2) {
											Player player = click.getElement();
											String[] args = click.getParent().getName().split(" ");
											for (String arg : args) {
												try {
													int amount = (int) Double.parseDouble(arg.replace(",", "."));
													Bukkit.dispatchCommand(player, "sell " + amount + " " + item);
												} catch (NumberFormatException ignored) {
												}
											}
										}

									});
						}
					}
					if (item instanceof MarketItem) {
						return MenuType.PRINTABLE.build()
								.setHost(plugin)
								.setSize(Menu.Rows.ONE)
								.setTitle("&6Specify an amount to sell.")
								.setStock(i -> {
									i.addItem(it -> {
										it.setElement(ed -> ed.setType(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to sell", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build());
										it.setType(ItemElement.ControlType.DISPLAY);
										it.setSlot(0);
									});
								})
								.join().addAction(click -> {
									click.setCancelled(true);
									click.setHotbarAllowed(false);
									if (click.getSlot() == 2) {
										Player player = click.getElement();
										String[] args = click.getParent().getName().split(" ");
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
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send(format);
													viewGlobalShopPage(player, MarketItem.getCategory(item.getItem().getType())).open(player);
												} else {
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("&cYou don't have enough of the item.");
												}
											} catch (NumberFormatException ignored) {
											}
										}
									}

								});
					}
				case BUY:
					if (item instanceof SystemItem) {
						if (item instanceof SpecialItem) {

						} else {
							return MenuType.PRINTABLE.build()
									.setHost(plugin)
									.setSize(Menu.Rows.ONE)
									.setTitle("&6Specify an amount to buy.")
									.setStock(i -> {
										i.addItem(it -> {
											it.setElement(ed -> ed.setType(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to buy", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build());
											it.setSlot(0);
											it.setType(ItemElement.ControlType.DISPLAY);
										});
									})
									.join().addAction(click -> {
										click.setCancelled(true);
										click.setHotbarAllowed(false);
										if (click.getSlot() == 2) {
											Player player = click.getElement();
											String[] args = click.getParent().getName().split(" ");
											for (String arg : args) {
												try {
													int amount = (int) Double.parseDouble(arg.replace(",", "."));
													Bukkit.dispatchCommand(player, "buy " + amount + " " + item);
												} catch (NumberFormatException ignored) {
												}
											}
										}

									});
						}
					}
					if (item instanceof MarketItem) {
						return MenuType.PRINTABLE.build()
								.setHost(plugin)
								.setSize(Menu.Rows.ONE)
								.setTitle("&6Specify an amount to buy.")
								.setStock(i -> {
									i.addItem(it -> {
										it.setElement(ed -> ed.setType(Material.PAPER).setTitle("Specify an amount → ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to buy", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build());
										it.setType(ItemElement.ControlType.DISPLAY);
										it.setSlot(0);
									});
								})
								.join().addAction(c -> {
									c.setCancelled(true);
									c.setHotbarAllowed(false);
									if (c.getSlot() == 2) {
										Player player = c.getElement();
										String[] args = c.getParent().getName().split(" ");
										for (String arg : args) {
											try {
												int amount = (int) Double.parseDouble(arg.replace(",", "."));
												if (amount <= ((MarketItem) item).getAmount()) {
													MarketItem marketItem = (MarketItem) item;

													RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

													if (wallet != null && !wallet.has(marketItem.getBuyPrice(amount), player.getWorld())) {
														player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
														return;
													}

													String title = item.getItem().getType().name().toLowerCase();

													if (item.getItem().hasItemMeta()) {
														title = item.getItem().getItemMeta().getDisplayName();
													}

													String format = FormattedMessage.convert(ConfiguredMessage.getMessage("item-bought")).bought(title, item.getBuyPrice(amount), item.getBuyPrice(1)).replace("{AMOUNT}", amount + "");
													Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send(format);

													if (marketItem.invoke(player.getUniqueId(), TransactionResult.Buy, amount).isTransactionSuccess()) {

														RetroConomy.getInstance().getManager().getWallet(marketItem.getOwner()).ifPresent(w -> {
															w.deposit(BigDecimal.valueOf(marketItem.getBuyPrice(amount)));
															OfflinePlayer op = w.getOwner();
															if (op.isOnline()) {
																Message.form(op.getPlayer()).send("&7[&b✉&7] &6Market &r&l&m→&r &3&ome &7[&a" + marketItem.getBuyPrice(amount) + "&7]");
															}
														});

														Shop atm = Shop.pick(Bukkit.getOfflinePlayer(marketItem.getOwner()));

														if (atm != null) {
															player.getWorld().dropItem(player.getLocation(), atm.take(TransactionStatement.from(title, player, BigDecimal.valueOf(marketItem.getBuyPrice(amount)), wallet, com.github.sanctum.retro.util.TransactionType.WITHDRAW)).toItem());
														}
														if (marketItem.getAmount() == 0) {
															Sound s = Sound.ENTITY_GHAST_AMBIENT;
															for (Player p : Bukkit.getOnlinePlayers()) {
																NotifiableEntity entity = NotifiableEntity.pick(p);
																if (entity.has(NotifiableEntity.Notifications.MARKET_OUT_OF_STOCK)) {
																	p.playSound(p.getEyeLocation(), s, 10, 1);
																	p.sendTitle(StringUtils.use("&b[&f&m⚔&b] &r[&6Market&r] &b[&f&m⚔&b]").translate(), StringUtils.use("&2" + title + " &7out of stock in &e" + MarketItem.getCategory(marketItem.getItem().getType())).translate(), 10, 120, 10);
																}
															}
															RetroConomy.getInstance().getManager().deleteItem(marketItem);
															c.getParent().remove(c.getElement(), true);
														}
														player.closeInventory();
													}
												}
											} catch (NumberFormatException ignored) {
											}
										}
									}

								});
					}
				default:
					throw new IllegalStateException("Invalid menu type present!");
			}
		}

		public static Menu selectShopCategory(UUID owner) {
			return MenuType.SINGULAR.build()
					.setHost(plugin)
					.setSize(Menu.Rows.THREE)
					.setTitle("Select a category.")
					.setStock(i -> {
						i.addItem(it -> {
							it.setElement(Item.ColoredArmor.select(Item.ColoredArmor.Piece.HEAD).setColor(Color.MAROON).setTitle("&eClothing &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Clothing && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Clothing).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(9);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.STONE_BRICK_WALL).setTitle(StringUtils.use("&7Building &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Building && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Building).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(10);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ACACIA_SAPLING).setTitle(StringUtils.use("&2Agriculture &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Agriculture && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Agriculture).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(11);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.BREAD).setTitle(StringUtils.use("&6Food &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Food && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Food).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(12);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.DIAMOND_SWORD).setTitle(StringUtils.use("&bWeapon &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Weapons && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Weapons).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(13);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.IRON_PICKAXE).setTitle(StringUtils.use("&b&oTool &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Tools && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Tools).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(14);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.GOLDEN_CHESTPLATE).setTitle(StringUtils.use("&3Armor &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Armor && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).addEnchantment(Enchantment.LOYALTY, 1).setFlags(ItemFlag.HIDE_ENCHANTS).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Armor).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(15);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(SkullType.COMMAND_BLOCK.get()).setTitle(StringUtils.use("&dHead &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Head && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Head).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(16);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ENCHANTED_BOOK).setTitle(StringUtils.use("&9Book &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Books && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Books).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(17);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.POTION).setTitle(StringUtils.use("&5Potion &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Potions && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Potions).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(21);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.SHULKER_BOX).setTitle(StringUtils.use("&cPackage &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Package && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Package).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(22);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ANVIL).setTitle(StringUtils.use("&fMisc &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Misc && ((MarketItem) demand).getOwner().equals(owner)).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewShopPage(click.getElement(), owner, MarketItem.Category.Misc).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(23);
						});
						// gray filler
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
						i.addItem(filler);
					})
					.join();
		}

		public static Menu editNotifications(NotifiableEntity entity) {
			return MenuType.PAGINATED.build()
					.setHost(plugin)
					.setSize(Menu.Rows.ONE)
					.setTitle("Notifications")
					.setProperty(Menu.Property.CACHEABLE)
					.setKey("notifications-" + entity.getId().toString())
					.setStock(i -> {

						ListElement<NotifiableEntity.Notifications> list = new ListElement<>(Arrays.asList(NotifiableEntity.Notifications.values()));
						list.setPopulate((noti, item) -> {
							boolean enabled = entity.has(noti);
							item.setElement(new Item.Edit(Material.NAME_TAG).setTitle(enabled ? "&3" + noti.getTag().replace("_", " ") : "&c" + noti.getTag().replace("_", " ")).build());
							item.setClick(click -> {
								click.setCancelled(true);
								if (enabled) {
									entity.remove(noti);
								} else {
									entity.add(noti);
								}
								editNotifications(entity).open(click.getElement());
							});
						});
						i.addItem(list);
						i.addItem(it -> {
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_LEFT.get()).setTitle("&cGo back.").build());
							it.setSlot(8);
							it.setClick(click -> {
								click.setCancelled(true);
								viewGlobalShop().open(click.getElement());
							});
						});
					})
					.orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("notifications-" + entity.getId().toString())::equals).orElse(false));
		}

		public static Menu selectGlobalCategory() {
			return MenuType.SINGULAR.build()
					.setHost(plugin)
					.setSize(Menu.Rows.THREE)
					.setTitle("Select a category.")
					.setStock(i -> {
						i.addItem(it -> {
							it.setElement(Item.ColoredArmor.select(Item.ColoredArmor.Piece.HEAD).setColor(Color.MAROON).setTitle("&eClothing &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Clothing).count() + "&7)").build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Clothing).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(9);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.STONE_BRICK_WALL).setTitle(StringUtils.use("&7Building &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Building).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Building).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(10);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ACACIA_SAPLING).setTitle(StringUtils.use("&2Agriculture &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Agriculture).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Agriculture).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(11);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.BREAD).setTitle(StringUtils.use("&6Food &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Food).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Food).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(12);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.DIAMOND_SWORD).setTitle(StringUtils.use("&bWeapon &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Weapons).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Weapons).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(13);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.IRON_PICKAXE).setTitle(StringUtils.use("&b&oTool &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Tools).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Tools).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(14);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.GOLDEN_CHESTPLATE).setTitle(StringUtils.use("&3Armor &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Armor).count() + "&7)").translate()).addEnchantment(Enchantment.LOYALTY, 1).setFlags(ItemFlag.HIDE_ENCHANTS).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Armor).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(15);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(SkullType.COMMAND_BLOCK.get()).setTitle(StringUtils.use("&dHead &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Head).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Head).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(16);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ENCHANTED_BOOK).setTitle(StringUtils.use("&9Book &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Books).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Books).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(17);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.POTION).setTitle(StringUtils.use("&5Potion &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Potions).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Potions).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(21);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.SHULKER_BOX).setTitle(StringUtils.use("&cPackage &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Package).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Package).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(22);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.ANVIL).setTitle(StringUtils.use("&fMisc &7(&b" + (int) RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == MarketItem.Category.Misc).count() + "&7)").translate()).build());
							it.setClick(click -> {
								viewGlobalShopPage(click.getElement(), MarketItem.Category.Misc).open(click.getElement());
								click.setCancelled(true);
							});
							it.setSlot(23);
						});
						// gray filler
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
						i.addItem(filler);
					})
					.join();
		}

		public static Menu attemptTransaction(ItemDemand item, TransactionType type) {
			switch (type) {
				case BUY:
					return MenuType.SINGULAR.build()
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setProperty(Menu.Property.CACHEABLE)
							.setKey("buy-" + item.toString())
							.setTitle("Now buying " + item.getItem().getType().name())
							.setStock(i -> {
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 1)).setTitle("Click to buy 1 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "buy 1 " + item);
									});
									it.setSlot(12);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 32)).setTitle("Click to buy 32 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "buy 32 " + item);
									});
									it.setSlot(13);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 64)).setTitle("Click to buy 64 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "buy 64 " + item);
									});
									it.setSlot(14);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 1)).setTitle("Click to buy a specified amount of " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										typeAmount(item, type).open(click.getElement());
									});
									it.setSlot(16);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1)).setTitle("&cClick to go back").build());
									it.setClick(click -> {
										click.setCancelled(true);
										viewGlobalShop().open(click.getElement());
									});
									it.setSlot(22);
								});
							})
							.orGet(m -> m instanceof SingularMenu && m.getKey().map(("buy-" + item)::equals).orElse(false));
				case SELL:
					return MenuType.SINGULAR.build()
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setProperty(Menu.Property.CACHEABLE)
							.setKey("sell-" + item.toString())
							.setTitle("Now selling " + item.getItem().getType().name())
							.setStock(i -> {
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 1)).setTitle("Click to sell 1 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "sell 1 " + item);
									});
									it.setSlot(12);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 32)).setTitle("Click to sell 32 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "sell 32 " + item);
									});
									it.setSlot(13);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 64)).setTitle("Click to sell 64 " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										Bukkit.dispatchCommand(click.getElement(), "sell 64 " + item);
									});
									it.setSlot(14);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(item.getItem().getType(), 1)).setTitle("Click to sell a specified amount of " + item.getItem().getType().name()).build());
									it.setClick(click -> {
										click.setCancelled(true);
										typeAmount(item, type).open(click.getElement());
									});
									it.setSlot(16);
								});
								i.addItem(it -> {
									it.setElement(ed -> ed.setItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1)).setTitle("&cClick to go back").build());
									it.setClick(click -> {
										click.setCancelled(true);
										viewGlobalShop().open(click.getElement());
									});
									it.setSlot(22);
								});
							})
							.orGet(m -> m instanceof SingularMenu && m.getKey().map(("sell-" + item)::equals).orElse(false));
				default:
					throw new IllegalStateException("Invalid menu type present.");
			}
		}

		public static Menu viewShopPage(Player viewer, UUID target, MarketItem.Category category) {
			return MenuType.PAGINATED.build()
					.setHost(plugin)
					.setSize(Menu.Rows.SIX)
					.setTitle(Bukkit.getOfflinePlayer(target).getName() + "'s Shop")
					.setProperty(Menu.Property.LIVE_META)
					.setStock(i -> {
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_NEXT);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_RIGHT.get()).setTitle(StringUtils.use("&3Go to the next page →").translate()).build());
							it.setSlot(53);
						});
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_BACK);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_LEFT.get()).setTitle(StringUtils.use("&3← Go back a page").translate()).build());
							it.setSlot(45);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.HEART_OF_THE_SEA).setTitle(StringUtils.use("&3[&7Server Market&3]").translate()).build());
							it.setSlot(46);
							it.setClick( click -> {
								click.setCancelled(true);
								viewGlobalShop().open(click.getElement());
							});
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(SkullType.ARROW_BLUE_UP.get()).setTitle(StringUtils.use("&3[&cCategories&3]").translate()).build());
							it.setSlot(52);
							it.setClick( click -> {
								click.setCancelled(true);
								selectGlobalCategory().open(click.getElement());
							});
						});
						ListElement<MarketItem> list = new ListElement<>(RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == category && ((MarketItem)demand).getOwner().equals(target)).map(demand -> (MarketItem)demand).collect(Collectors.toList()));
						list.setLimit(28);
						list.setComparator(Comparator.comparing(item -> {
							MarketItem marketItem = item.getData().get();
							return Bukkit.getOfflinePlayer(marketItem.getOwner()).getName() + item.getName();
						}));
						list.setPopulate((demand, item) -> {
							if (demand != null) {
								ItemStack stack;
								if (demand.getItem().hasItemMeta()) {
									stack = new Item.Edit(demand.getItem())
											.setTitle(demand.getItem().getItemMeta().getDisplayName())
											.build();
								} else {
									stack = new Item.Edit(demand.getItem())
											.setTitle(demand.getItem().getType().name())
											.build();
								}
								stack.setAmount(1);
								ItemMeta meta = stack.getItemMeta();

								List<String> lore = new ArrayList<>();

								if (meta.getLore() != null) {

									lore.addAll(stack.getItemMeta().getLore());

								}

								Map<Enchantment, Integer> enchantments = demand.getItem().getEnchantments();
								if (demand.getOwner().equals(viewer.getUniqueId())) {

									List<String> set = new ArrayList<>(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Lore &3►").translate()));

									if (lore.isEmpty()) {
										set.add(StringUtils.use("&r Empty").translate());
									} else {
										set.addAll(lore);
									}

									set.addAll(Arrays.asList(
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

									meta.setLore(set);
								} else {
									List<String> set = new ArrayList<>(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Lore &3►").translate()));

									if (lore.isEmpty()) {
										set.add(StringUtils.use("&r Empty").translate());
									} else {
										set.addAll(lore);
									}

									set.addAll(Arrays.asList(
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Right-click to &3buy&7.").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Buy: " + MathUtils.use(demand.getBuyPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&cPopularity: &7(&2 + " + RetroConomy.getInstance().getManager().format(Math.min(demand.getPopularity(), 950)) + " &7)").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&eRecent bought &7(&21m&f, &25m&f, &215m&7): &f" + demand.getBought(TimeUnit.MINUTES, 1) + ", " + demand.getBought(TimeUnit.MINUTES, 5) + ", " + demand.getBought(TimeUnit.MINUTES, 15)).translate(),
											StringUtils.use(" ").translate()));

									meta.setLore(set);
								}
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								stack.setItemMeta(meta);
								if (demand.getPopularity() >= 500) {
									stack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
								}
								item.setElement(stack);
								item.setClick(click -> {
									if (click.getClickType() == ClickType.SHIFT_LEFT) {
										if (demand.getOwner().equals(click.getElement().getUniqueId())) {
											ItemStack copy = new ItemStack(demand.getItem());
											copy.setAmount(1);
											for (int length = 0; length < demand.getAmount(); length++) {
												click.getElement().getWorld().dropItem(click.getElement().getLocation(), copy);
											}
											RetroConomy.getInstance().getManager().deleteItem(demand);
											click.getParent().remove(click.getElement(), true);
											viewShopPage(click.getElement(), target, MarketItem.getCategory(demand.getItem().getType())).open(click.getElement());
											return;
										}
									}
									if (click.getClickType() == ClickType.SHIFT_RIGHT) {
										if (demand.getOwner().equals(click.getElement().getUniqueId())) {
											for (ItemStack itemStack : click.getElement().getInventory().getContents()) {
												Optional<MarketItem> d = RetroConomy.getInstance().getManager().getMarketItem(itemStack);

												if (d.isPresent()) {
													MarketItem m = d.get();
													m.setAmount(m.getAmount() + itemStack.getAmount());
													itemStack.setAmount(0);
												}
											}
											viewShopPage(click.getElement(), target, MarketItem.getCategory(demand.getItem().getType())).open(click.getElement());
										} else {
											Bukkit.broadcastMessage(demand.getOwner().toString());
										}
									}
									if (click.getClickType() == ClickType.RIGHT) {
										typeAmount(demand, TransactionType.BUY).open(click.getElement());
									}
								});
							}
						});
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> {
							ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
						});
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p != Menu.Panel.MIDDLE) {
								border.add(p, ed -> {
									ed.setElement(it -> it.setType(Material.IRON_BARS).setTitle(" ").build());
								});
							}
						}
						i.addItem(list);
						i.addItem(filler);
						i.addItem(border);
					})
					.join();
		}

		public static Menu viewGlobalShopPage(Player viewer, MarketItem.Category category) {
			return MenuType.PAGINATED.build()
					.setHost(plugin)
					.setSize(Menu.Rows.SIX)
					.setTitle("The Market")
					.setProperty(Menu.Property.LIVE_META)
					.setStock(i -> {
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_NEXT);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_RIGHT.get()).setTitle(StringUtils.use("&3Go to the next page →").translate()).build());
							it.setSlot(53);
						});
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_BACK);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_LEFT.get()).setTitle(StringUtils.use("&3← Go back a page").translate()).build());
							it.setSlot(45);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.HEART_OF_THE_SEA).setTitle(StringUtils.use("&3[&7Server Market&3]").translate()).build());
							it.setSlot(46);
							it.setClick( click -> {
								click.setCancelled(true);
								viewGlobalShop().open(click.getElement());
							});
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(SkullType.ARROW_BLUE_UP.get()).setTitle(StringUtils.use("&3[&cCategories&3]").translate()).build());
							it.setSlot(52);
							it.setClick( click -> {
								click.setCancelled(true);
								selectGlobalCategory().open(click.getElement());
							});
						});
						ListElement<MarketItem> list = new ListElement<>(RetroConomy.getInstance().getManager().getInventory().stream().filter(demand -> demand instanceof MarketItem && MarketItem.getCategory(demand.getItem().getType()) == category).map(demand -> (MarketItem)demand).collect(Collectors.toList()));
						list.setLimit(28);
						list.setComparator(Comparator.comparing(item -> {
							MarketItem marketItem = item.getData().get();
							return Bukkit.getOfflinePlayer(marketItem.getOwner()).getName() + item.getName();
						}));
						list.setPopulate((demand, item) -> {
							if (demand != null) {
								ItemStack stack;
								if (demand.getItem().hasItemMeta()) {
									stack = new Item.Edit(demand.getItem())
											.setTitle(demand.getItem().getItemMeta().getDisplayName())
											.build();
								} else {
									stack = new Item.Edit(demand.getItem())
											.setTitle(demand.getItem().getType().name())
											.build();
								}
								stack.setAmount(1);
								ItemMeta meta = stack.getItemMeta();

								List<String> lore = new ArrayList<>();

								if (meta.getLore() != null) {

									lore.addAll(stack.getItemMeta().getLore());

								}

								Map<Enchantment, Integer> enchantments = demand.getItem().getEnchantments();
								if (demand.getOwner().equals(viewer.getUniqueId())) {

									List<String> set = new ArrayList<>(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Lore &3►").translate()));

									if (lore.isEmpty()) {
										set.add(StringUtils.use("&r Empty").translate());
									} else {
										set.addAll(lore);
									}

									set.addAll(Arrays.asList(
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

									meta.setLore(set);
								} else {
									List<String> set = new ArrayList<>(Arrays.asList(StringUtils.use(" ").translate(),
											StringUtils.use("&7Seller &3► &b&n" + Bukkit.getOfflinePlayer(demand.getOwner()).getName()).translate(),
											StringUtils.use("&7Amount &3► &f(&a" + demand.getAmount() + "&f)").translate(),
											StringUtils.use("&7Enchants &3► &f(&3" + enchantments.size() + "&f) " + enchantments.entrySet().stream().map(en -> en.getKey().getKey().getKey() + " Lvl." + en.getValue()).collect(Collectors.joining())).translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Lore &3►").translate()));

									if (lore.isEmpty()) {
										set.add(StringUtils.use("&r Empty").translate());
									} else {
										set.addAll(lore);
									}

									set.addAll(Arrays.asList(
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Right-click to &3buy&7.").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&7Buy: " + MathUtils.use(demand.getBuyPrice(1)).format(RetroConomy.getInstance().getManager().getLocale()) + " &fx &6Amount").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&cPopularity: &7(&2 + " + RetroConomy.getInstance().getManager().format(Math.min(demand.getPopularity(), 950)) + " &7)").translate(),
											StringUtils.use(" ").translate(),
											StringUtils.use("&eRecent bought &7(&21m&f, &25m&f, &215m&7): &f" + demand.getBought(TimeUnit.MINUTES, 1) + ", " + demand.getBought(TimeUnit.MINUTES, 5) + ", " + demand.getBought(TimeUnit.MINUTES, 15)).translate(),
											StringUtils.use(" ").translate()));

									meta.setLore(set);
								}
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								stack.setItemMeta(meta);
								if (demand.getPopularity() >= 500) {
									stack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
								}
								item.setElement(stack);
								item.setClick(click -> {
									if (click.getClickType() == ClickType.SHIFT_LEFT) {
											if (demand.getOwner().equals(click.getElement().getUniqueId())) {
												ItemStack copy = new ItemStack(demand.getItem());
												copy.setAmount(1);
												for (int length = 0; length < demand.getAmount(); length++) {
													click.getElement().getWorld().dropItem(click.getElement().getLocation(), copy);
												}
												RetroConomy.getInstance().getManager().deleteItem(demand);
												click.getParent().remove(click.getElement(), true);
												viewGlobalShopPage(click.getElement(), MarketItem.getCategory(demand.getItem().getType())).open(click.getElement());
												return;
											}
									}
									if (click.getClickType() == ClickType.SHIFT_RIGHT) {
											if (demand.getOwner().equals(click.getElement().getUniqueId())) {
												for (ItemStack itemStack : click.getElement().getInventory().getContents()) {
													Optional<MarketItem> d = RetroConomy.getInstance().getManager().getMarketItem(itemStack);

													if (d.isPresent()) {
														MarketItem m = d.get();
														m.setAmount(m.getAmount() + itemStack.getAmount());
														itemStack.setAmount(0);
													}
												}
												viewGlobalShopPage(click.getElement(), MarketItem.getCategory(demand.getItem().getType())).open(click.getElement());
											}
									}
									if (click.getClickType() == ClickType.RIGHT) {
										typeAmount(demand, TransactionType.BUY).open(click.getElement());
									}
								});
							}
						});
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> {
							ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
						});
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p != Menu.Panel.MIDDLE) {
								border.add(p, ed -> {
									ed.setElement(it -> it.setType(Material.IRON_BARS).setTitle(" ").build());
								});
							}
						}
						i.addItem(list);
						i.addItem(filler);
						i.addItem(border);
					})
					.join();
		}

		public static Menu viewGlobalShop() {
			return MenuType.PAGINATED.build()
					.setHost(plugin)
					.setSize(Menu.Rows.SIX)
					.setTitle("The Shop")
					.setProperty(Menu.Property.LIVE_META)
					.setStock(i -> {
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_NEXT);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_RIGHT.get()).setTitle(StringUtils.use("&3Go to the next page →").translate()).build());
							it.setSlot(53);
						});
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_BACK);
							it.setElement(new Item.Edit(SkullType.ARROW_BLACK_LEFT.get()).setTitle(StringUtils.use("&3← Go back a page").translate()).build());
							it.setSlot(45);
						});
						i.addItem(it -> {
							it.setType(ItemElement.ControlType.BUTTON_EXIT);
							it.setElement(new Item.Edit(Material.BARRIER).setTitle("&aClick to refresh.").build());
							it.setSlot(49);
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(Material.HEART_OF_THE_SEA).setTitle(StringUtils.use("&3[&7Player Market&3]").translate()).build());
							it.setSlot(46);
							it.setClick( click -> {
								click.setCancelled(true);
								selectGlobalCategory().open(click.getElement());
							});
						});
						i.addItem(it -> {
							it.setElement(new Item.Edit(CustomHeadLoader.provide("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwNWQ1NWYyMWI0OWEwNzRjZDVlM2RjMjQ0YTVhMDcwZTU1NDRiNTRmYTkyNTRkMmRjMmUxOGYxZTY4MDJmOSJ9fX0=")).setTitle(StringUtils.use("&3[&cMute Notifications&3]").translate()).build());
							it.setSlot(52);
							it.setClick( click -> {
								click.setCancelled(true);
								NotifiableEntity entity = NotifiableEntity.pick(click.getElement());
								editNotifications(entity).open(click.getElement());
							});
						});
						ListElement<SystemItem> list = new ListElement<>(RetroConomy.getInstance().getManager().getInventory().stream().filter(it -> it instanceof SystemItem).map(demand -> (SystemItem)demand).collect(Collectors.toList()));
						list.setLimit(28);
						list.setPopulate((demand, item) -> {
							if (demand != null) {
								ItemStack stack = Items.edit().setType(demand.getItem().getType()).setTitle("&e" + demand.getItem().getType().name().toLowerCase().replace("_", " ")).build();
								ItemMeta meta = stack.getItemMeta();
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
								stack.setItemMeta(meta);
								if (demand.getPopularity() >= 500) {
									stack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
								}
								item.setElement(stack);
								item.setClick(click -> {
									if (click.getClickType() == ClickType.LEFT) {
										attemptTransaction(demand, TransactionType.SELL).open(click.getElement());
									}
									if (click.getClickType() == ClickType.RIGHT) {
										attemptTransaction(demand, TransactionType.BUY).open(click.getElement());
									}
								});
							}
						});
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> {
							ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
						});
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p != Menu.Panel.MIDDLE) {
								border.add(p, ed -> {
									ed.setElement(it -> it.setType(Material.IRON_BARS).setTitle(" ").build());
								});
							}
						}
						i.addItem(list);
						i.addItem(filler);
						i.addItem(border);
					})
					.join();
		}

		public enum TransactionType {
			SELL,
			BUY

		}

	}

}

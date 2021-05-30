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
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.ItemModificationEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
					return AnvilBuilder.from(StringUtils.use("&6Specify an amount to sell.").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new Item.Edit(Material.PAPER).setTitle("&fSpecify an amount &2&m→&r ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to sell", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
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
				case BUY:
					return AnvilBuilder.from(StringUtils.use("&6Specify an amount to buy.").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new Item.Edit(Material.PAPER).setTitle("&fSpecify an amount &2&m→&r ").setLore("&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&7The amount to buy", "&f&m&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").addEnchantment(Enchantment.LOYALTY, 69).setFlags(ItemFlag.HIDE_ENCHANTS).build();
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
				default:
					throw new IllegalStateException("Invalid menu type present!");
			}
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

		public static Menu.Paginated<ItemDemand> browse() {
			return new PaginatedBuilder<>(RetroConomy.getInstance().getManager().getMarket().list())
					.forPlugin(JavaPlugin.getProvidingPlugin(RetroConomy.class))
					.setTitle(StringUtils.use("The Shop").translate())
					.setSize(InventoryRows.SIX)
					.setAlreadyFirst(StringUtils.use("&cYou are already on the first page.").translate())
					.setAlreadyLast(StringUtils.use("&cYou are already on the last page.").translate())
					.setCloseAction(PaginatedCloseAction::clear)
					.setNavigationBack(() -> Items.getItem(Material.BARRIER, "&aClick to refresh."), 49, click -> browse().open(click.getPlayer()))
					.setNavigationLeft(() -> Items.getItem(Material.DARK_OAK_BUTTON, "&aGo back a page."), 48, PaginatedClickAction::sync)
					.setNavigationRight(() -> Items.getItem(Material.DARK_OAK_BUTTON, "&aGo to the next page."), 50, PaginatedClickAction::sync)
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

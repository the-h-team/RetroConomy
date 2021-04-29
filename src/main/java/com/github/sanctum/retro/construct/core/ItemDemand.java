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
import com.github.sanctum.labyrinth.gui.builder.PaginatedBuilder;
import com.github.sanctum.labyrinth.gui.builder.PaginatedClose;
import com.github.sanctum.labyrinth.gui.builder.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.menuman.MenuBuilder;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemDemand extends Modifiable, SellableItem{

	long getRecentBought();

	long getRecentSold();

	long getSold(UUID user);

	long getSoldLast(UUID user);

	long getSold();

	long getBought(UUID user);

	long getBoughtLast(UUID user);

	long getBought();

	double getPopularity();

	String getLastBuyer();

	String getLastSeller();

	Map<String, Long> getBuyerTimeMap();

	Map<String, Long> getSellerTimeMap();

	Map<String, Long> getBuyerMap();

	Map<String, Long> getSellerMap();

	class GUI {

		public static Menu transact(ItemDemand item, Type type) {
			switch (type) {
				case BUY:
					return new MenuBuilder(InventoryRows.THREE, StringUtils.use("Now buying " + item.getItem().getType().name()).translate())
							.addElement(new ItemStack(item.getItem().getType(), 1))
							.setAction(click -> {

							})
							.setLore()
							.setText(StringUtils.use("Click to buy 1 " + item.getItem().getType().name()).translate())
							.assignToSlots()
							.addElement(new ItemStack(item.getItem().getType(), 32))
							.setAction(click -> {

							})
							.setLore()
							.setText(StringUtils.use("Click to buy 32 " + item.getItem().getType().name()).translate())
							.assignToSlots()
							.addElement(new ItemStack(item.getItem().getType(), 64))
							.setAction(click -> {

							})
							.setLore()
							.setText(StringUtils.use("Click to buy 64 " + item.getItem().getType().name()).translate())
							.assignToSlots()
							.addElement(new ItemStack(item.getItem().getType(), -1))
							.setAction(click -> {

							})
							.setLore()
							.setText(StringUtils.use("Click to buy a specified amount of " + item.getItem().getType().name()).translate())
							.assignToSlots()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case SELL:
					return new MenuBuilder(InventoryRows.THREE, StringUtils.use("Now selling " + item.getItem().getType().name()).translate())
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				default:
					throw new IllegalStateException("Invalid menu type present.");
			}
		}

		public static PaginatedMenu browse(Type type) {
			switch (type) {
				case SHOP:
					return new PaginatedBuilder(JavaPlugin.getProvidingPlugin(RetroConomy.class))
							.setTitle(StringUtils.use("The Shop").translate())
							.collect(new LinkedList<>(RetroConomy.getInstance().getManager().getMarket().map(SellableItem::getItem).map(ItemStack::getType).map(Enum::name).collect(Collectors.toList())))
							.setSize(InventoryRows.SIX)
							.setAlreadyFirst(StringUtils.use("Already first").translate())
							.setAlreadyLast(StringUtils.use("Already last").translate())
							.setCloseAction(PaginatedClose::clear)
							.setNavigationBack(() -> Items.getItem(Material.BARRIER, "&cGo back"), 49, click -> {

							})
							.setNavigationLeft(() -> Items.getItem(Material.DARK_OAK_BUTTON, "&aGo back a page."), 48, click -> {

							})
							.setNavigationRight(() -> Items.getItem(Material.DARK_OAK_BUTTON, "&aGo to the next page."), 50, click ->{

							})
							.setupProcess(e -> {
								e.buildItem(() -> {
									ItemDemand demand = RetroConomy.getInstance().getManager().getDemand(Items.getMaterial(e.getContext())).orElse(null);
									if (demand != null) {
										ItemStack i = Items.getItem(demand.getItem().getType(), "&e" + e.getContext());
										ItemMeta meta = i.getItemMeta();
										meta.setLore(Arrays.asList(StringUtils.use(" ").translate(),
												StringUtils.use("&7Right-click to buy.").translate(),
												StringUtils.use("&7Left-click to sell.").translate(),
												StringUtils.use(" ").translate()));
										i.setItemMeta(meta);
										return i;
									}
									throw new IllegalStateException("An invalid shop item was presented while generating the GUI.");
								});
								e.action().setClick(click -> {
									ItemDemand demand = RetroConomy.getInstance().getManager().getDemand(Items.getMaterial(e.getContext())).orElse(null);
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
							.addBorder()
							.setBorderType(Material.BEACON)
							.setFillType(Material.GRAY_STAINED_GLASS_PANE)
							.fill()
							.build();
				default:
					throw new IllegalStateException("Invalid menu type present.");
			}
		}

		public enum Type {
			SHOP, SELL, BUY
		}

	}

}

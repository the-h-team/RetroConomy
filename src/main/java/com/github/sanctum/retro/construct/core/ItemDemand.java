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
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemDemand extends Modifiable, SellableItem{

	long getRecentBought();

	long getRecentSold();

	long getSold(String user);

	long getSoldLast(String user);

	long getSold();

	long getBought(String user);

	long getBoughtLast(String user);

	long getBought();

	double getPopularity();

	String getLastBuyer();

	String getLastSeller();

	Map<String, Long> getBuyerTimeMap();

	Map<String, Long> getSellerTimeMap();

	Map<String, Long> getBuyerMap();

	Map<String, Long> getSellerMap();

	class GUI {

		public static PaginatedMenu browse(Type type) {
			switch (type) {
				case SHOP:
					return new PaginatedBuilder(JavaPlugin.getProvidingPlugin(RetroConomy.class))
							.setTitle(StringUtils.use("The Shop").translate())
							.collect(new LinkedList<>(RetroConomy.getInstance().getManager().getShop().map(SellableItem::getItem).map(ItemStack::getType).map(Enum::name).collect(Collectors.toList())))
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
									if (click.isLeftClick()) {
										click.getPlayer().sendMessage("Now selling " + e.getContext());
									}
									if (click.isRightClick()) {
										click.getPlayer().sendMessage("Now buying " + e.getContext());
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

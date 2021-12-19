/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.command;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.api.RetroAccount;
import com.github.sanctum.retro.construct.core.Shop;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopCommand extends CommandOrientation {

	public ShopCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {

			RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

			if (args.length == 0) {

				sendMessage(player, "&cUsages: &r/shop buy,locate");

				ItemDemand.GUI.viewGlobalShop().open(player);

			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("buy")) {
					if (wallet != null) {
						if (wallet.has(1428.98, player.getWorld())) {
							if (!Shop.has(player)) {
								wallet.withdraw(BigDecimal.valueOf(1428.98), player.getWorld());
								Shop atm = Shop.pick(player);
								player.getWorld().dropItem(player.getLocation(), atm.toItem());
								sendMessage(player, "Place down your shop to begin using it!");
							} else {
								if (player.isOp()) {
									Shop atm = Shop.pick(player);
									player.getWorld().dropItem(player.getLocation(), atm.toItem());
									return;
								}
								sendMessage(player, "&cYou already have an atm!");
							}
						} else {
							sendMessage(player, "&cYou don't have enough money, needed: " + wallet.getBalance().subtract(BigDecimal.valueOf(1428.98)));
						}
					}
				}
				if (args[0].equalsIgnoreCase("locate")) {
					if (Shop.has(player)) {
						Shop atm = Shop.pick(player);
						player.teleport(atm.getLocation());
					} else {
						sendMessage(player, "&cYou don't have an atm!");
					}
				}
				return;
			}

			if (args.length == 2) {

			}

		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.ATM;
import com.github.sanctum.retro.construct.core.RetroAccount;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtmCommand extends CommandOrientation {

	public AtmCommand(@NotNull CommandInformation information) {
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


			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("buy")) {
					if (wallet != null) {
						if (wallet.has(1428.98, player.getWorld())) {
							if (!ATM.has(player)) {
								wallet.withdraw(BigDecimal.valueOf(1428.98), player.getWorld());
								ATM atm = ATM.pick(player);
								player.getWorld().dropItem(player.getLocation(), atm.toItem());
								sendMessage(player, "Place down your ATM to begin using it!");
							} else {
								sendMessage(player, "&cYou already have an atm!");
							}
						} else {
							sendMessage(player, "&cYou don't have enough money.");
						}
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

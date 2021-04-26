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
import com.github.sanctum.retro.util.FileType;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultCommand extends CommandOrientation {

	public DefaultCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {
			if (args.length == 0) {

			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					RetroConomy.getInstance().getManager().getMain().reload();
					FileType.ACCOUNT.get().reload();
					RetroConomy.getInstance().getManager().loadCurrencies();
					RetroConomy.getInstance().getManager().loadShop();
					sendMessage(player, "&aAll configuration reloaded.");
					return;
				}
			}
		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

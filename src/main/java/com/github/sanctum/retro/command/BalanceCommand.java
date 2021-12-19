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
import com.github.sanctum.retro.api.RetroAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BalanceCommand extends CommandOrientation {

	public BalanceCommand(@NotNull CommandInformation information) {
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

				if (RetroConomy.getInstance().getManager().getWallet(player).isPresent()) {
					RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).get();

					String[] balance;
					String bal = RetroConomy.getInstance().getManager().format(wallet.getBalance(player.getWorld()));
					if (RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.format").equals("en")) {
						balance = bal.split("\\.");
					} else {
						balance = bal.split(",");
					}

					String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-balance")).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
					sendMessage(player, format);
				}
			}

			if (args.length == 1) {
				OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
				if (target != null) {
					if (RetroConomy.getInstance().getManager().getWallet(target).isPresent()) {
						RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(target).get();

						String[] balance;
						String bal = RetroConomy.getInstance().getManager().format(wallet.getBalance(player.getWorld()));
						if (RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.format").equals("en")) {
							balance = bal.split("\\.");
						} else {
							balance = bal.split(",");
						}

						String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-balance")).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
						sendMessage(player, format);
					}
				} else {
					sendMessage(player, ConfiguredMessage.getMessage("invalid-target").replace("{PLAYER}", args[0]));
				}
			}


		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.ATM;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.construct.core.RetroWallet;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.PlaceHolder;
import java.util.List;
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

			if (!ATM.has(player)) {
				ATM atm = ATM.pick(player);
				player.getWorld().dropItem(player.getLocation(), atm.get());
				sendMessage(player, "Place down your ATM to begin using it!");
				Schedule.sync(() -> {

					RetroAccount account = RetroConomy.getInstance().getManager().getAccount(player).orElse(null);
					if (account != null) {
						sendMessage(player, "&aUse this card to access your bank account from any atm location.");
						player.getWorld().dropItem(player.getLocation(), account.getDebitCard().get());
					}

				}).wait(2);
			} else {
				sendMessage(player, "&cYou already have an atm!");
			}

			if (RetroConomy.getInstance().getManager().getWallet(player).isPresent()) {
				RetroWallet wallet = RetroConomy.getInstance().getManager().getWallet(player).get();

				String[] balance;
				String bal = RetroConomy.getInstance().getManager().format(wallet.getBalance(player.getWorld()));
				if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
					balance = bal.split("\\.");
				} else {
					balance =bal.split(",");
				}

				String format = PlaceHolder.convert(ConfiguredMessage.getMessage("wallet-balance")).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
				sendMessage(player, format);
			}
		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

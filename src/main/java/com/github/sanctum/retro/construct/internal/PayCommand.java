/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.labyrinth.formatting.TabCompletion;
import com.github.sanctum.labyrinth.formatting.TabCompletionBuilder;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PayCommand extends CommandOrientation {

	public PayCommand(@NotNull CommandInformation information) {
		super(information);
	}

	private final TabCompletionBuilder builder = TabCompletion.build(getLabel());

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return builder.forArgs(args)
				.level(1)
				.completeAt(getLabel())
				.filter(() -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()))
				.collect()
				.get(args.length);
	}

	@Override
	public void player(Player player, String[] args) {

		if (args.length == 2) {
			OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
			if (target != null) {
				if (RetroConomy.getInstance().getManager().getWallet(target).isPresent()) {
					RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(target).get();
					try {
						double amount = Double.parseDouble(args[1]);

						String[] balance;
						String bal = RetroConomy.getInstance().getManager().format(amount);
						if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
							balance = bal.split("\\.");
						} else {
							balance = bal.split(",");
						}

						if (RetroConomy.getInstance().getManager().getWallet(player).isPresent()) {
							RetroAccount wallet2 = RetroConomy.getInstance().getManager().getWallet(player).get();
							if (wallet2.has(amount, player.getWorld())) {
								wallet2.withdraw(BigDecimal.valueOf(amount), player.getWorld());
								wallet.deposit(BigDecimal.valueOf(amount), player.getWorld());
								String format = FormattedMessage.convert(ConfiguredMessage.getMessage("send-money").replace("{PLAYER}", target.getName())).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
								sendMessage(player, format);
								if (target.isOnline()) {
									sendMessage(target.getPlayer(), "&a" + player.getName() + " sent you " + amount);
								}
							} else {
								sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
							}
						}
					} catch (NumberFormatException e) {
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
					}
				}
			} else {
				sendMessage(player, ConfiguredMessage.getMessage("invalid-target").replace("{PLAYER}", args[0]));
			}
		} else {
			sendUsage(player);
		}

	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

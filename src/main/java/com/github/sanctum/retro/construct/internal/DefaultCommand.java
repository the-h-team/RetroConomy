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
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.ItemDemand;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.FormattedMessage;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultCommand extends CommandOrientation {

	public DefaultCommand(@NotNull CommandInformation information) {
		super(information);
	}

	TabCompletionBuilder builder = TabCompletion.build(getLabel());

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return builder.forArgs(args)
				.level(1)
				.completeAt(getLabel())
				.filter(() -> Arrays.asList("set", "give", "take"))
				.collect()
				.level(2)
				.completeAt(getLabel())
				.filter(() -> {
					if (p.hasPermission(getInformation().getPermission() + ".admin")) {
						if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
							return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
						}
					}
					return Collections.emptyList();
				})
				.collect()
				.level(3)
				.completeAt(getLabel())
				.filter(Collections::emptyList)
				.collect()
				.level(4)
				.completeAt(getLabel())
				.filter(() -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()))
				.collect()
				.get(args.length);
	}

	@Override
	public void player(Player player, String[] args) {
		if (player.hasPermission(getInformation().getPermission() + ".admin")) {
			if (args.length == 0) {
				// send help menu
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
				if (args[0].equalsIgnoreCase("shop")) {
					ItemDemand.GUI.browse(ItemDemand.GUI.Type.SHOP).open(player);
				}
				if (args[0].equalsIgnoreCase("reset")) {
					for (WalletAccount ac : RetroConomy.getInstance().getManager().getWallets().list()) {
						for (World w : Bukkit.getWorlds()) {
							ac.setBalance(RetroConomy.getInstance().getManager().getMain().getConfig().getDouble("Options.wallets.starting-balance"), w);
						}
						sendMessage(player, "&e" + ac.getOwner().getName() + " &7wallet balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
					}
					for (BankAccount ac : RetroConomy.getInstance().getManager().getAccounts().list()) {
						for (World w : Bukkit.getWorlds()) {
							ac.setBalance(RetroConomy.getInstance().getManager().getMain().getConfig().getDouble("Options.accounts.starting-balance"), w);
						}
						sendMessage(player, "&e" + Bukkit.getOfflinePlayer(ac.getOwner()).getName() + " &7bank account balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
					}
				}
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("set")) {
					sendMessage(player, "&cUsage: &f/" + getLabel() + " set <playerName> ##.##");
				}
				if (args[0].equalsIgnoreCase("give")) {
					sendMessage(player, "&cUsage: &f/" + getLabel() + " give <playerName> ##.##");
				}
				if (args[0].equalsIgnoreCase("take")) {
					sendMessage(player, "&cUsage: &f/" + getLabel() + " take <playerName> ##.##");
				}
			}

			if (args.length == 3) {

				OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(o -> o.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);

				if (target != null) {
					try {

						double amount = Double.parseDouble(args[2]);

						if (args[0].equalsIgnoreCase("set")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.setBalance(amount);
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-set").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
						if (args[0].equalsIgnoreCase("give")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.deposit(BigDecimal.valueOf(amount));
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-add").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
						if (args[0].equalsIgnoreCase("take")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.withdraw(BigDecimal.valueOf(amount));
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-take").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
					} catch (NumberFormatException e) {

					}

				} else {
					// target not found.
					sendMessage(player, ConfiguredMessage.getMessage("invalid-target").replace("{PLAYER}", args[1]));
				}
			}

			if (args.length == 4) {

				OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(o -> o.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);

				World w = Bukkit.getWorld(args[3]);

				if (w == null) {
					sendMessage(player, "&cThis world doesn't exist or isn't properly loaded!");
					return;
				}

				if (target != null) {
					try {

						double amount = Double.parseDouble(args[2]);

						if (args[0].equalsIgnoreCase("set")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.setBalance(amount, w);
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-set").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
						if (args[0].equalsIgnoreCase("give")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.deposit(BigDecimal.valueOf(amount), w);
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-add").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
						if (args[0].equalsIgnoreCase("take")) {

							RetroConomy.getInstance().getManager().getWallet(target).ifPresent(account -> {
								account.withdraw(BigDecimal.valueOf(amount), w);
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-add").replace("{PLAYER}", target.getName())).next(amount, account.getBalance().doubleValue()));
							});

						}
					} catch (NumberFormatException e) {

					}

				} else {
					sendMessage(player, ConfiguredMessage.getMessage("invalid-target").replace("{PLAYER}", args[1]));
				}

			}

		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

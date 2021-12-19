/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.command;

import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FileReader;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultCommand extends CommandOrientation {

	public DefaultCommand(@NotNull CommandInformation information) {
		super(information);
	}

	SimpleTabCompletion builder = SimpleTabCompletion.empty();

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return builder.fillArgs(args)
				.then(TabCompletionIndex.ONE, "set", "give", "take")
				.then(TabCompletionIndex.TWO, () -> {
					if (p.hasPermission(getInformation().getPermission() + ".admin")) {
						if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
							return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
						}
					}
					return Collections.emptyList();
				})
				.then(TabCompletionIndex.FOUR, Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()))
				.get();
	}

	@Override
	public void player(Player player, String[] args) {
		if (args.length == 0) {
			// send help menu

		}
		if (player.hasPermission(getInformation().getPermission() + ".admin")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					RetroConomy.getInstance().getManager().getMain().getRoot().reload();
					FileReader.ACCOUNT.get().getRoot().reload();
					RetroConomy.getInstance().getManager().loadCurrencies();
					RetroConomy.getInstance().getManager().loadShop();
					sendMessage(player, "&aAll configuration reloaded.");
					return;
				}
				if (args[0].equalsIgnoreCase("reset")) {
					for (WalletAccount ac : RetroConomy.getInstance().getManager().getWallets()) {
						for (World w : Bukkit.getWorlds()) {
							ac.setBalance(RetroConomy.getInstance().getManager().getMain().getRoot().getDouble("Options.wallets.starting-balance"), w);
						}
						sendMessage(player, "&e" + ac.getOwner().getName() + " &7wallet balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
					}
					for (BankAccount ac : RetroConomy.getInstance().getManager().getAccounts()) {
						for (World w : Bukkit.getWorlds()) {
							ac.setBalance(RetroConomy.getInstance().getManager().getMain().getRoot().getDouble("Options.accounts.starting-balance"), w);
						}
						sendMessage(player, "&e" + Bukkit.getOfflinePlayer(ac.getOwner()).getName() + " &7bank account balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
					}
				}

				if (args[0].equalsIgnoreCase("clear")) {
					ItemStack item = player.getInventory().getItemInMainHand();
					RetroConomy.getInstance().getManager().getDemand(item).ifPresent(i -> {
						i.getSellerAmountMap().clear();
						i.getBuyerAmountMap().clear();
						i.getBuyerMap().clear();
						i.getSellerMap().clear();
						i.getBuyerTimeMap().clear();
						i.getSellerTimeMap().clear();
						sendMessage(player, "&aYou cleared all registered history for item " + item.getType().name().toLowerCase().replace("_", " "));
					});
					return;
				}
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("reset")) {
					if (args[1].equalsIgnoreCase("wallets")) {
						for (WalletAccount ac : RetroConomy.getInstance().getManager().getWallets()) {
							for (World w : Bukkit.getWorlds()) {
								ac.setBalance(RetroConomy.getInstance().getManager().getMain().getRoot().getDouble("Options.wallets.starting-balance"), w);
							}
							sendMessage(player, "&e" + ac.getOwner().getName() + " &7wallet balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
						}
					}
					if (args[1].equalsIgnoreCase("banks")) {
						for (BankAccount ac : RetroConomy.getInstance().getManager().getAccounts()) {
							for (World w : Bukkit.getWorlds()) {
								ac.setBalance(RetroConomy.getInstance().getManager().getMain().getRoot().getDouble("Options.accounts.starting-balance"), w);
							}
							sendMessage(player, "&e" + Bukkit.getOfflinePlayer(ac.getOwner()).getName() + " &7bank account balance just got reset to &f&l$" + RetroConomy.getInstance().getManager().format(ac.getBalance()));
						}
					}
				}
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

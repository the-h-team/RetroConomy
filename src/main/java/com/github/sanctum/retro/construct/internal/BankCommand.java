/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FormattedMessage;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BankCommand extends CommandOrientation {

	public BankCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {

			BankAccount account = RetroConomy.getInstance().getManager().getAccount(player).orElse(null);

			if (args.length == 0) {
				// open either GUI? or paginated assortment


			}

			if (args.length == 1) {

				if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {

					if (account == null) {
						// nope
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
						return;
					}

					String[] balance;
					String bal = RetroConomy.getInstance().getManager().format(account.getBalance(player.getWorld()));
					if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
						balance = bal.split("\\.");
					} else {
						balance =bal.split(",");
					}

					String format = FormattedMessage.convert(ConfiguredMessage.getMessage("account-balance")).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
					sendMessage(player, format);
					return;
				}

				if (args[0].equalsIgnoreCase("open")) {
					if (account == null) {
						RetroConomy.getInstance().getManager().loadAccount(new BankAccount(player.getUniqueId(), null, HUID.randomID(), Collections.emptyList()));
						sendMessage(player, ConfiguredMessage.getMessage("account-open"));
					} else {
						// already opened
						sendMessage(player, ConfiguredMessage.getMessage("account-exists"));
					}
				}
				if (args[0].equalsIgnoreCase("close")) {
					if (account != null) {
						RetroConomy.getInstance().getManager().deleteAccount(account);
						sendMessage(player, ConfiguredMessage.getMessage("account-close"));
					} else {
						// noo account
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
				}
				if (args[0].equalsIgnoreCase("deposit")) {
					sendUsage(player);
				}
				if (args[0].equalsIgnoreCase("withdraw")) {
					sendUsage(player);
				}
				if (args[0].equalsIgnoreCase("add")) {
					sendUsage(player);
				}
				if (args[0].equalsIgnoreCase("remove")) {
					sendUsage(player);
				}
				return;
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("joint")) {
					if (account != null) {
						if (account.getOwner().equals(player.getUniqueId())) {
							UUID target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equalsIgnoreCase(args[1])).map(OfflinePlayer::getUniqueId).findFirst().orElse(null);

							if (target != null) {
								if (!account.getJointOwner().equals(target)) {
									account.setJointOwner(target);
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-joint-set")).from(Bukkit.getOfflinePlayer(target)));
								} else {
									// already a member.
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-member-exists")).from(Bukkit.getOfflinePlayer(target)));
								}
							} else {
								// target not found.
								sendMessage(player, ConfiguredMessage.getMessage("account-member-missing").replace("{PLAYER}", args[1]));
							}
						} else {
							sendMessage(player, ConfiguredMessage.getMessage("account-perms-needed"));
						}
					} else {
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (account != null) {
						if (account.getOwner().equals(player.getUniqueId()) || account.getJointOwner().equals(player.getUniqueId())) {
							UUID target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equalsIgnoreCase(args[1])).map(OfflinePlayer::getUniqueId).findFirst().orElse(null);

							if (target != null) {
								if (account.addMember(target).success()) {
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-member-add")).from(Bukkit.getOfflinePlayer(target)));
								} else {
									// already a member.
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-member-exists")).from(Bukkit.getOfflinePlayer(target)));
								}
							} else {
								// target not found.
								sendMessage(player, ConfiguredMessage.getMessage("account-member-missing").replace("{PLAYER}", args[1]));
							}

						} else {
							// not enough account privileges
							sendMessage(player, ConfiguredMessage.getMessage("account-perms-needed"));
						}
					} else {
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
					return;
				}
				if (args[0].equalsIgnoreCase("remove")) {
					if (account != null) {
						if (account.getOwner().equals(player.getUniqueId()) || account.getJointOwner().equals(player.getUniqueId())) {
							UUID target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equalsIgnoreCase(args[1])).map(OfflinePlayer::getUniqueId).findFirst().orElse(null);

							if (target != null) {
								if (account.removeMember(target).success()) {
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-member-remove")).from(Bukkit.getOfflinePlayer(target)));
								} else {
									sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-member-missing")).from(Bukkit.getOfflinePlayer(target)));
								}
							} else {
								// target not found.
								sendMessage(player, ConfiguredMessage.getMessage("account-member-missing").replace("{PLAYER}", args[1]));
							}

						} else {
							// not enough account privileges
							sendMessage(player, ConfiguredMessage.getMessage("account-perms-needed"));
						}
					} else {
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
					return;
				}
				if (args[0].equalsIgnoreCase("deposit")) {
					if (account != null) {
						try {
							double amount = Double.parseDouble(args[1]);

							RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

							if (wallet != null) {
								if (wallet.has(amount, player.getWorld())) {
									wallet.withdraw(BigDecimal.valueOf(amount), player.getWorld());
									player.getWorld().dropItem(player.getLocation(), account.record(TransactionType.DEPOSIT, player, BigDecimal.valueOf(amount), player.getWorld()).toItem());
								}
							}

						} catch (NumberFormatException e) {
							sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[1]));
						}
					} else {
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
				}
				if (args[0].equalsIgnoreCase("withdraw")) {
					if (account != null) {
						try {
							double amount = Double.parseDouble(args[1]);

							RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).orElse(null);

							if (wallet != null) {
								if (account.has(amount, player.getWorld())) {
									player.getWorld().dropItem(player.getLocation(), account.record(TransactionType.WITHDRAW, player, BigDecimal.valueOf(amount), player.getWorld()).toItem());
									wallet.deposit(BigDecimal.valueOf(amount), player.getWorld());
								}
							}

						} catch (NumberFormatException e) {
							sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[1]));
						}
					} else {
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
				}
			}

		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

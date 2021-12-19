/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.command;

import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.TextLib;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.construct.core.BankAccount;
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

	private final SimpleTabCompletion builder = SimpleTabCompletion.empty();

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return builder.fillArgs(args)
				.then(TabCompletionIndex.ONE, "balance", "list", "open", "close", "card", "deposit", "withdraw", "add", "remove")
				.get();
	}

	private int maxAccounts(Player p) {

		if (p.hasPermission("retro.bank.infinite")) {
			return 999999999;
		}

		for (int i = 0; i < 1000; i++) {
			if (p.hasPermission("retro.bank." + i)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {

			BankAccount account = RetroConomy.getInstance().getManager().getAccount(player).orElse(null);

			if (args.length == 0) {
				// open either GUI? or paginated assortment
				PaginatedList<String> list = new PaginatedList<>(Arrays.asList("/&6bank" + " open &f- Open a new primary account.", "/&6bank" + " close &f- Close your current primary account.", "/&6bank" + " list &f- View all of your open accounts.", "/&6bank" + " switch <accountId#> &f- Switch your primary bank account", "/&6bank" + " card &f- Obtain a copy of your debit card."));

				list.limit(5).finish(builder -> builder.setSuffix("").setPrefix("&r▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").setPlayer(player));
				list.start((pagination, page, max) -> {
					Message.form(player).send(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix") + " &r- Retro bank commands. &r(&7/bank #page&r)");
				}).decorate((pagination, object, page, max, placement) -> Message.form(player).send(object));
				list.get(1);
				return;
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
					if (RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.format").equals("en")) {
						balance = bal.split("\\.");
					} else {
						balance = bal.split(",");
					}

					String format = FormattedMessage.convert(ConfiguredMessage.getMessage("account-balance")).next(balance[0], balance.length == 2 ? balance[1] : 0 + "");
					sendMessage(player, format);
					return;
				}

				if (args[0].equalsIgnoreCase("list")) {
					sendMessage(player, "&fYou have &8(&7" + RetroConomy.getInstance().getManager().getAccounts(player.getUniqueId()).size() + "&8) &fopen accounts.");
					sendMessage(player, "&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					for (BankAccount a : RetroConomy.getInstance().getManager().getAccounts(player.getUniqueId())) {
						if (a.isPrimary(player.getUniqueId())) {
							sendComponent(player, TextLib.getInstance().textHoverable("&2Account: &f(&6" + a.getId().toString() + "&f) / (&a$" + RetroConomy.getInstance().getManager().format(a.getBalance()) + "&f) ", "&f[&7&mSwitch&f]", "&cYou already have this account selected."));
						} else {
							sendComponent(player, TextLib.getInstance().textRunnable("&2Account: &f(&6" + a.getId().toString() + "&f) / (&a$" + RetroConomy.getInstance().getManager().format(a.getBalance()) + "&f) ", "&f[&6&lSwitch&f]", "Click to make me the primary account.", "bank switch " + a.getId().toString()));

						}
					}
					sendMessage(player, "&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				}

				if (args[0].equalsIgnoreCase("open")) {
					if (account == null) {
						BankAccount ac = new BankAccount(player.getUniqueId(), null, HUID.randomID(), Collections.emptyList());
						ac.setPrimary(player.getUniqueId(), true);
						RetroConomy.getInstance().getManager().loadAccount(ac);
						sendMessage(player, ConfiguredMessage.getMessage("account-open"));
					} else {
						// already opened
						if (RetroConomy.getInstance().getManager().getMain().getRoot().getBoolean("Options.accounts.multiple-allowed")) {
							if (RetroConomy.getInstance().getManager().getAccounts(player.getUniqueId()).size() >= maxAccounts(player)) {
								sendMessage(player, FormattedMessage.convert(ConfiguredMessage.getMessage("account-cap-reached")).next(maxAccounts(player), 0.0));
							} else {
								BankAccount ac = new BankAccount(player.getUniqueId(), null, HUID.randomID(), Collections.emptyList());
								RetroConomy.getInstance().getManager().loadAccount(ac);
								sendMessage(player, ConfiguredMessage.getMessage("account-open"));
							}
						} else {
							sendMessage(player, ConfiguredMessage.getMessage("account-exists"));
						}
					}
				}
				if (args[0].equalsIgnoreCase("close")) {
					if (account != null) {
						if (account.getOwner().equals(player.getUniqueId())) {
							RetroConomy.getInstance().getManager().deleteAccount(account);
							RetroConomy.getInstance().getManager().getAccounts(player.getUniqueId()).stream().findFirst().ifPresent(a -> a.setPrimary(player.getUniqueId(), true));
							sendMessage(player, ConfiguredMessage.getMessage("account-close"));
						} else {
							sendMessage(player, ConfiguredMessage.getMessage("account-perms-needed"));
						}
					} else {
						// noo account
						sendMessage(player, ConfiguredMessage.getMessage("account-missing"));
					}
				}
				if (args[0].equalsIgnoreCase("card")) {
					if (account != null) {
						if (!player.getInventory().contains(account.getDebitCard().toItem())) {
							sendMessage(player, "&aUse this card to access your bank account from any atm location.");
							player.getWorld().dropItem(player.getLocation(), account.getDebitCard().toItem());
						} else {
							sendMessage(player, "&cYou already have a copy of this accounts card within your inventory!");
						}
					}
				}
				if (args[0].equalsIgnoreCase("deposit")) {
					sendMessage(player, "&cUsage: &r/bank deposit <##.##>");
				}
				if (args[0].equalsIgnoreCase("withdraw")) {
					sendMessage(player, "&cUsage: &r/bank withdraw <##.##>");
				}
				if (args[0].equalsIgnoreCase("add")) {
					sendMessage(player, "&cUsage: &r/bank add <playerName>");
				}
				if (args[0].equalsIgnoreCase("remove")) {
					sendMessage(player, "&cUsage: &r/bank remove <playerName>");
				}
				if (args[0].equalsIgnoreCase("joint")) {
					sendMessage(player, "&cUsage: &r/bank joint <playerName>");
				}
				if (args[0].equalsIgnoreCase("switch")) {
					sendMessage(player, "&cUsage: &r/bank switch <accountId>");
				}
				return;
			}

			if (args.length == 2) {

				if (args[0].equalsIgnoreCase("switch")) {

					if (account == null) {

						return;
					}

					try {
						HUID id = HUID.fromString(args[1]);
						BankAccount a = RetroConomy.getInstance().getManager().getAccount(id).orElse(null);
						if (a != null) {
							if (a.getOwner().equals(player.getUniqueId()) || a.getJointOwner() != null && a.getJointOwner().equals(player.getUniqueId()) || a.getMembers().contains(player.getUniqueId())) {
								account.setPrimary(player.getUniqueId(), false);
								a.setPrimary(player.getUniqueId(), true);
								sendMessage(player, "&aYou have updated account &6" + a.getId().toString() + "&a as primary funding source.");
							}
						}
					} catch (Exception e) {

					}

					return;
				}

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
							player.getWorld().dropItem(player.getLocation(), account.record(TransactionType.DEPOSIT, player, BigDecimal.valueOf(amount), player.getWorld()).toItem());
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
							player.getWorld().dropItem(player.getLocation(), account.record(TransactionType.WITHDRAW, player, BigDecimal.valueOf(amount), player.getWorld()).toItem());
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

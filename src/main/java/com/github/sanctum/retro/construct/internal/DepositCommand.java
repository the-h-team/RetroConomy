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
import com.github.sanctum.retro.construct.core.Currency;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.CurrencyType;
import com.github.sanctum.retro.util.FormattedMessage;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DepositCommand extends CommandOrientation {

	public DepositCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {
			Optional<WalletAccount> walletOptional = RetroConomy.getInstance().getManager().getWallet(player);
			if (walletOptional.isPresent()) {
				RetroAccount wallet = walletOptional.get();

				if (args.length == 0) {
					BigDecimal dep = BigDecimal.valueOf(0.0);
					for (ItemStack item : player.getInventory().getContents()) {

						if (CurrencyType.match(item).isPresent()) {
							Currency c = CurrencyType.match(item).get();
							int count = item.getAmount();
							if (RetroConomy.getInstance().currencyRemoval(player, c, count).isTransactionSuccess()) {
								double amount = c.getWorth() * count;
								dep = dep.add(BigDecimal.valueOf(amount));
							}
						}
					}
					if (dep.doubleValue() > 0) {
						wallet.deposit(dep, player.getWorld());
						String[] balance;
						if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
							balance = String.valueOf(dep.doubleValue()).split("\\.");
						} else {
							balance = String.valueOf(dep.doubleValue()).split(",");
						}
						String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-deposit")).next(Double.parseDouble(balance[0]), Double.parseDouble(balance.length == 2 ? balance[1] : 0 + ""));
						sendMessage(player, format);
					} else {
						sendMessage(player, "&cYou have no money to deposit. Valid types: " + Arrays.toString(RetroConomy.getInstance().getManager().getCurrencyNames()));
					}
					return;
				}

				if (args.length == 1) {
					try {
						int amount = Integer.parseInt(args[0]);
						for (ItemStack it : player.getInventory().getContents()) {
							if (CurrencyType.match(it).isPresent()) {
								Currency c = CurrencyType.match(it).get();
								double money = c.getWorth() * amount;
								if (RetroConomy.getInstance().currencyRemoval(player, c, amount).isTransactionSuccess()) {
									wallet.deposit(BigDecimal.valueOf(money), player.getWorld());
									String[] balance;
									if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
										balance = String.valueOf(money).split("\\.");
									} else {
										balance = String.valueOf(money).split(",");
									}
									String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-deposit")).next(Double.parseDouble(balance[0]), Double.parseDouble(balance.length == 2 ? balance[1] : 0 + ""));
									sendMessage(player, format);
								}
								return;
							}
						}
						sendMessage(player, "&cIt looks like something went wrong. Incorrect syntax or no money to deposit.");

					} catch (NumberFormatException e) {
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
					}
				}

			}
		}
	}

	@Override
	public void console(CommandSender sender, String[] args) {
		playerOnly(sender);
	}
}

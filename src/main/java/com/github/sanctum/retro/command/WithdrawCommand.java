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
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.api.RetroAccount;
import com.github.sanctum.retro.construct.core.Currency;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.CurrencyType;
import com.github.sanctum.retro.util.FormattedMessage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithdrawCommand extends CommandOrientation {

	SimpleTabCompletion completion = SimpleTabCompletion.empty();

	public WithdrawCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return completion.fillArgs(args)
				.then(TabCompletionIndex.TWO, () -> {
					List<String> list = new ArrayList<>();
					for (String s : RetroConomy.getInstance().getManager().getCurrencyNames()) {
						list.add(ChatColor.stripColor(color(s)));
					}
					return list;
				})
				.get();
	}

	@Override
	public void player(Player player, String[] args) {
		if (testPermission(player)) {
			if (RetroConomy.getInstance().getManager().getWallet(player).isPresent()) {
				RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(player).get();

				if (args.length == 0) {
					sendUsage(player);
				}

				if (args.length == 1) {
					try {
						int amount = Integer.parseInt(args[0]);
						Optional<Currency> firstDollar = RetroConomy.getInstance().getManager().getAcceptableCurrencies().stream().filter(c -> c.getType() == CurrencyType.DOLLAR).findFirst();

						if (firstDollar.isPresent()) {
							Currency c = firstDollar.get();
							double cost = c.getWorth() * amount;
							String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-withdraw")).from(c.toItem().getItemMeta().getDisplayName(), cost, 0);
							if (wallet.has(cost, player.getWorld())) {
								wallet.withdraw(BigDecimal.valueOf(cost), player.getWorld());
								for (int i = 0; i < amount; i++) {
									player.getLocation().getWorld().dropItem(player.getLocation(), c.toItem());
								}
								sendMessage(player, format);
							} else {
								sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
							}

						} else {
							sendMessage(player, "&cThere is no dollar item present. Configure one then reload.");
						}


					} catch (NumberFormatException e) {
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
					}
				}

				if (args.length == 2) {
					boolean isMajor = StringUtils.use(args[1]).containsIgnoreCase(ChatColor.stripColor(color(RetroConomy.getInstance().getManager().getMajorSingular())));

					try {
						Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						sendMessage(player, ConfiguredMessage.getMessage("invalid-amount").replace("{AMOUNT_1}", args[0]));
						return;
					}

					int amount = Integer.parseInt(args[0]);

					if (isMajor) {
						Optional<Currency> firstDollar = RetroConomy.getInstance().getManager().getAcceptableCurrencies().stream().filter(c -> c.getType() == CurrencyType.DOLLAR).findFirst();
						if (firstDollar.isPresent()) {
							Currency dollar = firstDollar.get();
							double cost = dollar.getWorth() * amount;
							String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-withdraw")).from(RetroConomy.getInstance().getManager().getMajorSingular(), cost, 0);
							if (wallet.has(cost, player.getWorld())) {
								wallet.withdraw(BigDecimal.valueOf(cost), player.getWorld());
								for (int i = 0; i < amount; i++) {
									player.getLocation().getWorld().dropItem(player.getLocation(), dollar.toItem());
								}
								sendMessage(player, format);
							} else {
								sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
							}
						} else {
							sendMessage(player, "&cThere is no dollar item present. Configure one then reload.");
						}
					} else {
						Optional<Currency> firstChange = RetroConomy.getInstance().getManager().getAcceptableCurrencies().stream().filter(c -> c.getType() == CurrencyType.CHANGE).findFirst();
						if (firstChange.isPresent()) {
							Currency change = firstChange.get();
							double cost = change.getWorth() * amount;
							String format = FormattedMessage.convert(ConfiguredMessage.getMessage("wallet-withdraw")).from(RetroConomy.getInstance().getManager().getMinorPlural(), cost, 0);
							if (wallet.has(cost, player.getWorld())) {
								wallet.withdraw(BigDecimal.valueOf(cost), player.getWorld());
								for (int i = 0; i < amount; i++) {
									player.getLocation().getWorld().dropItem(player.getLocation(), change.toItem());
								}
								sendMessage(player, format);
							} else {
								sendMessage(player, ConfiguredMessage.getMessage("wallet-insufficient"));
							}
						} else {
							sendMessage(player, "&cThere is no change item present. Configure one then reload.");
						}
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

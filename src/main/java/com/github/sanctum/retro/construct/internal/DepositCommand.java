package com.github.sanctum.retro.construct.internal;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.command.CommandInformation;
import com.github.sanctum.retro.command.CommandOrientation;
import com.github.sanctum.retro.construct.core.RetroWallet;
import com.github.sanctum.retro.construct.item.Currency;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.PlaceHolder;
import java.math.BigDecimal;
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
			Optional<RetroWallet> walletOptional = RetroConomy.getInstance().getManager().getWallet(player);
			if (walletOptional.isPresent()) {
				RetroWallet wallet = walletOptional.get();

				ItemStack hand = player.getInventory().getItemInMainHand();

				if (args.length == 0) {

					if (Currency.match(hand).isPresent()) {
						Currency c = Currency.match(hand).get();
						int count = hand.getAmount();
						double amount = c.getWorth() * count;
						wallet.deposit(BigDecimal.valueOf(amount), player.getWorld());
					} else {
						sendMessage(player, "&cYou have no money to deposit. Valid types: " + RetroConomy.getInstance().getManager().getCurrencyNames().toString());
					}
					return;
				}

				if (args.length == 1) {
					try {
						int amount = Integer.parseInt(args[0]);
						for (ItemStack it : player.getInventory().getContents()) {
							if (Currency.match(it).isPresent()) {
								Currency c = Currency.match(it).get();
								double money = c.getWorth() * amount;
								if (RetroConomy.getInstance().currencyRemoval(player, c, amount).isTransactionSuccess()) {
									wallet.deposit(BigDecimal.valueOf(money), player.getWorld());
									String[] balance;
									if (RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.format").equals("en")) {
										balance = String.valueOf(money).split("\\.");
									} else {
										balance = String.valueOf(money).split(",");
									}
									String format = PlaceHolder.convert(ConfiguredMessage.getMessage("wallet-deposit")).next(Double.parseDouble(balance[0]), Double.parseDouble(balance.length == 2 ? balance[1] : 0 + ""));
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

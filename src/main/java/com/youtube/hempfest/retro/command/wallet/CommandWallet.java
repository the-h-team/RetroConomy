package com.youtube.hempfest.retro.command.wallet;

import com.github.sanctum.labyrinth.formatting.string.PaginatedAssortment;
import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.data.Config;
import com.youtube.hempfest.retro.util.Coin;
import com.youtube.hempfest.retro.util.RetroMisc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandWallet extends BukkitCommand {

	public CommandWallet() {
		super("wallet");
	}

	public List<String> helpMenu() {
		return new ArrayList<>(Arrays.asList("&6|&7)&f /wallet balance",
				"&6|&7)&f /wallet &cwithdraw &f<amount>",
				"&6|&7)&f /wallet &adeposit &f<amount>"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {

			return true;
		}

		Player p = (Player) sender;

		RetroAPI api = RetroAPI.getInstance();

		Message msg = new Message(p, "&f[&6RetroConomy&f]");

		List<Material> mats = new ArrayList<>(Arrays.asList(Coin.getMajor().getType(), Coin.getMinor().getType()));
		mats.addAll(Coin.getAltMap().keySet());

		if (args.length == 0) {
			// help menu
			PaginatedAssortment assortment = new PaginatedAssortment(p, helpMenu());
			assortment.setNavigateCommand("");
			assortment.setLinesPerPage(5);
			assortment.setListBorder("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			assortment.setListTitle("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			msg.send("- Wallet Help.");
			assortment.export(1);
			return true;
		}

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("balance")) {
				// balance
				msg.send("&aCurrent balance: &f" + api.getWalletBalance(p, p.getWorld().getName()));
				return true;
			}
			if (args[0].equalsIgnoreCase("deposit")) {
				// account name needed.
				msg.send("&c&oYou need to specify an amount");
				return true;
			}
			if (args[0].equalsIgnoreCase("withdraw")) {
				// account name needed.
				msg.send("&c&oYou need to specify an amount");
				return true;
			}
			return true;
		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("deposit")) {
				try {
					int amount = Integer.parseInt(args[1]);
					double success = RetroMisc.depositItems(p, amount);
					if (success > 0) {
						msg.send(RetroMisc.format(p, Config.getMessage(Config.ResponseType.VALID, "money-deposit"), success));
					} else {
						msg.send(Config.getMessage(Config.ResponseType.NON_VALID, "money-deposit-fail"));
					}
				} catch (NumberFormatException e) {
					if (args[1].equalsIgnoreCase("all")) {
						double total = 0.0;
						for (ItemStack item : p.getInventory().getContents()) {
							if (item != null && mats.contains(item.getType())) {
								int size = item.getAmount();
								double amount = RetroMisc.depositItems(p, size);
								if (amount > 0) {
									total += amount;
								}
							}
						}
						if (total == 0) {
							return true;
						}
						msg.send(RetroMisc.format(p, Config.getMessage(Config.ResponseType.VALID, "money-deposit"), total));
						return true;
					}
					msg.send(Config.getMessage(Config.ResponseType.NON_VALID, "invalid-amount"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("withdraw")) {
				msg.send("&7Invalid usage : &c/withdraw &famount &9<" + Coin.majorSingular() + ", " + Coin.minorSingular() + ">");
				return true;
			}
			return true;
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("withdraw")) {
				try {
					int amount = Integer.parseInt(args[1]);
					RetroMisc.WithdrawType type = RetroMisc.WithdrawType.MAJOR;
					if (args[2].equalsIgnoreCase(Coin.minorSingular())) {
						type = RetroMisc.WithdrawType.MINOR;
					}
					double success = RetroMisc.withdrawItems(p, amount, type);

					if (success > 0) {
						msg.send(RetroMisc.format(p, Config.getMessage(Config.ResponseType.VALID, "money-withdraw"), success));
					} else {
						msg.send(Config.getMessage(Config.ResponseType.NON_VALID, "money-insufficient"));
					}
					return true;

				} catch (NumberFormatException e) {
					msg.send(Config.getMessage(Config.ResponseType.NON_VALID, "invalid-amount"));
				}
				msg.send("&7Invalid usage : &c/withdraw &famount &9<" + Coin.majorSingular() + ", " + Coin.minorSingular() + ">");
				return true;
			}
		}

		return false;
	}
}

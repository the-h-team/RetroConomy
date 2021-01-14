package com.youtube.hempfest.retro.command.wallet;

import com.youtube.hempfest.hempcore.formatting.string.PaginatedAssortment;
import com.youtube.hempfest.hempcore.library.Message;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.construct.economy.Economy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

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

		if (args.length == 0) {
			// help menu
			PaginatedAssortment assortment = new PaginatedAssortment(p, helpMenu());
			assortment.setNavigateCommand("");
			assortment.setLinesPerPage(5);
			assortment.setListBorder("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			assortment.setListTitle("&f[&6RetroConomy&f] Wallet Help.");
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
			String account = args[1];
			if (args[0].equalsIgnoreCase("deposit")) {
				try {
					double amount = Double.parseDouble(args[1]);
					msg.send("&a&oAttempting wallet deposit.");
					api.depositWallet(p, p.getWorld().getName(), BigDecimal.valueOf(amount));
					msg.send("&2&oNew balance: &f" + api.getWalletBalance(p, p.getWorld().getName()));
				} catch (NumberFormatException e) {
					msg.send("&c&oInvalid deposit amount.");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("withdraw")) {
				try {
					double amount = Double.parseDouble(args[1]);
					msg.send("&a&oAttempting wallet withdrawal.");
					if (api.walletHas(p, p.getWorld().getName(), BigDecimal.valueOf(amount))) {
						api.withdrawWallet(p, p.getWorld().getName(), BigDecimal.valueOf(amount));
						msg.send("&2&oNew balance: &f" + api.getWalletBalance(p, p.getWorld().getName()));
					} else {
						msg.send("&c&oYou don't have enough money.");
						return true;
					}
				} catch (NumberFormatException e) {
					msg.send("&c&oInvalid withdraw amount.");
					return true;
				}
				return true;
			}

			return true;
		}

		return false;
	}
}

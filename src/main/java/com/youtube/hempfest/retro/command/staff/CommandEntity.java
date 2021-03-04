package com.youtube.hempfest.retro.command.staff;

import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import java.math.BigDecimal;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CommandEntity extends BukkitCommand {

	public CommandEntity() {
		super("entity");
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
			return true;
		}

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("balance")) {
				// account name needed.
				msg.send("&c&oYou need to specify an account");
				return true;
			}
			if (args[0].equalsIgnoreCase("open")) {
				// account name needed.
				msg.send("&c&oYou need to specify an account id");
				return true;
			}
			if (args[0].equalsIgnoreCase("close")) {
				// account name needed.
				msg.send("&c&oYou need to specify an account");
				return true;
			}
			return true;
		}

		if (args.length == 2) {
			String account = args[1];
			if (args[0].equalsIgnoreCase("balance")) {
				if (!api.accountExists(FundingSource.ENTITY_ACCOUNT, account, p.getWorld().getName())) {
					msg.send("&c&oAn account by the name of " + '"' + account + '"' + " was not found in this world.");
				} else {
					if (api.isAccountMember(FundingSource.ENTITY_ACCOUNT, account, p.getWorld().getName(), p)) {
						double balance = api.getAccountBalance(FundingSource.ENTITY_ACCOUNT, account, p.getWorld().getName()).doubleValue();
						msg.send("&a&oYour current balance for account " + '"' + account + '"' + " is: " + balance);
					} else {
						msg.send("&c&oYou are not permitted to view information for this account.");
						return true;
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("open")) {
				if (!api.accountExists(FundingSource.ENTITY_ACCOUNT, account, p.getWorld().getName())) {
					api.createAccount(FundingSource.ENTITY_ACCOUNT, p, account, p.getWorld().getName());
					msg.send("&a&oNew entity account opened under id: &f" + account);
				} else {
					msg.send("&c&oAn account with this name already exists within this world.");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("close")) {
				if (!api.accountExists(FundingSource.ENTITY_ACCOUNT, account, p.getWorld().getName())) {
					msg.send("&c&oAn account with this name does not exist within this world.");
				} else {
					if (api.isAccountOwner(FundingSource.ENTITY_ACCOUNT, account, p) || api.isAccountJointOwner(FundingSource.ENTITY_ACCOUNT, account, p)) {
						api.deleteAccount(FundingSource.ENTITY_ACCOUNT, account);
						msg.send("&4&oEntity account under id " + '"' + account + '"' + " has been closed.");
					} else {
						msg.send("&c&oYou do not have permission to close this account.");
						return true;
					}
				}
				return true;
			}

			return true;
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("deposit")) {
				try {
					if (api.isAccountMember(FundingSource.ENTITY_ACCOUNT, args[1], p)) {
						double amount = Double.parseDouble(args[2]);
						api.depositAccount(FundingSource.ENTITY_ACCOUNT, args[1], BigDecimal.valueOf(amount));
						double balance = api.getAccountBalance(FundingSource.ENTITY_ACCOUNT, args[1]).doubleValue();
						msg.send("&6&oYour account " + args[1] + " now has a balance of &f" + balance);
					} else {
						msg.send("&c&oYou are not permitted to deposit into this account.");
						return true;
					}
				} catch (NumberFormatException e) {
					msg.send("&c&oInvalid deposit amount.");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("withdraw")) {
				try {
					if (api.isAccountOwner(FundingSource.ENTITY_ACCOUNT, args[1], p) || api.isAccountJointOwner(FundingSource.ENTITY_ACCOUNT, args[1], p)) {
						double amount = Double.parseDouble(args[2]);
						api.depositAccount(FundingSource.ENTITY_ACCOUNT, args[1], BigDecimal.valueOf(amount));
						double balance = api.getAccountBalance(FundingSource.ENTITY_ACCOUNT, args[1]).doubleValue();
						msg.send("&6&oYour account " + args[1] + " now has a balance of &f" + balance);
					} else {
						msg.send("&c&oYou are not permitted to withdraw from this account.");
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

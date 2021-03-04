package com.youtube.hempfest.retro.command.embedded;

import com.github.sanctum.labyrinth.formatting.string.PaginatedAssortment;
import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.util.Coin;
import com.youtube.hempfest.retro.util.RetroMisc;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandDeposit extends BukkitCommand {

	public CommandDeposit() {
		super("deposit");
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {



		return super.tabComplete(sender, alias, args);
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
			msg.send("&7Invalid usage : &c/deposit &f<##.##, all>");
			return true;
		}

		if (args.length == 1) {
			Bukkit.dispatchCommand(p, "wallet deposit " + args[0]);
			return true;
		}

		return false;
	}
}

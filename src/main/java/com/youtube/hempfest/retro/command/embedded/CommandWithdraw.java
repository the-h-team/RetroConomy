package com.youtube.hempfest.retro.command.embedded;

import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.util.Coin;
import com.youtube.hempfest.retro.util.RetroMisc;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CommandWithdraw extends BukkitCommand {

	public CommandWithdraw() {
		super("withdraw");
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
			msg.send("&7Invalid usage : &c/withdraw &famount &9<" + Coin.majorSingular() + ", " + Coin.minorSingular() + ">");
			return true;
		}
		if (args.length == 1) {
			msg.send("&7Invalid usage : &c/withdraw &famount &9<" + Coin.majorSingular() + ", " + Coin.minorSingular() + ">");
			return true;
		}
		if (args.length == 2) {
			Bukkit.dispatchCommand(p, "wallet withdraw " + args[0] + " " + args[1]);
			return true;
		}
		return true;
	}
}

package com.github.sanctum.retro.command;

import com.github.sanctum.labyrinth.command.CommandBuilder;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandOrientation extends Command {

	private final CommandInformation information;

	protected CommandOrientation(@NotNull CommandInformation information) {
		super(information.getLabel());
		this.information = information;
		if (!information.getAliases().isEmpty()) {
			setAliases(information.getAliases());
		}
		if (information.getPermission() != null) {
			setPermission(information.getPermission());
		}
		setLabel(information.getLabel());
		setDescription(information.getDescription());
		setPermissionMessage(color("&cYou don't have permission: &f'<permission>'"));
		setUsage(information.getUsage());
		CommandBuilder.register(this);
	}

	protected final CommandInformation getInformation() {
		return information;
	}

	protected final String color(String text) {
		return StringUtils.use(text).translate();
	}

	protected final void sendMessage(Player player, String msg) {
		Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send(msg);
	}

	protected final void sendUsage(Player player) {
		sendMessage(player, getInformation().getUsage());
	}

	protected final void playerOnly(CommandSender sender) {
		sender.sendMessage("This is a player command only!");
	}

	public abstract @Nullable List<String> complete(Player p, String[] args);

	public abstract void player(Player player, String[] args);

	public abstract void console(CommandSender sender, String[] args);

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (complete((Player)sender, args) != null) {
			return Objects.requireNonNull(complete((Player) sender, args));
		}
		return super.tabComplete(sender, alias, args);
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

		if (!(sender instanceof Player)) {
			console(sender, args);
			return true;
		}
		player((Player)sender, args);
		return true;
	}
}

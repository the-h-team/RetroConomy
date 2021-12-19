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
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.TextLib;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.CommandInformation;
import com.github.sanctum.retro.api.CommandOrientation;
import com.github.sanctum.retro.construct.core.WalletAccount;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopCommand extends CommandOrientation {

	public TopCommand(@NotNull CommandInformation information) {
		super(information);
	}

	@Override
	public @Nullable List<String> complete(Player p, String[] args) {
		return null;
	}

	@Override
	public void player(Player player, String[] args) {
		List<WalletAccount> list = new LinkedList<>(RetroConomy.getInstance().getManager().getWallets());
		PaginatedList<WalletAccount> t = new PaginatedList<>(list)
				.limit(10)
				.compare((o1, o2) -> Double.compare(o2.getBalance().doubleValue(), o1.getBalance().doubleValue()))
				.start((pagination, page, max) -> {
					sendMessage(player, "&eRichest players of all time.");
					Message.form(player).send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				});

		t.finish((pagination, page, max) -> {
			Message.form(player).send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			TextLib component = TextLib.getInstance();
			int next = page + 1;
			int last = Math.max(page - 1, 1);
			List<BaseComponent> toSend = new LinkedList<>();
			if (page == 1) {
				if (page == max) {
					toSend.add(component.textHoverable("", "&8« ", "&cYou are on the first page already."));
					toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
					toSend.add(component.textHoverable("", " &8»", "&cYou are already on the last page."));
					player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
					return;
				}
				toSend.add(component.textHoverable("", "&8« ", "&cYou are on the first page already."));
				toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
				toSend.add(component.execute(() -> t.get(next), component.textHoverable("", " &3»", "&aGoto the next page.")));
				player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
				return;
			}
			if (page == max) {
				toSend.add(component.execute(() -> t.get(last), component.textHoverable("", "&3« ", "&aGoto the previous page.")));
				toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
				toSend.add(component.textHoverable("", " &8»", "&cYou are already on the last page."));
				player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
				return;
			}
			if (next <= max) {
				toSend.add(component.execute(() -> t.get(last), component.textHoverable("", "&3« ", "&aGoto the previous page.")));
				toSend.add(component.textHoverable("&f<&7" + page + "&f/&7" + max + "&f>", "", ""));
				toSend.add(component.execute(() -> t.get(next), component.textHoverable("", " &3»", "&aGoto the next page.")));
				player.spigot().sendMessage(toSend.toArray(new BaseComponent[0]));
			}
		}).decorate((pagination, wallet, page, max, placement) -> {
			Message.form(player).send(" &7#&3" + placement + " &e" + wallet.getOwner().getName() + " &f&l: $&6" + RetroConomy.getInstance().getManager().format(pagination.format(wallet.getBalance(), 2)));
		}).get(1);
	}

	@Override
	public void console(CommandSender sender, String[] args) {

	}
}

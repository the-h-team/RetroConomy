package com.youtube.hempfest.retro.events.listener;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.account.Wallet;
import com.github.sanctum.economy.construct.entity.EconomyEntity;
import com.github.sanctum.economy.construct.entity.types.PlayerEntity;
import com.github.sanctum.economy.construct.events.AsyncTransactionEvent;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import java.math.BigDecimal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();
		Schedule.async(() -> {
			if (!RetroConomy.getAdvancedEconomy().getWallet(player).exists()) {
				RetroConomy.getAdvancedEconomy().getWallet(player).setBalance(BigDecimal.ZERO);
			}
		}).wait(30);
	}

	@EventHandler
	public void transactionAnnounce(AsyncTransactionEvent e) {
		final EconomyAction action = e.getEconomyAction();
		final EconomyEntity holder = action.getActiveHolder();
		if (!(holder instanceof PlayerEntity)) {
			return;
		}
		final Player player = ((PlayerEntity) holder).getPlayer().getPlayer();
		if (player != null) {
			Message msg = new Message(player, "&f[&6RetroConomy&f]");
			msg.send(action.getInfo());
		}
	}

}

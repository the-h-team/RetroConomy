package com.youtube.hempfest.retro.events.listener;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.events.AsyncTransactionEvent;
import com.youtube.hempfest.hempcore.library.Message;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!RetroConomy.getInstance().walletDir.getConfig().isConfigurationSection("Index." + p.getUniqueId().toString())) {
			RetroAPI.getInstance().walletSetBalance(p, p.getWorld().getName(), BigDecimal.ZERO);
		}
	}

	@EventHandler
	public void transactionAnnounce(AsyncTransactionEvent e) {
		EconomyAction action = e.getEconomyAction();
		if (!action.getActiveHolder().friendlyName().equals(RetroConomy.getInstance().getName())) {
			UUID player = UUID.fromString(action.getActiveHolder().id());
			Player target = Bukkit.getPlayer(player);
			if (target != null) {
				Message msg = new Message(target, "&f[&6RetroConomy&f]");
				msg.send(action.getInfo());
			}
		}
	}

}

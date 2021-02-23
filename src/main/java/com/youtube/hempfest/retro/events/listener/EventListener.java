package com.youtube.hempfest.retro.events.listener;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.Wallet;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.economy.construct.entity.types.PlayerEntity;
import com.youtube.hempfest.economy.construct.events.AsyncTransactionEvent;
import com.youtube.hempfest.hempcore.library.Message;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import java.math.BigDecimal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			final Player player = e.getPlayer();
			@Override
			public void run() {
				if (!RetroConomy.getInstance().walletDir.getConfig().isConfigurationSection("Index." + player.getUniqueId().toString())) {
					RetroAPI.getInstance().walletSetBalance(player, player.getWorld().getName(), BigDecimal.ZERO);
				}
				if (!RetroConomy.getTokenEconomy().hasWallet(player)) {
					RetroConomy.getTokenEconomy().getWallet(player).setBalance(new BigDecimal("420"));
				} else {
					final Wallet wallet = RetroConomy.getTokenEconomy().getWallet(player);
					System.out.println("Player " + player.getName() + " has " + wallet.getBalance());
					System.out.println("Adding 20 to balance...");
					wallet.deposit(new BigDecimal("20")).log();
					System.out.println("Taking 10 from balance...");
					wallet.withdraw(new BigDecimal("10")).log();
					System.out.println("New Balance: " + wallet.getBalance());
				}
			}
		}.runTaskLaterAsynchronously(RetroConomy.getInstance(), 30L); // Delaying by a few moments so we can see both announcements
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

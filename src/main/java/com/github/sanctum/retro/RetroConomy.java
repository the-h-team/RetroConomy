/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.api.RetroAPI;
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.Currency;
import com.github.sanctum.retro.construct.core.ItemDemand;
import com.github.sanctum.retro.construct.core.MarketItem;
import com.github.sanctum.retro.construct.core.Shop;
import com.github.sanctum.retro.construct.core.SpecialItem;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.construct.internal.BalanceCommand;
import com.github.sanctum.retro.construct.internal.BankCommand;
import com.github.sanctum.retro.construct.internal.BuyCommand;
import com.github.sanctum.retro.construct.internal.DefaultCommand;
import com.github.sanctum.retro.construct.internal.DepositCommand;
import com.github.sanctum.retro.construct.internal.PayCommand;
import com.github.sanctum.retro.construct.internal.RetroCommand;
import com.github.sanctum.retro.construct.internal.SellCommand;
import com.github.sanctum.retro.construct.internal.ShopCommand;
import com.github.sanctum.retro.construct.internal.TopCommand;
import com.github.sanctum.retro.construct.internal.WithdrawCommand;
import com.github.sanctum.retro.enterprise.EnterpriseEconomy;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.CurrencyType;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.FormattedMessage;
import com.github.sanctum.retro.vault.VaultEconomy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RetroConomy extends JavaPlugin implements RetroAPI, Listener {

	private static RetroConomy instance;
	private RetroManager manager;

	public static RetroAPI getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onDisable() {

		FileManager manager = FileType.ACCOUNT.get();
		FileManager items = FileType.MISC.get("Items");
		for (ItemDemand item : getManager().getMarket().sort()) {
			if (item instanceof SystemItem) {
				if (item.getBuyerMap().isEmpty()) {
					items.getConfig().set("Items." + item.toString() + ".usage-purchase", null);
					items.getConfig().createSection("Items." + item.toString() + ".usage-purchase");
				} else {
					for (Map.Entry<String, Long> entry : item.getBuyerMap().entrySet()) {
						items.getConfig().set("Items." + item.toString() + ".usage-purchase." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				if (item.getSellerMap().isEmpty()) {
					items.getConfig().set("Items." + item.toString() + ".usage-sold", null);
					items.getConfig().createSection("Items." + item.toString() + ".usage-sold");
				} else {
					for (Map.Entry<String, Long> entry : item.getSellerMap().entrySet()) {
						items.getConfig().set("Items." + item.toString() + ".usage-sold." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				for (Map.Entry<String, Long> entry : item.getBuyerTimeMap().entrySet()) {
					items.getConfig().set("Items." + item.toString() + ".usage-purchase." + entry.getKey() + ".time", entry.getValue());
				}
				for (Map.Entry<String, Long> entry : item.getSellerTimeMap().entrySet()) {
					items.getConfig().set("Items." + item.toString() + ".usage-sold." + entry.getKey() + ".time", entry.getValue());
				}

				for (Map.Entry<Long, Long> entry : item.getBuyerAmountMap().entrySet()) {
					items.getConfig().set("Items." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Items." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.date", entry.getKey());
				}
				for (Map.Entry<Long, Long> entry : item.getSellerAmountMap().entrySet()) {
					items.getConfig().set("Items." + item.toString() + ".usage-sold." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Items." + item.toString() + ".usage-sold." + entry.getKey() + ".history.date", entry.getKey());
				}
				items.getConfig().set("Items." + item.toString() + ".multiplier", item.getMultiplier());
				items.saveConfig();
			}
			if (item instanceof SpecialItem) {
				SpecialItem i = (SpecialItem) item;
				items.getConfig().set("Special." + item.toString() + ".owner", i.getOwner().toString());
				items.getConfig().set("Special." + item.toString() + ".amount", i.getAmount());
				if (i.getBuyerMap().isEmpty()) {
					items.getConfig().set("Special." + item.toString() + ".usage-purchase", null);
					items.getConfig().createSection("Special." + item.toString() + ".usage-purchase");
				} else {
					for (Map.Entry<String, Long> entry : item.getBuyerMap().entrySet()) {
						items.getConfig().set("Special." + item.toString() + ".usage-purchase." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				if (item.getSellerMap().isEmpty()) {
					items.getConfig().set("Special." + item.toString() + ".usage-sold", null);
					items.getConfig().createSection("Special." + item.toString() + ".usage-sold");
				} else {
					for (Map.Entry<String, Long> entry : item.getSellerMap().entrySet()) {
						items.getConfig().set("Special." + item.toString() + ".usage-sold." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				for (Map.Entry<String, Long> entry : item.getBuyerTimeMap().entrySet()) {
					items.getConfig().set("Special." + item.toString() + ".usage-purchase." + entry.getKey() + ".time", entry.getValue());
				}
				for (Map.Entry<String, Long> entry : item.getSellerTimeMap().entrySet()) {
					items.getConfig().set("Special." + item.toString() + ".usage-sold." + entry.getKey() + ".time", entry.getValue());
				}

				for (Map.Entry<Long, Long> entry : item.getBuyerAmountMap().entrySet()) {
					items.getConfig().set("Special." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Special." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.date", entry.getKey());
				}
				for (Map.Entry<Long, Long> entry : item.getSellerAmountMap().entrySet()) {
					items.getConfig().set("Special." + item.toString() + ".usage-sold." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Special." + item.toString() + ".usage-sold." + entry.getKey() + ".history.date", entry.getKey());
				}
				items.getConfig().set("Special." + item.toString() + ".price", item.getBasePrice());
				items.getConfig().set("Special." + item.toString() + ".floor", item.getFloor());
				items.getConfig().set("Special." + item.toString() + ".ceiling", item.getCeiling());
				items.getConfig().set("Special." + item.toString() + ".multiplier", item.getMultiplier());
				items.saveConfig();
			}
			if (item instanceof MarketItem) {
				MarketItem i = (MarketItem) item;
				items.getConfig().set("Market." + item.toString() + ".owner", i.getOwner().toString());
				items.getConfig().set("Market." + item.toString() + ".amount", i.getAmount());
				if (i.getBuyerMap().isEmpty()) {
					items.getConfig().set("Market." + item.toString() + ".usage-purchase", null);
					items.getConfig().createSection("Market." + item.toString() + ".usage-purchase");
				} else {
					for (Map.Entry<String, Long> entry : item.getBuyerMap().entrySet()) {
						items.getConfig().set("Market." + item.toString() + ".usage-purchase." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				if (item.getSellerMap().isEmpty()) {
					items.getConfig().set("Market." + item.toString() + ".usage-sold", null);
					items.getConfig().createSection("Market." + item.toString() + ".usage-sold");
				} else {
					for (Map.Entry<String, Long> entry : item.getSellerMap().entrySet()) {
						items.getConfig().set("Market." + item.toString() + ".usage-sold." + entry.getKey() + ".amount", entry.getValue());
					}
				}
				for (Map.Entry<String, Long> entry : item.getBuyerTimeMap().entrySet()) {
					items.getConfig().set("Market." + item.toString() + ".usage-purchase." + entry.getKey() + ".time", entry.getValue());
				}
				for (Map.Entry<String, Long> entry : item.getSellerTimeMap().entrySet()) {
					items.getConfig().set("Market." + item.toString() + ".usage-sold." + entry.getKey() + ".time", entry.getValue());
				}

				for (Map.Entry<Long, Long> entry : item.getBuyerAmountMap().entrySet()) {
					items.getConfig().set("Market." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Market." + item.toString() + ".usage-purchase." + entry.getKey() + ".history.date", entry.getKey());
				}
				for (Map.Entry<Long, Long> entry : item.getSellerAmountMap().entrySet()) {
					items.getConfig().set("Market." + item.toString() + ".usage-sold." + entry.getKey() + ".history.amount", entry.getValue());
					items.getConfig().set("Market." + item.toString() + ".usage-sold." + entry.getKey() + ".history.date", entry.getKey());
				}
				items.getConfig().set("Market." + item.toString() + ".price", item.getBasePrice());
				items.getConfig().set("Market." + item.toString() + ".floor", item.getFloor());
				items.getConfig().set("Market." + item.toString() + ".ceiling", item.getCeiling());
				items.getConfig().set("Market." + item.toString() + ".multiplier", item.getMultiplier());
				items.saveConfig();
			}
		}
		for (BankAccount account : getManager().ACCOUNTS) {
			manager.getConfig().set("accounts." + account.getId().toString() + ".owner", account.getOwner().toString());
			if (account.getJointOwner() != null) {
				manager.getConfig().set("accounts." + account.getId().toString() + ".joint", account.getJointOwner().toString());
			}
			manager.getConfig().set("accounts." + account.getId().toString() + ".members", account.getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
			manager.saveConfig();
		}
		for (Shop atm : getManager().SHOPS) {
			atm.save();
		}
	}

	@Override
	public void onEnable() {
		this.manager = new RetroManager();
		FileManager manager = FileType.ACCOUNT.get();

		Bukkit.getPluginManager().registerEvents(this, this);

		if (!manager.exists()) {
			try {
				manager.create();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getManager().loadShop();

		if (!manager.getConfig().isConfigurationSection("accounts")) {
			manager.getConfig().createSection("accounts");
			manager.getConfig().createSection("wallets");
			manager.saveConfig();
		}
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (!manager.getConfig().isConfigurationSection("wallets." + p.getUniqueId().toString())) {
				for (World w : Bukkit.getWorlds()) {
					manager.getConfig().set("wallets." + p.getUniqueId().toString() + ".balance." + w.getName(), getManager().getMain().getConfig().getDouble("Options.wallets.starting-balance"));
				}
				manager.saveConfig();
			}
			PersistentContainer container = Labyrinth.getContainer(new NamespacedKey(this, "Shops"));
			Shop shop = container.get(Shop.class, p.getUniqueId().toString());
			if (shop != null) {
				getManager().SHOPS.add(shop);
			}
		}

		getManager().loadCurrencies();

		for (String id : manager.getConfig().getConfigurationSection("accounts").getKeys(false)) {
			this.manager.ACCOUNTS.add(new BankAccount(UUID.fromString(manager.getConfig().getString("accounts." + id + ".owner")), manager.getConfig().isString("accounts." + id + ".joint") ? UUID.fromString(manager.getConfig().getString("accounts." + id + ".joint")) : null, HUID.fromString(id), manager.getConfig().getStringList("accounts." + id + ".members")));
		}
		for (String id : manager.getConfig().getConfigurationSection("wallets").getKeys(false)) {
			OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(id));
			this.manager.WALLETS.add(new WalletAccount(owner.getUniqueId(), HUID.randomID()));
		}

		Schedule.sync(() -> {
			if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
				VaultEconomy.register();
			}
		}).wait(1);

		Schedule.sync(() -> {
			if (Bukkit.getPluginManager().isPluginEnabled("Enterprise")) {
				EnterpriseEconomy.register();
			}
		}).wait(1);

		getServer().getPluginManager().registerEvents(Shop.CONTROLLER, this);
		getServer().getPluginManager().registerEvents(ItemDemand.CONTROLLER, this);

		registerCommands();

		getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler(ignoreCancelled = true)
			public void onJoin(PlayerJoinEvent e) {
				Player p = e.getPlayer();
				if (!p.hasPlayedBefore()) {
					if (!manager.getConfig().isConfigurationSection("wallets." + p.getUniqueId().toString())) {
						for (World w : Bukkit.getWorlds()) {
							manager.getConfig().set("wallets." + p.getUniqueId().toString() + ".balance." + w.getName(), getManager().getMain().getConfig().getDouble("Options.wallets.starting-balance"));
						}
						manager.saveConfig();
						manager.reload();
						getManager().WALLETS.add(new WalletAccount(p.getUniqueId(), HUID.randomID()));
					}
				}
			}

			@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
			public void onMobDeath(EntityDeathEvent e) {
				Random rand = new Random();
				Currency cu = getManager().CURRENCIES.get(rand.nextInt(getManager().CURRENCIES.size()));
				if (cu.getType() == CurrencyType.ALT)
					return;

				if (FileType.MISC.get("Config").getConfig().getBoolean("Options.mob-reward.enabled")) {
					List<String> mobtypes = new ArrayList<>(FileType.MISC.get("Config").getConfig().getStringList("Options.mob-reward.ignored-mobs"));
					List<String> monstertypes = new ArrayList<>(FileType.MISC.get("Config").getConfig().getStringList("Options.mob-reward.ignored-monsters"));
					final int i = Integer.parseInt(String.valueOf(Math.round(1.2)));
					if (e.getEntity() instanceof Monster && e.getEntity().getKiller() != null) {
						Player p = e.getEntity().getKiller();
						Random r = new Random();

						int ran = r.nextInt(4) + 1;
						int nug = i * ran;
						if (!monstertypes.contains(e.getEntityType().name())) {
							for (int j = 0; j < nug; j++) {
								p.getWorld().dropItem(e.getEntity().getLocation(), cu.toItem());
							}
						}
					}

					if (e.getEntity() instanceof Mob && e.getEntity().getKiller() != null) {
						Player p = e.getEntity().getKiller();
						Random r = new Random();

						int ran = r.nextInt(4) + 1;
						int nug = i * ran;

						if (!mobtypes.contains(e.getEntityType().name())) {
							for (int j = 0; j < nug; j++) {
								p.getWorld().dropItem(e.getEntity().getLocation(), cu.toItem());
							}
						}
					}

					if (e.getEntity() instanceof Flying && e.getEntity().getKiller() != null) {
						Player p = e.getEntity().getKiller();
						Random r = new Random();

						int ran = r.nextInt(4) + 1;
						int nug = i * ran;
						if (!monstertypes.contains(e.getEntityType().name())) {
							for (int j = 0; j < nug; j++) {
								p.getWorld().dropItem(e.getEntity().getLocation(), cu.toItem());
							}
						}
					}
				}
			}


		}, this);
	}

	@Override
	public RetroManager getManager() {
		return this.manager;
	}

	@Override
	public int currencyTotal(Player p, Currency c) {
		if (c == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = p.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(c.toItem()))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	@Override
	public int itemStackTotal(Player p, ItemStack item) {
		if (item == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = p.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(item))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	@Override
	public PlayerTransactionResult currencyRemoval(Player p, Currency c, int amount) {
		if (currencyTotal(p, c) < amount) {
			Message.form(p).send(FormattedMessage.convert(ConfiguredMessage.getMessage("invalid-amount-item")).from(c.toItem().getItemMeta().getDisplayName()));
			return PlayerTransactionResult.FAILED;
		}
		int size = 36;
		for (int slot = 0; slot < size; slot++) {
			ItemStack is = p.getInventory().getItem(slot);
			if (is == null) continue;
			if (is.isSimilar(c.toItem())) {
				int newAmount = is.getAmount() - amount;
				if (newAmount > 0) {
					is.setAmount(newAmount);
					break;
				} else {
					p.getInventory().clear(slot);
					amount = -newAmount;
					if (amount == 0) break;
				}
			}
		}
		return PlayerTransactionResult.SUCCESS;
	}

	@Override
	public PlayerTransactionResult itemRemoval(Player p, ItemStack item, int amount) {
		if (itemStackTotal(p, item) < amount) {
			Message.form(p).setPrefix(getManager().getMain().getConfig().getString("Options.prefix")).send("&cYou don't have enough " + item.getType().name().toLowerCase());
			return PlayerTransactionResult.FAILED;
		}
		int size = 36;
		for (int slot = 0; slot < size; slot++) {
			ItemStack is = p.getInventory().getItem(slot);
			if (is == null) continue;
			if (is.isSimilar(item)) {
				int newAmount = is.getAmount() - amount;
				if (newAmount > 0) {
					is.setAmount(newAmount);
					break;
				} else {
					p.getInventory().clear(slot);
					amount = -newAmount;
					if (amount == 0) break;
				}
			}
		}
		return PlayerTransactionResult.SUCCESS;
	}

	@Override
	public FileList getFiles() {
		return FileList.search(this);
	}

	private void registerCommands() {
		new ShopCommand(RetroCommand.SHOP);
		new BankCommand(RetroCommand.BANK);
		new BuyCommand(RetroCommand.BUY);
		new SellCommand(RetroCommand.SELL);
		new TopCommand(RetroCommand.TOP);
		new BalanceCommand(RetroCommand.BALANCE);
		new DefaultCommand(RetroCommand.RETRO);
		new PayCommand(RetroCommand.PAY);
		new WithdrawCommand(RetroCommand.WITHDRAW);
		new DepositCommand(RetroCommand.DEPOSIT);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		FileManager manager = FileType.ACCOUNT.get();
		Player p = e.getPlayer();
		if (!manager.getConfig().isConfigurationSection("wallets." + p.getUniqueId().toString())) {
			for (World w : Bukkit.getWorlds()) {
				manager.getConfig().set("wallets." + p.getUniqueId().toString() + ".balance." + w.getName(), getManager().getMain().getConfig().getDouble("Options.wallets.starting-balance"));
			}
			manager.saveConfig();
		}
	}

	public enum PlayerTransactionResult {
		SUCCESS(true), FAILED(false);

		private final boolean transactionSuccess;

		PlayerTransactionResult(boolean b) {
			transactionSuccess = b;
		}

		public boolean isTransactionSuccess() {
			return transactionSuccess;
		}
	}

	public enum TransactionResult {
		SUCCESS(true), FAILED(false);

		private final boolean transactionSuccess;

		TransactionResult(boolean b) {
			transactionSuccess = b;
		}

		public boolean success() {
			return transactionSuccess;
		}
	}

}

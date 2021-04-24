package com.github.sanctum.retro;

import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.api.RetroAPI;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.construct.core.RetroWallet;
import com.github.sanctum.retro.construct.internal.BalanceCommand;
import com.github.sanctum.retro.construct.internal.BankCommand;
import com.github.sanctum.retro.construct.internal.BuyCommand;
import com.github.sanctum.retro.construct.internal.DefaultCommand;
import com.github.sanctum.retro.construct.internal.DepositCommand;
import com.github.sanctum.retro.construct.internal.PayCommand;
import com.github.sanctum.retro.construct.internal.RetroCommand;
import com.github.sanctum.retro.construct.internal.SellCommand;
import com.github.sanctum.retro.construct.internal.WithdrawCommand;
import com.github.sanctum.retro.construct.item.Currency;
import com.github.sanctum.retro.construct.item.ItemDemand;
import com.github.sanctum.retro.enterprise.EnterpriseEconomy;
import com.github.sanctum.retro.util.ConfiguredMessage;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.PlaceHolder;
import com.github.sanctum.retro.vault.VaultEconomy;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RetroConomy extends JavaPlugin implements RetroAPI {

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
		for (ItemDemand item : getManager().getShop().sort()) {
			for (Map.Entry<String, Long> entry : item.getBuyerMap().entrySet()) {
				items.getConfig().set("Items." + item.getItem().getType().name() + ".usage-purchase." + entry.getKey() + ".amount", entry.getValue());
			}
			for (Map.Entry<String, Long> entry : item.getSellerMap().entrySet()) {
				items.getConfig().set("Items." + item.getItem().getType().name() + ".usage-sold." + entry.getKey() + ".amount", entry.getValue());
			}
			for (Map.Entry<String, Long> entry : item.getBuyerTimeMap().entrySet()) {
				items.getConfig().set("Items." + item.getItem().getType().name() + ".usage-purchase." + entry.getKey() + ".time", entry.getValue());
			}
			for (Map.Entry<String, Long> entry : item.getSellerTimeMap().entrySet()) {
				items.getConfig().set("Items." + item.getItem().getType().name() + ".usage-sold." + entry.getKey() + ".time", entry.getValue());
			}
			items.saveConfig();
		}
		for (RetroAccount account : getManager().ACCOUNTS) {
			manager.getConfig().set("accounts." + account.getId().toString() + ".owner", account.getOwner().toString());
			if (account.getJointOwner() != null) {
				manager.getConfig().set("accounts." + account.getId().toString() + ".joint", account.getJointOwner().toString());
			}
			manager.getConfig().set("accounts." + account.getId().toString() + ".members", account.getMembers());
			manager.saveConfig();
		}
	}

	@Override
	public void onEnable() {
		this.manager = new RetroManager();
		FileManager manager = FileType.ACCOUNT.get();

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
		}

		getManager().loadCurrencies();

		for (String id : manager.getConfig().getConfigurationSection("accounts").getKeys(false)) {
			this.manager.ACCOUNTS.add(new RetroAccount(UUID.fromString(manager.getConfig().getString("accounts." + id + ".owner")), manager.getConfig().getString("accounts." + id + ".joint") != null ? UUID.fromString(manager.getConfig().getString("accounts." + id + ".joint")) : null, HUID.fromString(id), manager.getConfig().getStringList("accounts." + id + ".members")));
		}
		for (String id : manager.getConfig().getConfigurationSection("wallets").getKeys(false)) {
			OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(id));
			this.manager.WALLETS.add(new RetroWallet(owner.getUniqueId(), HUID.randomID()));
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

		registerCommands();

	}

	@Override
	public RetroManager getManager() {
		return this.manager;
	}

	@Override
	public int getTotalAmount(Player p, Currency c) {
		if (c == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = p.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(c.getItem()))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	public int getTotalAmount(Player p, ItemStack item) {
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
		if (getTotalAmount(p, c) < amount) {
			Message.form(p).send(PlaceHolder.convert(ConfiguredMessage.getMessage("invalid-amount-item")).from(c.getItem().getItemMeta().getDisplayName()));
			return PlayerTransactionResult.FAILED;
		}
		int size = 36;
		for (int slot = 0; slot < size; slot++) {
			ItemStack is = p.getInventory().getItem(slot);
			if (is == null) continue;
			if (is.isSimilar(c.getItem())) {
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
		if (getTotalAmount(p, item) < amount) {
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
		new BankCommand(RetroCommand.BANK);
		new BuyCommand(RetroCommand.BUY);
		new SellCommand(RetroCommand.SELL);
		new BalanceCommand(RetroCommand.BALANCE);
		new DefaultCommand(RetroCommand.RETRO);
		new PayCommand(RetroCommand.PAY);
		new WithdrawCommand(RetroCommand.WITHDRAW);
		new DepositCommand(RetroCommand.DEPOSIT);
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

}

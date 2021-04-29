/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.construct.core.ATM;
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.Currency;
import com.github.sanctum.retro.construct.core.ItemDemand;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.util.ATMList;
import com.github.sanctum.retro.util.AcceptableCurrencies;
import com.github.sanctum.retro.util.AccountList;
import com.github.sanctum.retro.util.CurrencyType;
import com.github.sanctum.retro.util.FileType;
import com.github.sanctum.retro.util.Marketplace;
import com.github.sanctum.retro.util.WalletList;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RetroManager {

	public final LinkedList<BankAccount> ACCOUNTS = new LinkedList<>();

	public final LinkedList<WalletAccount> WALLETS = new LinkedList<>();

	public final LinkedList<Currency> CURRENCIES = new LinkedList<>();

	public final LinkedList<ATM> ATMS = new LinkedList<>();

	public final LinkedList<ItemDemand> SHOP = new LinkedList<>();

	public @NotNull FileManager getMain() {

		FileManager config = FileType.MISC.get("Config");
		if (!config.exists()) {
			InputStream is = JavaPlugin.getProvidingPlugin(getClass()).getResource("Config.yml");
			FileManager.copy(is, config.getFile());
			config.reload();
		}
		return config;
	}

	public String getMajorSingular() {
		return getMain().getConfig().getString("Currency.major.singular");
	}

	public String getMajorPlural() {
		return getMain().getConfig().getString("Currency.major.plural");
	}

	public String getMinorSingular() {
		return getMain().getConfig().getString("Currency.minor.singular");
	}

	public String getMinorPlural() {
		return getMain().getConfig().getString("Currency.minor.plural");
	}

	public Locale getLocale() {
		Locale loc = Locale.US;
		switch (getMain().getConfig().getString("Options.format")) {
			case "fr":
				loc = Locale.FRANCE;
				break;
			case "jp":
				loc = Locale.JAPAN;
				break;
			case "it":
				loc = Locale.ITALY;
				break;
			case "de":
				loc = Locale.GERMANY;
				break;
			case "nl":
				loc = new Locale("nl", "NL");
				break;
		}
		return loc;
	}

	public String format(double amount) {
		return NumberFormat.getNumberInstance(getLocale()).format(amount);
	}

	public String format(BigDecimal amount) {
		return NumberFormat.getNumberInstance(getLocale()).format(amount.doubleValue());
	}

	public String format(BigDecimal amount, Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(amount.doubleValue());
	}

	public void deleteAccount(BankAccount account) {
		ACCOUNTS.remove(account);
		if (!account.remove().success()) {
			Bukkit.getLogger().warning("{RetroConomy} - Something went wrong while attempting to delete account " + account.getId().toString());
		}
	}

	public void loadAccount(BankAccount account) {
		ACCOUNTS.add(account);
	}

	public void loadShop() {
		SHOP.clear();
		FileManager items = FileType.MISC.get("Items");
		Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
		if (!items.exists()) {
			InputStream is = plugin.getResource("Items.yml");
			FileManager.copy(is, items.getFile());
		}
		items.reload();

		for (String item : items.getConfig().getConfigurationSection("Items").getKeys(false)) {
			Material mat = Items.getMaterial(item);
			if (mat != null) {
				Map<String, Long> buyMap = new HashMap<>();
				Map<String, Long> sellMap = new HashMap<>();
				Map<String, Long> buyMapDate = new HashMap<>();
				Map<String, Long> sellMapDate = new HashMap<>();
				for (String user : items.getConfig().getConfigurationSection("Items." + item + ".usage-purchase").getKeys(false)) {
					buyMap.put(user, items.getConfig().getLong("Items." + item + ".usage-purchase" + "." + user + ".amount"));
				}
				for (String user : items.getConfig().getConfigurationSection("Items." + item + ".usage-sold").getKeys(false)) {
					sellMap.put(user, items.getConfig().getLong("Items." + item + ".usage-sold" + "." + user + ".amount"));
				}
				for (String user : items.getConfig().getConfigurationSection("Items." + item + ".usage-purchase").getKeys(false)) {
					buyMapDate.put(user, items.getConfig().getLong("Items." + item + ".usage-purchase" + "." + user + ".date"));
				}
				for (String user : items.getConfig().getConfigurationSection("Items." + item + ".usage-sold").getKeys(false)) {
					sellMapDate.put(user, items.getConfig().getLong("Items." + item + ".usage-sold" + "." + user + ".date"));
				}
				new SystemItem(new ItemStack(mat), items.getConfig().getDouble("Items." + item + ".price"), items.getConfig().getDouble("Items." + item + ".multiplier"), items.getConfig().getDouble("Items." + item + ".ceiling"), items.getConfig().getDouble("Items." + item + ".floor"), buyMap, sellMap, buyMapDate, sellMapDate);
			} else {
				plugin.getLogger().severe("- An invalid item description was found within the items configuration, section '" + item + "'");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
	}

	public void loadCurrencies() {
		CURRENCIES.clear();
		FileManager main = getMain();
		Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
		for (String item : main.getConfig().getConfigurationSection("Currency.items.dollar").getKeys(false)) {
			if (Items.getMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the dollar item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.getMaterial(item));

				if (main.getConfig().getBoolean("Currency.custom")) {

					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getConfig().getString("Currency.major.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.DOLLAR, main.getConfig().getDouble("Currency.items.dollar." + item));
				CURRENCIES.add(c);
			}
		}
		for (String item : main.getConfig().getConfigurationSection("Currency.items.change").getKeys(false)) {
			if (Items.getMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the change item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.getMaterial(item));
				if (main.getConfig().getBoolean("Currency.custom")) {
					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getConfig().getString("Currency.minor.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.CHANGE, main.getConfig().getDouble("Currency.items.change." + item));
				CURRENCIES.add(c);
			}
		}
		for (String item : main.getConfig().getConfigurationSection("Currency.items.alt").getKeys(false)) {
			if (Items.getMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the alt item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.getMaterial(item));

				if (main.getConfig().getBoolean("Currency.custom")) {
					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getConfig().getString("Currency.major.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.ALT, main.getConfig().getDouble("Currency.items.alt." + item));
				CURRENCIES.add(c);
			}
		}
	}

	public UniformedComponents<WalletAccount> getWallets() {
		return new WalletList();
	}

	public UniformedComponents<Currency> getAcceptableCurrencies() {
		return new AcceptableCurrencies();
	}

	public UniformedComponents<BankAccount> getAccounts() {
		return new AccountList();
	}

	public UniformedComponents<ItemDemand> getMarket() {
		return new Marketplace();
	}

	public UniformedComponents<ATM> getATMs() {
		return new ATMList();
	}

	public String[] getCurrencyNames() {
		List<String> list = new ArrayList<>();

		list.add(getMajorSingular());
		list.add(getMinorSingular());

		return list.toArray(new String[0]);
	}

	public Optional<ItemDemand> getDemand(ItemStack item) {
		return getMarket().filter(i -> i.getItem().isSimilar(item)).findFirst();
	}

	public Optional<ItemDemand> getDemand(Material mat) {
		return getMarket().filter(i -> i.getItem().getType() == mat).findFirst();
	}

	public Optional<BankAccount> getAccount(HUID accountId) {
		return getAccounts().filter(a -> a.getId().equals(accountId)).findFirst();
	}

	public List<BankAccount> getAccounts(String name) {
		return getAccounts().filter(a -> {
			if (Bukkit.getOfflinePlayer(a.getOwner()).getName().equals(name)) {
				return true;
			} else
			if (a.getJointOwner() != null && Bukkit.getOfflinePlayer(a.getJointOwner()).getName().equals(name)) {
				return true;
			} else
				return a.getMembers().contains(Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equals(name)).map(OfflinePlayer::getUniqueId).findFirst().get());
		}).collect(Collectors.toList());
	}

	public List<BankAccount> getAccounts(UUID id) {
		return getAccounts().filter(a -> {
			if (Bukkit.getOfflinePlayer(a.getOwner()).getUniqueId().equals(id)) {
				return true;
			} else
			if (a.getJointOwner() != null && Bukkit.getOfflinePlayer(a.getJointOwner()).getUniqueId().equals(id)) {
				return true;
			} else
				return a.getMembers().contains(id);
		}).collect(Collectors.toList());
	}

	public Optional<BankAccount> getAccount(String name) {
		return getAccounts(name).stream().filter(a -> a.isPrimary(Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName().equals(name)).map(OfflinePlayer::getUniqueId).findFirst().get())).findFirst();
	}

	public Optional<BankAccount> getAccount(UUID id) {
		return getAccounts(id).stream().filter(a -> a.isPrimary(id)).findFirst();
	}

	public Optional<BankAccount> getAccount(OfflinePlayer player) {
		return getAccounts(player.getUniqueId()).stream().filter(a -> a.isPrimary(player.getUniqueId())).findFirst();
	}

	public Optional<WalletAccount> getWallet(String name) {
		return getWallets().filter(a -> a.getOwner().getName().equals(name)).findFirst();
	}

	public Optional<WalletAccount> getWallet(UUID id) {
		return getWallets().filter(a -> a.getOwner().getUniqueId().equals(id)).findFirst();
	}

	public Optional<WalletAccount> getWallet(OfflinePlayer player) {
		return getWallets().filter(a -> a.getOwner().getUniqueId().equals(player.getUniqueId())).findFirst();
	}


}

/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro;

import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HFEncoded;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.MathUtils;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.construct.core.BankAccount;
import com.github.sanctum.retro.construct.core.Currency;
import com.github.sanctum.retro.construct.core.MarketItem;
import com.github.sanctum.retro.construct.core.Shop;
import com.github.sanctum.retro.construct.core.SpecialItem;
import com.github.sanctum.retro.construct.core.SystemItem;
import com.github.sanctum.retro.construct.core.WalletAccount;
import com.github.sanctum.retro.util.CurrencyType;
import com.github.sanctum.retro.util.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

	protected final List<BankAccount> ACCOUNTS = Collections.synchronizedList(new LinkedList<>());

	protected final List<WalletAccount> WALLETS = Collections.synchronizedList(new LinkedList<>());

	protected final List<Currency> CURRENCIES = Collections.synchronizedList(new LinkedList<>());

	protected final List<Shop> SHOPS = Collections.synchronizedList(new LinkedList<>());

	protected final List<ItemDemand> SHOP = Collections.synchronizedList(new LinkedList<>());

	public @NotNull FileManager getMain() {

		FileManager config = FileReader.MISC.get("Config");
		if (!config.getRoot().exists()) {
			FileList.copy(Objects.requireNonNull(JavaPlugin.getProvidingPlugin(getClass()).getResource("Config.yml")), config.getRoot().getParent());
			config.getRoot().reload();
		}
		return config;
	}

	public String getMajorSingular() {
		return getMain().getRoot().getString("Currency.major.singular");
	}

	public String getMajorPlural() {
		return getMain().getRoot().getString("Currency.major.plural");
	}

	public String getMinorSingular() {
		return getMain().getRoot().getString("Currency.minor.singular");
	}

	public String getMinorPlural() {
		return getMain().getRoot().getString("Currency.minor.plural");
	}

	public Locale getLocale() {
		Locale loc = Locale.US;

		switch (getMain().getRoot().getString("Options.format")) {
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
		return MathUtils.use(amount).formatCurrency(getLocale());
	}

	public String format(BigDecimal amount) {
		return MathUtils.use(amount).formatCurrency(getLocale());
	}

	public String format(BigDecimal amount, Locale locale) {
		return MathUtils.use(amount).formatCurrency(locale);
	}

	public void deleteAccount(BankAccount account) {
		ACCOUNTS.remove(account);
		if (!account.remove().success()) {
			Bukkit.getLogger().warning("{RetroConomy} - Something went wrong while attempting to delete account " + account.getId().toString());
		}
	}

	public void deleteItem(ItemDemand demand) {
		FileManager items = FileReader.MISC.get("Items");
		if (demand instanceof SystemItem) {
			if (demand instanceof SpecialItem) {
				items.getRoot().set("Special." + demand, null);
			} else {
				items.getRoot().set("Items." + demand, null);
			}

		}

		if (demand instanceof MarketItem) {
			items.getRoot().set("Market." + demand, null);
		}

		SHOP.remove(demand);

	}

	public void loadAccount(BankAccount account) {
		ACCOUNTS.add(account);
	}

	public void loadShop() {
		SHOP.clear();
		FileManager items = FileReader.MISC.get("Items");
		Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
		if (!items.getRoot().exists()) {
			FileList.copy(plugin.getResource("Items.yml"), items.getRoot().getParent());
		}
		items.getRoot().reload();

		for (String item : items.getRoot().getNode("Items").getKeys(false)) {
			Material mat = Items.findMaterial(item);
			if (mat != null) {
				Map<String, Long> buyMap = new HashMap<>();
				Map<String, Long> sellMap = new HashMap<>();
				Map<String, Long> buyMapDate = new HashMap<>();
				Map<String, Long> sellMapDate = new HashMap<>();
				Map<Long, Long> sellAmountMap = new HashMap<>();
				Map<Long, Long> buyAmountMap = new HashMap<>();
				for (String user : items.getRoot().getNode("Items." + item + ".usage-purchase").getKeys(false)) {
					buyMap.put(user, items.getRoot().getLong("Items." + item + ".usage-purchase" + "." + user + ".amount"));
					buyMapDate.put(user, items.getRoot().getLong("Items." + item + ".usage-purchase" + "." + user + ".date"));
					if (items.getRoot().isNode("Items." + item + ".usage-purchase" + "." + user + ".history")) {
						buyAmountMap.put(items.getRoot().getLong("Items." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Items." + item + ".usage-purchase" + "." + user + ".history.amount"));
					}
				}
				for (String user : items.getRoot().getNode("Items." + item + ".usage-sold").getKeys(false)) {
					sellMap.put(user, items.getRoot().getLong("Items." + item + ".usage-sold" + "." + user + ".amount"));
					sellMapDate.put(user, items.getRoot().getLong("Items." + item + ".usage-sold" + "." + user + ".date"));
					if (items.getRoot().isNode("Items." + item + ".usage-purchase" + "." + user + ".history")) {
						sellAmountMap.put(items.getRoot().getLong("Items." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Items." + item + ".usage-sold" + "." + user + ".history.amount"));
					}
				}

				new SystemItem(item, new ItemStack(mat), items.getRoot().getDouble("Items." + item + ".price"), items.getRoot().getDouble("Items." + item + ".multiplier"), items.getRoot().getDouble("Items." + item + ".ceiling"), items.getRoot().getDouble("Items." + item + ".floor"), buyMap, sellMap, buyMapDate, sellMapDate, buyAmountMap, sellAmountMap);
			} else {
				plugin.getLogger().severe("- An invalid item description was found within the items configuration, section '" + item + "'");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
		if (items.getRoot().isNode("Special")) {
			for (String item : items.getRoot().getNode("Special").getKeys(false)) {
				ItemStack mat = items.getRoot().getItemStack(item);
				String id = items.getRoot().getString("Special." + item + ".owner");
				int amount = items.getRoot().getInt("Special." + item + ".amount");
				if (mat != null) {
					Map<String, Long> buyMap = new HashMap<>();
					Map<String, Long> sellMap = new HashMap<>();
					Map<String, Long> buyMapDate = new HashMap<>();
					Map<String, Long> sellMapDate = new HashMap<>();
					Map<Long, Long> sellAmountMap = new HashMap<>();
					Map<Long, Long> buyAmountMap = new HashMap<>();
					for (String user : items.getRoot().getNode("Special." + item + ".usage-purchase").getKeys(false)) {
						buyMap.put(user, items.getRoot().getLong("Special." + item + ".usage-purchase" + "." + user + ".amount"));
						buyMapDate.put(user, items.getRoot().getLong("Special." + item + ".usage-purchase" + "." + user + ".date"));
						if (items.getRoot().isNode("Special." + item + ".usage-purchase" + "." + user + ".history")) {
							buyAmountMap.put(items.getRoot().getLong("Special." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Special." + item + ".usage-purchase" + "." + user + ".history.amount"));
						}
					}
					for (String user : items.getRoot().getNode("Special." + item + ".usage-sold").getKeys(false)) {
						sellMap.put(user, items.getRoot().getLong("Special." + item + ".usage-sold" + "." + user + ".amount"));
						sellMapDate.put(user, items.getRoot().getLong("Special." + item + ".usage-sold" + "." + user + ".date"));
						if (items.getRoot().isNode("Special." + item + ".usage-purchase" + "." + user + ".history")) {
							sellAmountMap.put(items.getRoot().getLong("Special." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Special." + item + ".usage-sold" + "." + user + ".history.amount"));
						}
					}

					new SpecialItem(item, id, amount, mat, items.getRoot().getDouble("Special." + item + ".price"), items.getRoot().getDouble("Special." + item + ".multiplier"), items.getRoot().getDouble("Special." + item + ".ceiling"), items.getRoot().getDouble("Special." + item + ".floor"), buyMap, sellMap, buyMapDate, sellMapDate, buyAmountMap, sellAmountMap);
				} else {
					plugin.getLogger().severe("- An invalid item description was found within the items configuration, section '" + item + "'");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}
			}
		}
		if (items.getRoot().isNode("Market")) {
			for (String item : items.getRoot().getNode("Market").getKeys(false)) {
				ItemStack mat = null;
				try {
					mat = (ItemStack) new HFEncoded(item).deserialized();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				String id = items.getRoot().getString("Market." + item + ".owner");
				int amount = items.getRoot().getInt("Market." + item + ".amount");
				if (mat != null) {
					Map<String, Long> buyMap = new HashMap<>();
					Map<String, Long> sellMap = new HashMap<>();
					Map<String, Long> buyMapDate = new HashMap<>();
					Map<String, Long> sellMapDate = new HashMap<>();
					Map<Long, Long> sellAmountMap = new HashMap<>();
					Map<Long, Long> buyAmountMap = new HashMap<>();
					for (String user : items.getRoot().getNode("Market." + item + ".usage-purchase").getKeys(false)) {
						buyMap.put(user, items.getRoot().getLong("Market." + item + ".usage-purchase" + "." + user + ".amount"));
						buyMapDate.put(user, items.getRoot().getLong("Market." + item + ".usage-purchase" + "." + user + ".date"));
						if (items.getRoot().isNode("Market." + item + ".usage-purchase" + "." + user + ".history")) {
							buyAmountMap.put(items.getRoot().getLong("Market." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Market." + item + ".usage-purchase" + "." + user + ".history.amount"));
						}
					}
					for (String user : items.getRoot().getNode("Market." + item + ".usage-sold").getKeys(false)) {
						sellMap.put(user, items.getRoot().getLong("Market." + item + ".usage-sold" + "." + user + ".amount"));
						sellMapDate.put(user, items.getRoot().getLong("Market." + item + ".usage-sold" + "." + user + ".date"));
						if (items.getRoot().isNode("Market." + item + ".usage-purchase" + "." + user + ".history")) {
							sellAmountMap.put(items.getRoot().getLong("Market." + item + ".usage-purchase" + "." + user + ".history.date"), items.getRoot().getLong("Market." + item + ".usage-sold" + "." + user + ".history.amount"));
						}
					}

					new MarketItem(item, id, amount, mat, items.getRoot().getDouble("Market." + item + ".price"), items.getRoot().getDouble("Market." + item + ".multiplier"), items.getRoot().getDouble("Market." + item + ".ceiling"), items.getRoot().getDouble("Market." + item + ".floor"), buyMap, sellMap, buyMapDate, sellMapDate, buyAmountMap, sellAmountMap);
				} else {
					plugin.getLogger().severe("- An invalid item description was found within the items configuration, section '" + item + "'");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}
			}
		}
	}

	public void loadCurrencies() {
		CURRENCIES.clear();
		FileManager main = getMain();
		Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
		for (String item : main.getRoot().getNode("Currency.items.dollar").getKeys(false)) {
			if (Items.findMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the dollar item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.findMaterial(item));

				if (main.getRoot().getBoolean("Currency.custom")) {

					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getRoot().getString("Currency.major.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.DOLLAR, main.getRoot().getDouble("Currency.items.dollar." + item));
				CURRENCIES.add(c);
			}
		}
		for (String item : main.getRoot().getNode("Currency.items.change").getKeys(false)) {
			if (Items.findMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the change item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.findMaterial(item));
				if (main.getRoot().getBoolean("Currency.custom")) {
					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getRoot().getString("Currency.minor.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.CHANGE, main.getRoot().getDouble("Currency.items.change." + item));
				CURRENCIES.add(c);
			}
		}
		for (String item : main.getRoot().getNode("Currency.items.alt").getKeys(false)) {
			if (Items.findMaterial(item) == null) {
				plugin.getLogger().severe("- An invalid item description was found in the alt item config section. Re-format then restart.");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			} else {

				ItemStack i = new ItemStack(Items.findMaterial(item));

				if (main.getRoot().getBoolean("Currency.custom")) {
					ItemMeta meta = i.getItemMeta();

					meta.setDisplayName(StringUtils.use(main.getRoot().getString("Currency.major.singular")).translate());
					i.setItemMeta(meta);
				}

				Currency c = new Currency(i, CurrencyType.ALT, main.getRoot().getDouble("Currency.items.alt." + item));
				CURRENCIES.add(c);
			}
		}
	}

	public List<WalletAccount> getWallets() {
		return WALLETS;
	}

	public List<Currency> getAcceptableCurrencies() {
		return CURRENCIES;
	}

	public List<BankAccount> getAccounts() {
		return ACCOUNTS;
	}

	public List<ItemDemand> getInventory() {
		return SHOP;
	}

	public List<Shop> getShops() {
		return SHOPS;
	}

	public String[] getCurrencyNames() {
		List<String> list = new ArrayList<>();

		list.add(getMajorSingular());
		list.add(getMinorSingular());

		return list.toArray(new String[0]);
	}

	private boolean isSimilar(ItemStack s, ItemStack stack) {
		return stack.getType() == s.getType();
	}

	public Optional<ItemDemand> getDemand(ItemStack item) {
		return SHOP.stream().filter(i -> isSimilar(i.getItem(), item)).findFirst();
	}

	public Optional<MarketItem> getMarketItem(ItemStack item) {
		return SHOP.stream().filter(i -> i.getItem().isSimilar(item) && i instanceof MarketItem).map(it -> (MarketItem)it).findFirst();
	}

	public Optional<ItemDemand> getDemand(Material mat) {
		return SHOP.stream().filter(i -> i.getItem().getType() == mat).findFirst();
	}

	public Optional<BankAccount> getAccount(HUID accountId) {
		return ACCOUNTS.stream().filter(a -> a.getId().equals(accountId)).findFirst();
	}

	public List<BankAccount> getAccounts(String name) {
		return getAccounts().stream().filter(a -> {
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
		return getAccounts().stream().filter(a -> {
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
		return getWallets().stream().filter(a -> a.getOwner().getName().equals(name)).findFirst();
	}

	public Optional<WalletAccount> getWallet(UUID id) {
		return getWallets().stream().filter(a -> a.getOwner().getUniqueId().equals(id)).findFirst();
	}

	public Optional<WalletAccount> getWallet(OfflinePlayer player) {
		return getWallets().stream().filter(a -> a.getOwner().getUniqueId().equals(player.getUniqueId())).findFirst();
	}


}

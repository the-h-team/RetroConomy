/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.util.ItemModificationEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SystemItem implements ItemDemand {

	private final ItemStack item;
	private final double price;
	private final Map<String, Long> buyerMap;
	private final Map<String, Long> sellerMap;
	private final Map<String, Long> buyerTimeMap;
	private final Map<String, Long> sellerTimeMap;
	private final Map<Long, Long> buyerAmountMap;
	private final Map<Long, Long> sellerAmountMap;
	private double multiplier;
	private final double ceiling;
	private final double floor;
	private int bought;
	private int sold;
	private final String path;
	private long lastModified;
	private String lastBuyer = "No-one";
	private String lastSeller = "No-one";

	public SystemItem(String path, ItemStack item, double price, double multiplier, double ceiling, double floor, Map<String, Long> buyerMap, Map<String, Long> sellerMap, Map<String, Long> buyerTimeMap, Map<String, Long> sellerTimeMap, Map<Long, Long> sellerAmountMap, Map<Long, Long> buyerAmountMap) {
		this.item = item;
		this.path = path;
		this.price = price;
		this.ceiling = ceiling;
		this.floor = floor;
		this.multiplier = multiplier;
		this.buyerMap = new HashMap<>(buyerMap);
		this.sellerMap = new HashMap<>(sellerMap);
		this.buyerTimeMap = new HashMap<>(buyerTimeMap);
		this.sellerTimeMap = new HashMap<>(sellerTimeMap);
		this.buyerAmountMap = new HashMap<>(buyerAmountMap);
		this.sellerAmountMap = new HashMap<>(sellerAmountMap);
		this.bought = 0;
		this.sold = 0;
		RetroConomy.getInstance().getManager().getInventory().add(this);
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public double getBasePrice() {
		return price;
	}

	@Override
	public double getBuyPrice(int amount) {
		double base = getBasePrice() * getMultiplier() * amount;
		double floor = getFloor() * getMultiplier() * amount;
		double ceiling = getCeiling() * getMultiplier() * amount;
		if (base < 0) {
			return floor;
		} else {
			return Math.min(base, ceiling);
		}
	}

	@Override
	public double getSellPrice(int amount) {
		double base = (getBasePrice() * getMultiplier()) * amount / 2;
		double floor = (getFloor() * getMultiplier()) * amount / 2;
		double ceiling = (getCeiling() * getMultiplier()) * amount / 2;
		if (base < 0) {
			return floor;
		} else {
			return Math.min(base, ceiling);
		}
	}

	@Override
	public double getCeiling() {
		return ceiling;
	}

	@Override
	public double getFloor() {
		return floor;
	}

	@Override
	public double getMultiplier() {
		return Math.max(multiplier, 0.1);
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public boolean isBlacklisted() {
		return RetroConomy.getInstance().getManager().getMain().getRoot().getStringList("Options.item-blacklist").contains(getItem().getType().name());
	}

	@Override
	public long getRecentBought() {
		return bought;
	}

	@Override
	public long getRecentSold() {
		return sold;
	}

	@Override
	public long getSold(UUID user) {
		return sellerMap.getOrDefault(user.toString(), 0L);
	}

	@Override
	public long getSoldLast(UUID user) {
		return sellerTimeMap.getOrDefault(user.toString(), 0L);
	}

	@Override
	public long getSold(TimeUnit unit, int time) {
		long total = 0L;
		for (Map.Entry<Long, Long> entry : sellerAmountMap.entrySet()) {
			TimeWatch watch = TimeWatch.start(entry.getKey());
			switch (unit) {
				case DAYS:
					if (TimeUnit.SECONDS.toDays(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case HOURS:
					if (TimeUnit.SECONDS.toHours(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case MINUTES:
					if (TimeUnit.SECONDS.toMinutes(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case SECONDS:
					if (watch.interval(Instant.now()).getSeconds() <= time) {
						total += entry.getValue();
					}
					break;
			}
		}
		return total;
	}

	@Override
	public long getSold() {
		long sold = 0;
		for (Map.Entry<String, Long> entry : getSellerMap().entrySet()) {
			sold += entry.getValue();
		}
		return sold;
	}

	@Override
	public long getBought(UUID user) {
		return buyerMap.getOrDefault(user.toString(), 0L);
	}

	@Override
	public long getBoughtLast(UUID user) {
		return buyerTimeMap.getOrDefault(user.toString(), 0L);
	}

	@Override
	public long getBought(TimeUnit unit, int time) {
		long total = 0L;
		for (Map.Entry<Long, Long> entry : buyerAmountMap.entrySet()) {
			TimeWatch watch = TimeWatch.start(entry.getKey());
			switch (unit) {
				case DAYS:
					if (TimeUnit.SECONDS.toDays(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case HOURS:
					if (TimeUnit.SECONDS.toHours(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case MINUTES:
					if (TimeUnit.SECONDS.toMinutes(watch.interval(Instant.now()).getSeconds()) <= time) {
						total += entry.getValue();
					}
					break;
				case SECONDS:
					if (watch.interval(Instant.now()).getSeconds() <= time) {
						total += entry.getValue();
					}
					break;
			}
		}
		return total;
	}

	@Override
	public long getBought() {
		long buy = 0;
		for (Map.Entry<String, Long> entry : getBuyerMap().entrySet()) {
			buy += entry.getValue();
		}
		return buy;
	}

	@Override
	public double getPopularity() {
		return (double) getBought(TimeUnit.DAYS, 2) * getSold(TimeUnit.DAYS, 2) / 420;
	}

	@Override
	public String getLastBuyer() {
		return lastBuyer;
	}

	@Override
	public String getLastSeller() {
		return lastSeller;
	}

	@Override
	public Map<String, Long> getBuyerTimeMap() {
		return buyerTimeMap;
	}

	@Override
	public Map<String, Long> getSellerTimeMap() {
		return sellerTimeMap;
	}

	@Override
	public Map<Long, Long> getBuyerAmountMap() {
		return buyerAmountMap;
	}

	@Override
	public Map<Long, Long> getSellerAmountMap() {
		return sellerAmountMap;
	}

	@Override
	public Map<String, Long> getBuyerMap() {
		return buyerMap;
	}

	@Override
	public Map<String, Long> getSellerMap() {
		return sellerMap;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(Player who, double multiplier) {
		if (who.isOp()) {
			this.multiplier = multiplier;
			this.lastModified = System.currentTimeMillis();
			this.sellerAmountMap.clear();
			this.buyerAmountMap.clear();
			return RetroConomy.PlayerTransactionResult.SUCCESS;
		}
		return RetroConomy.PlayerTransactionResult.FAILED;
	}

	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(double multiplier) {
		this.multiplier = multiplier;
		this.lastModified = System.currentTimeMillis();
		this.sellerAmountMap.clear();
		this.buyerAmountMap.clear();
		return RetroConomy.PlayerTransactionResult.SUCCESS;
	}

	@Override
	public RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result) {
		long time = System.currentTimeMillis();
		switch (result) {
			case Buy:
				// give item if online
				double price = getBuyPrice(1);
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					ItemModificationEvent event = new ItemModificationEvent(u, this, TransactionResult.Buy);
					Bukkit.getPluginManager().callEvent(event);
					Optional<WalletAccount> wallet = RetroConomy.getInstance().getManager().getWallet(user);
					if (wallet.isPresent() && !wallet.get().has(price, u.getWorld())) {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
					u.getWorld().dropItem(u.getLocation(), getItem());
					lastBuyer = user.toString();
					bought++;
					buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 0L) + 1);
					buyerTimeMap.put(user.toString(), time);
					if (buyerAmountMap.containsKey(time)) {
						buyerAmountMap.put(time, buyerAmountMap.get(time) + 1L);
					} else {
						buyerAmountMap.put(time, 1L);
					}
					wallet.ifPresent(w -> {
						if (w.has(price, u.getWorld())) {
							w.withdraw(BigDecimal.valueOf(price), u.getWorld());
						}
					});
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:
				// take item if online
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					ItemModificationEvent event = new ItemModificationEvent(u, this, TransactionResult.Sell);
					Bukkit.getPluginManager().callEvent(event);
					if (RetroConomy.getInstance().itemRemoval(u, getItem(), 1).isTransactionSuccess()) {
						double amount = getSellPrice(1);
						lastSeller = user.toString();
						sold++;
						sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 0L) + 1);
						sellerTimeMap.put(user.toString(), time);
						if (sellerAmountMap.containsKey(time)) {
							sellerAmountMap.put(time, sellerAmountMap.get(time) + 1L);
						} else {
							sellerAmountMap.put(time, 1L);
						}
						RetroConomy.getInstance().getManager().getWallet(user).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(amount), u.getWorld()));
					} else {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			default:
				return RetroConomy.PlayerTransactionResult.FAILED;
		}
	}

	@Override
	public RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result, int count) {
		long time = System.currentTimeMillis();
		switch (result) {
			case Buy:

				// give items if online
				double price = getBuyPrice(count);
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					ItemModificationEvent event = new ItemModificationEvent(u, this, TransactionResult.Buy);
					Bukkit.getPluginManager().callEvent(event);
					Optional<WalletAccount> wallet = RetroConomy.getInstance().getManager().getWallet(user);
					if (wallet.isPresent() && !wallet.get().has(price, u.getWorld())) {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
					for (int i = 0; i < count; i++) {
						u.getWorld().dropItem(u.getLocation(), getItem());
					}
					lastBuyer = user.toString();
					bought += count;
					buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 0L) + count);
					buyerTimeMap.put(user.toString(), time);
					if (buyerAmountMap.containsKey(time)) {
						buyerAmountMap.put(time, buyerAmountMap.get(time) + count);
					} else {
						buyerAmountMap.put(time, (long) count);
					}
					wallet.ifPresent(w -> {
						if (w.has(price, u.getWorld())) {
							w.withdraw(BigDecimal.valueOf(price), u.getWorld());
						}
					});
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:

				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					ItemModificationEvent event = new ItemModificationEvent(u, this, TransactionResult.Sell);
					Bukkit.getPluginManager().callEvent(event);
					if (RetroConomy.getInstance().itemRemoval(u, getItem(), count).isTransactionSuccess()) {
						double amount = getSellPrice(count);
						lastSeller = user.toString();
						sold += count;
						sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 0L) + count);
						sellerTimeMap.put(user.toString(), time);
						if (sellerAmountMap.containsKey(time)) {
							sellerAmountMap.put(time, sellerAmountMap.get(time) + count);
						} else {
							sellerAmountMap.put(time, (long) count);
						}
						RetroConomy.getInstance().getManager().getWallet(user).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(amount), u.getWorld()));
					} else {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			default:
				return RetroConomy.PlayerTransactionResult.FAILED;
		}
	}

	@Override
	public RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result) {
		switch (result) {
			case Buy:
				lastBuyer = user.toString();
				if (bought > 0) {
					bought--;
					buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 1L) - 1);
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:
				lastSeller = user.toString();
				if (sold > 0) {
					sold--;
					sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 1L) - 1);
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			default:
				return RetroConomy.PlayerTransactionResult.FAILED;
		}
	}

	@Override
	public RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result, int count) {
		switch (result) {
			case Buy:
				lastBuyer = user.toString();
				if (bought > 0) {
					bought -= count;
					buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 1L) - count);
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:
				lastSeller = user.toString();
				if (sold > 0) {
					sold -= count;
					sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 1L) - count);
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			default:
				return RetroConomy.PlayerTransactionResult.FAILED;
		}
	}
}

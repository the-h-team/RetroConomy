/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.item;

import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroWallet;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
	private double multiplier;
	private int bought;
	private int sold;
	private String lastBuyer = "No-one";
	private String lastSeller = "No-one";

	public SystemItem(ItemStack item, double price, double multiplier, Map<String, Long> buyerMap, Map<String, Long> sellerMap, Map<String, Long> buyerTimeMap, Map<String, Long> sellerTimeMap) {
		this.item = item;
		this.price = price;
		this.multiplier = multiplier;
		this.buyerMap = new HashMap<>(buyerMap);
		this.sellerMap = new HashMap<>(sellerMap);
		this.buyerTimeMap = new HashMap<>(buyerTimeMap);
		this.sellerTimeMap = new HashMap<>(sellerTimeMap);
		this.bought = 0;
		this.sold = 0;
		RetroConomy.getInstance().getManager().getShop().list().add(this);
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public double getMultiplier() {
		return multiplier;
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
	public long getSold(String user) {
		return sellerMap.getOrDefault(user, 0L);
	}

	@Override
	public long getSoldLast(String user) {
		return sellerTimeMap.getOrDefault(user, 0L);
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
	public long getBought(String user) {
		return buyerMap.getOrDefault(user, 0L);
	}

	@Override
	public long getBoughtLast(String user) {
		return buyerTimeMap.getOrDefault(user, 0L);
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
		return (double) getBought() * getSold() / 2;
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
	public Map<String, Long> getBuyerMap() {
		return buyerMap;
	}

	@Override
	public Map<String, Long> getSellerMap() {
		return sellerMap;
	}

	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(Player who, double multiplier) {
		if (who.isOp()) {
			this.multiplier = multiplier;
			return RetroConomy.PlayerTransactionResult.SUCCESS;
		}
		return RetroConomy.PlayerTransactionResult.FAILED;
	}

	@Override
	public RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result) {
		switch (result) {
			case Buy:
				lastBuyer = user.toString();
				bought++;
				buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 0L) + 1);
				buyerTimeMap.put(user.toString(), System.currentTimeMillis());
				// give item if online
				double price = this.price * getMultiplier();
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					u.getWorld().dropItem(u.getLocation(), getItem());
					Optional<RetroWallet> wallet = RetroConomy.getInstance().getManager().getWallet(user);
					if (wallet.isPresent() && !wallet.get().has(price, u.getWorld())) {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
					wallet.ifPresent(w -> {
						if (w.has(price, u.getWorld())) {
							w.withdraw(BigDecimal.valueOf(price));
						}
					});
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:
				lastSeller = user.toString();
				sold++;
				sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 0L) + 1);
				sellerTimeMap.put(user.toString(), System.currentTimeMillis());
				// take item if online
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					if (RetroConomy.getInstance().itemRemoval(u, getItem(), 1).isTransactionSuccess()) {
						double amount = (this.price * getMultiplier()) / 2;
						RetroConomy.getInstance().getManager().getWallet(user).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(amount)));
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
		switch (result) {
			case Buy:
				lastBuyer = user.toString();
				bought += count;
				buyerMap.put(user.toString(), buyerMap.getOrDefault(user.toString(), 0L) + count);
				buyerTimeMap.put(user.toString(), System.currentTimeMillis());
				// give items if online
				double price = (this.price * getMultiplier()) * count;
				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					for (int i = 0; i < count; i++) {
						u.getWorld().dropItem(u.getLocation(), getItem());
					}
					Optional<RetroWallet> wallet = RetroConomy.getInstance().getManager().getWallet(user);
					if (wallet.isPresent() && !wallet.get().has(price, u.getWorld())) {
						return RetroConomy.PlayerTransactionResult.FAILED;
					}
					wallet.ifPresent(w -> {
						if (w.has(price, u.getWorld())) {
							w.withdraw(BigDecimal.valueOf(price));
						}
					});
				} else {
					return RetroConomy.PlayerTransactionResult.FAILED;
				}
				return RetroConomy.PlayerTransactionResult.SUCCESS;
			case Sell:
				lastSeller = user.toString();
				sold += count;
				sellerMap.put(user.toString(), sellerMap.getOrDefault(user.toString(), 0L) + count);
				sellerTimeMap.put(user.toString(), System.currentTimeMillis());
				// take items if online

				if (Bukkit.getOfflinePlayer(user).isOnline()) {
					Player u = Bukkit.getPlayer(user);
					if (RetroConomy.getInstance().itemRemoval(u, getItem(), count).isTransactionSuccess()) {
						double amount = (this.price * getMultiplier()) * count / 2;
						RetroConomy.getInstance().getManager().getWallet(user).ifPresent(wallet -> wallet.deposit(BigDecimal.valueOf(amount)));
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

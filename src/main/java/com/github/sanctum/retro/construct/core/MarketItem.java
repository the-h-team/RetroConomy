/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;


import com.github.sanctum.retro.RetroConomy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


// To be made.. For possible player market setup. Not super important ( Compare to BDO marketplace )
public class MarketItem implements ItemDemand {

	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean isBlacklisted() {
		return false;
	}

	@Override
	public long getRecentBought() {
		return 0;
	}

	@Override
	public long getRecentSold() {
		return 0;
	}

	@Override
	public long getSold(UUID user) {
		return 0;
	}

	@Override
	public long getSoldLast(UUID user) {
		return 0;
	}

	@Override
	public long getSold(TimeUnit unit, int time) {
		return 0;
	}

	@Override
	public long getSold() {
		return 0;
	}

	@Override
	public long getBought(UUID user) {
		return 0;
	}

	@Override
	public long getBoughtLast(UUID user) {
		return 0;
	}

	@Override
	public long getBought(TimeUnit unit, int time) {
		return 0;
	}

	@Override
	public long getBought() {
		return 0;
	}

	@Override
	public double getPopularity() {
		return 0;
	}

	@Override
	public String getLastBuyer() {
		return null;
	}

	@Override
	public String getLastSeller() {
		return null;
	}

	@Override
	public Map<String, Long> getBuyerTimeMap() {
		return null;
	}

	@Override
	public Map<String, Long> getSellerTimeMap() {
		return null;
	}

	@Override
	public Map<Long, Long> getBuyerAmountMap() {
		return null;
	}

	@Override
	public Map<Long, Long> getSellerAmountMap() {
		return null;
	}

	@Override
	public Map<String, Long> getBuyerMap() {
		return null;
	}

	@Override
	public Map<String, Long> getSellerMap() {
		return null;
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(Player who, double multiplier) {
		return null;
	}

	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(double multiplier) {
		return null;
	}

	@Override
	public RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result) {
		return null;
	}

	@Override
	public RetroConomy.PlayerTransactionResult invoke(UUID user, TransactionResult result, int count) {
		return null;
	}

	@Override
	public RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result) {
		return null;
	}

	@Override
	public RetroConomy.PlayerTransactionResult undo(UUID user, TransactionResult result, int count) {
		return null;
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

	@Override
	public double getBasePrice() {
		return 0;
	}

	@Override
	public double getBuyPrice(int amount) {
		return 0;
	}

	@Override
	public double getSellPrice(int amount) {
		return 0;
	}

	@Override
	public double getCeiling() {
		return 0;
	}

	@Override
	public double getFloor() {
		return 0;
	}

	@Override
	public double getMultiplier() {
		return 0;
	}
}

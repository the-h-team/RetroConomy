package com.github.sanctum.retro.construct.item;


import com.github.sanctum.retro.RetroConomy;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


// To be made.. For possible player market setup. Not super important
public class MarketItem implements ItemDemand{
	@Override
	public ItemStack getItem() {
		return null;
	}

	@Override
	public double getPrice() {
		return 0;
	}

	@Override
	public double getMultiplier() {
		return 0;
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
	public long getSold(String user) {
		return 0;
	}

	@Override
	public long getSold() {
		return 0;
	}

	@Override
	public long getBought(String user) {
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
	public Map<String, Long> getBuyerMap() {
		return null;
	}

	@Override
	public Map<String, Long> getSellerMap() {
		return null;
	}


	@Override
	public RetroConomy.PlayerTransactionResult adjustMultiplier(Player who, double multiplier) {
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
}

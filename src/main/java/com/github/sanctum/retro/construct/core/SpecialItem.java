package com.github.sanctum.retro.construct.core;

import java.util.Map;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class SpecialItem extends SystemItem implements Ownable{

	private final String id;
	private int amount;

	public SpecialItem(String path, String owner, int amount, ItemStack item, double price, double multiplier, double ceiling, double floor, Map<String, Long> buyerMap, Map<String, Long> sellerMap, Map<String, Long> buyerTimeMap, Map<String, Long> sellerTimeMap, Map<Long, Long> sellerAmountMap, Map<Long, Long> buyerAmountMap) {
		super(path, item, price, multiplier, ceiling, floor, buyerMap, sellerMap, buyerTimeMap, sellerTimeMap, sellerAmountMap, buyerAmountMap);
		this.amount = amount;
		this.id = owner;
	}

	@Override
	public UUID getOwner() {
		return UUID.fromString(this.id);
	}

	@Override
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}

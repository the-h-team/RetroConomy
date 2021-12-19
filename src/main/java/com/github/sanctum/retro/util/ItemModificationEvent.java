package com.github.sanctum.retro.util;

import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.api.Modifiable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ItemModificationEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ItemDemand item;

	private final Player buyer;

	private final Modifiable.TransactionResult transaction;

	public ItemModificationEvent(Player buyer, ItemDemand item, Modifiable.TransactionResult transaction) {
		this.item = item;
		this.buyer = buyer;
		this.transaction = transaction;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Modifiable.TransactionResult getTransaction() {
		return transaction;
	}

	public Player getBuyer() {
		return buyer;
	}

	public ItemDemand getItem() {
		return item;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}


}

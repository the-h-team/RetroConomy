package com.github.sanctum.retro.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class NotifiableEntity {

	private static final Set<NotifiableEntity> ENTITIES = new HashSet<>();

	private final Set<Notifications> MESSAGING = new HashSet<>();

	private final UUID id;

	protected NotifiableEntity(UUID user) {
		this.id = user;
		MESSAGING.addAll(Arrays.asList(Notifications.values()));
		ENTITIES.add(this);
	}

	public static NotifiableEntity pick(OfflinePlayer player) {
		for (NotifiableEntity entity : ENTITIES) {
			if (entity.getId().equals(player.getUniqueId())) {
				return entity;
			}
		}
		return new NotifiableEntity(player.getUniqueId());
	}

	public static NotifiableEntity pick(UUID id) {
		for (NotifiableEntity entity : ENTITIES) {
			if (entity.getId().equals(id)) {
				return entity;
			}
		}
		return new NotifiableEntity(id);
	}

	public UUID getId() {
		return id;
	}

	public OfflinePlayer getUser() {
		return Bukkit.getOfflinePlayer(getId());
	}

	public boolean has(Notifications notifications) {
		return MESSAGING.contains(notifications);
	}

	public boolean add(Notifications notifications) {
		return MESSAGING.add(notifications);
	}

	public boolean remove(Notifications notifications) {
		return MESSAGING.remove(notifications);
	}


	public enum Notifications {
		MARKET_PRICE_CHANGE("price_change"), MARKET_OUT_OF_STOCK("out_of_stock"), MARKET_RE_STOCKED("re_stocked"), MARKET_ITEM_ADDED("item_added");

		private final String tag;

		Notifications(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}
	}

}

package com.youtube.hempfest.retro.construct.entity;

import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import org.bukkit.OfflinePlayer;

public class RetroPlayer implements EconomyEntity {

	private final OfflinePlayer player;

	public RetroPlayer(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public String friendlyName() {
		return player.getName();
	}

	@Override
	public String id() {
		return player.getUniqueId().toString();
	}
}

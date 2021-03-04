package com.youtube.hempfest.retro.construct.entity;

import com.github.sanctum.economy.construct.entity.EconomyEntity;
import com.github.sanctum.labyrinth.library.HUID;
import org.jetbrains.annotations.NotNull;

public class ServerEntity implements EconomyEntity {

	private final String name;
	private final HUID id;

	public ServerEntity(String name) {
		this.name = name;
		this.id = HUID.randomID();
	}

	@Override
	public @NotNull String friendlyName() {
		return name;
	}

	@Override
	public @NotNull String id() {
		return id.toString();
	}
}

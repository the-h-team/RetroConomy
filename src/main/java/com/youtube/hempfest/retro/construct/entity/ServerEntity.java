package com.youtube.hempfest.retro.construct.entity;

import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.hempcore.library.HUID;

public class ServerEntity implements EconomyEntity {

	private final String name;
	private final HUID id;

	public ServerEntity(String name) {
		this.name = name;
		this.id = HUID.randomID();
	}

	@Override
	public String friendlyName() {
		return name;
	}

	@Override
	public String id() {
		return id.toString();
	}
}

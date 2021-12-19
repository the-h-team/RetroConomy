package com.github.sanctum.retro.api;

import java.util.UUID;

public interface ItemDemandOwnable extends ItemDemand {

	UUID getOwner();

	int getAmount();

}

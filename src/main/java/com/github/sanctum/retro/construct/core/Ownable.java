package com.github.sanctum.retro.construct.core;

import java.util.UUID;

public interface Ownable extends ItemDemand {

	UUID getOwner();

	int getAmount();

}

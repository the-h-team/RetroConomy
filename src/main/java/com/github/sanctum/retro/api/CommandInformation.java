/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.api;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandInformation {

	@NotNull String getLabel();

	@NotNull String getDescription();

	@NotNull String getUsage();

	@Nullable String getPermission();

	default @NotNull List<String> getAliases() {
		return Collections.emptyList();
	}

}

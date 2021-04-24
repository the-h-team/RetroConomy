package com.github.sanctum.retro.command;

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

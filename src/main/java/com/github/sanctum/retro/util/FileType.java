/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.retro.RetroConomy;

public enum FileType {

	MISC, ACCOUNT;

	public FileManager get() {
		switch (this) {
			case ACCOUNT:
				return RetroConomy.getInstance().getFiles().find("Accounts", "Data");
			default:
				throw new IllegalStateException("Invalid file type selected! This is not internal!");
		}

	}

	public FileManager get(String name) {
		return RetroConomy.getInstance().getFiles().find(name, "Configuration");
	}

}

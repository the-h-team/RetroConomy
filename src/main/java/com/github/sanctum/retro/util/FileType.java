package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.retro.RetroConomy;

public enum FileType {

	MISC, ACCOUNT;

	public FileManager get() {
		if (this == FileType.ACCOUNT) {
			return RetroConomy.getInstance().getFiles().find("Accounts", "Data");
		}
		throw new IllegalStateException("Invalid file type selected! This is not internal!");
	}

	public FileManager get(String name) {
		return RetroConomy.getInstance().getFiles().find(name, "Configuration");
	}

}

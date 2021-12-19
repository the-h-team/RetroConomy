/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.retro.RetroConomy;
import java.io.InputStream;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfiguredMessage {

	public static String getMessage(String path) {
		FileManager file = FileReader.MISC.get("Messages");
		if (!file.getRoot().exists()) {
			InputStream is = JavaPlugin.getProvidingPlugin(RetroConomy.class).getResource("Messages.yml");
			FileList.copy(is, file.getRoot().getParent());
			file.getRoot().reload();
		}
		return file.getRoot().getString(path);
	}

}

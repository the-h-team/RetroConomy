package com.youtube.hempfest.retro.hook;

import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import com.github.sanctum.labyrinth.task.Schedule;
import com.youtube.hempfest.retro.RetroConomy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class HempEconomy {

	private final RetroConomy plugin;
	private AdvancedEconomy provider;

	public HempEconomy(RetroConomy plugin) {
		this.plugin = plugin;
	}

	public void hook() {
		provider = plugin.economy;
		Schedule.async(() -> Bukkit.getServicesManager().register(AdvancedEconomy.class, this.provider, this.plugin, ServicePriority.Highest)).wait(3);
		plugin.getLogger().info("- Advanced economy hooked! Now registered as a provider");
	}

	public void unhook() {
		Bukkit.getServicesManager().unregister(AdvancedEconomy.class, this.provider);
		plugin.getLogger().info("- Advanced economy un-hooked! No longer registered as a provider");
	}

}

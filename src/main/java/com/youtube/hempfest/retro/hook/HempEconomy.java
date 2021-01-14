package com.youtube.hempfest.retro.hook;

import com.youtube.hempfest.economy.construct.implement.AdvancedEconomy;
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
		Bukkit.getServicesManager().register(AdvancedEconomy.class, this.provider, this.plugin, ServicePriority.Highest);
		plugin.getLogger().info("- Advanced economy hooked! Now registered as a provider");
	}

	public void unhook() {
		Bukkit.getServicesManager().unregister(AdvancedEconomy.class, this.provider);
		plugin.getLogger().info("- Advanced economy un-hooked! No longer registered as a provider");
	}

}

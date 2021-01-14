package com.youtube.hempfest.retro;

import com.youtube.hempfest.hempcore.command.CommandBuilder;
import com.youtube.hempfest.hempcore.event.EventBuilder;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.construct.economy.Economy;
import com.youtube.hempfest.retro.data.Config;
import com.youtube.hempfest.retro.hook.HempEconomy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class RetroConomy extends JavaPlugin implements RetroAPI {

	private static RetroConomy instance;

	public final Economy economy = new Economy();

	public HempEconomy hook;

	public Config serverDir = Config.get(Config.AccountFile.SERVER);

	public Config bankDir = Config.get(Config.AccountFile.BANK);

	public Config entityDir = Config.get(Config.AccountFile.ENTITY);

	public Config walletDir = Config.get("Accounts", "Wallet");

	@Override
	public void onEnable() {
		instance = this;
		if (Bukkit.getPluginManager().isPluginEnabled("Hemponomics")) {
			hook = new HempEconomy(this);
			hook.hook();
		}
		new CommandBuilder(this).compileFields("com.youtube.hempfest.retro.command");
		new EventBuilder(this).compileFields("com.youtube.hempfest.retro.events.listener");
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		if (Bukkit.getPluginManager().isPluginEnabled("Hemponomics")) {
			hook.unhook();
		}
	}

	public static RetroConomy getInstance() {
		return instance;
	}

	@Override
	public List<String> getAccounts(String name) {
		return null;
	}

	@Override
	public List<String> getAccounts(String name, String world) {
		return null;
	}

	@Override
	public List<String> getAccounts(UUID uuid) {
		return null;
	}

	@Override
	public List<String> getAccounts(UUID uuid, String world) {
		return null;
	}

	@Override
	public List<String> getAccounts(OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public List<String> getAccounts(OfflinePlayer offlinePlayer, String world) {
		return null;
	}

	@Override
	public void createAccount(FundingSource type, String holder, String accountId) {

	}

	@Override
	public void createAccount(FundingSource type, String holder, String accountId, String world) {

	}

	@Override
	public void createAccount(FundingSource type, UUID holder, String accountId) {

	}

	@Override
	public void createAccount(FundingSource type, UUID holder, String accountId, String world) {

	}

	@Override
	public void createAccount(FundingSource type, OfflinePlayer holder, String accountId) {
		Config config;
		List<String> array = new ArrayList<>();
		switch (type) {
			case SERVER_ACCOUNT:
				config = serverDir;
				for (World w : Bukkit.getWorlds()) {
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".balance", 0);
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".owner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".jointowner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".members", array);
					break;
				}
				config.saveConfig();
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				for (World w : Bukkit.getWorlds()) {
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".balance", 0);
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".owner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".jointowner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".members", array);
					break;
				}
				config.saveConfig();
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				for (World w : Bukkit.getWorlds()) {
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".balance", 0);
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".owner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".jointowner", holder.getUniqueId().toString());
					config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + w + "." + accountId + ".members", array);
					break;
				}
				config.saveConfig();
				break;
		}
	}

	@Override
	public void createAccount(FundingSource type, OfflinePlayer holder, String accountId, String world) {
		Config config;
		List<String> array = new ArrayList<>();
		switch (type) {
			case SERVER_ACCOUNT:
				config = serverDir;
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".balance", 0);
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".owner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".jointowner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".members", array);
				config.saveConfig();
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".balance", 0);
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".owner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".jointowner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".members", array);
				config.saveConfig();
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".balance", 0);
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".owner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".jointowner", holder.getUniqueId().toString());
				config.getConfig().set("Index." + holder.getUniqueId().toString() + ".accounts." + world + "." + accountId + ".members", array);
				config.saveConfig();
				break;
		}
	}

	@Override
	public void deleteAccount(FundingSource type, String accountId) {
		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
					config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId, null);
					config.saveConfig();
				}
			}
		}
	}

	@Override
	public void deleteAccount(FundingSource type, String accountId, String world) {
		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
				config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId, null);
				config.saveConfig();
			}
		}
	}

	@Override
	public String getAccountID(FundingSource type, String name) {
		return null;
	}

	@Override
	public String getAccountID(FundingSource type, String name, String world) {
		return null;
	}

	@Override
	public String getAccountID(FundingSource type, UUID uuid) {
		return null;
	}

	@Override
	public String getAccountID(FundingSource type, UUID uuid, String world) {
		return null;
	}

	@Override
	public String getAccountID(FundingSource type, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public String getAccountID(FundingSource type, OfflinePlayer offlinePlayer, String world) {
		return null;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, String name) {
		return false;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, String world, String name) {
		return false;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, String world, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean isAccountMember(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, String name) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, String world, String name) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, String world, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean isAccountOwner(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, String name) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, String world, String name) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, String world, UUID uuid) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean isAccountJointOwner(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean accountHas(FundingSource type, String accountId, BigDecimal amount) {
		Config config = null;
		boolean result = false;
		switch (type) {
			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		if (!config.exists() || config.getConfig().getConfigurationSection("Index").getKeys(false).isEmpty()) {
			return false;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
					result = BigDecimal.valueOf(config.getConfig().getDouble("Index." + ent + ".accounts." + world + "." + accountId + ".balance")).doubleValue() >= amount.doubleValue();
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean accountHas(FundingSource type, String accountId, String world, BigDecimal amount) {
		Config config = null;
		boolean result = false;
		switch (type) {
			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		if (!config.exists() || config.getConfig().getConfigurationSection("Index").getKeys(false).isEmpty()) {
			return false;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
				result = BigDecimal.valueOf(config.getConfig().getDouble("Index." + ent + ".accounts." + world + "." + accountId + ".balance")).doubleValue() >= amount.doubleValue();
				break;
			}
		}
		return result;
	}

	@Override
	public boolean accountExists(FundingSource type, String accountId) {
		Config config = null;
		boolean result = false;
		switch (type) {
			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		if (!config.exists() || config.getConfig().getConfigurationSection("Index").getKeys(false).isEmpty()) {
			return false;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				result = config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId);
			}
		}
		return result;
	}

	@Override
	public boolean accountExists(FundingSource type, String accountId, String world) {
		Config config = null;
		boolean result = false;
		switch (type) {
			case SERVER_ACCOUNT:
				config = null;
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = null;
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = null;
				config = bankDir;
				break;
		}
		if (config.exists()) {
			Bukkit.getLogger().info("Directory: " + config.getDirectory());
		}
		if (!config.exists() || config.getConfig().getConfigurationSection("Index").getKeys(false).isEmpty()) {
			return false;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			result = config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId);
		}
		return result;
	}

	@Override
	public void depositAccount(FundingSource type, String accountId, BigDecimal amount) {

		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
					config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", (getAccountBalance(type, accountId).doubleValue() + amount.doubleValue()));
					break;
				}
			}
		}
		config.saveConfig();
	}

	@Override
	public void depositAccount(FundingSource type, String accountId, String world, BigDecimal amount) {

		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
				config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", (getAccountBalance(type, accountId).doubleValue() + amount.doubleValue()));
				break;
			}
		}
		config.saveConfig();

	}

	@Override
	public void setAccountBalance(FundingSource type, String accountId, BigDecimal amount) {
		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
					config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", amount.doubleValue());
					break;
				}
			}
		}
		config.saveConfig();
	}

	@Override
	public void setAccountBalance(FundingSource type, String accountId, String world, BigDecimal amount) {
		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
				config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", amount.doubleValue());
				break;
			}
		}
		config.saveConfig();
	}

	@Override
	public void withdrawAccount(FundingSource type, String accountId, BigDecimal amount) {

		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
					config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", (getAccountBalance(type, accountId).doubleValue() - amount.doubleValue()));
					break;
				}
			}
		}
		config.saveConfig();
	}

	@Override
	public void withdrawAccount(FundingSource type, String accountId, String world, BigDecimal amount) {

		Config config = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			if (config.getConfig().getConfigurationSection("Index." + ent + ".accounts." + world).getKeys(false).contains(accountId)) {
				config.getConfig().set("Index." + ent + ".accounts." + world + "." + accountId + ".balance", (getAccountBalance(type, accountId).doubleValue() - amount.doubleValue()));
				break;
			}
		}
		config.saveConfig();
	}

	@Override
	public BigDecimal getAccountBalance(FundingSource type, String accountId) {

		Config config = null;
		BigDecimal result = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			for (String world : config.getConfig().getConfigurationSection("Index." + ent + ".accounts").getKeys(false)) {
				result = BigDecimal.valueOf(config.getConfig().getDouble("Index." + ent + ".accounts." + world + "." + accountId + ".balance"));
				break;
			}
		}
		return result;
	}

	@Override
	public BigDecimal getAccountBalance(FundingSource type, String accountId, String world) {

		Config config = null;
		BigDecimal result = null;
		switch (type) {

			case SERVER_ACCOUNT:
				config = serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = entityDir;
				break;
			case BANK_ACCOUNT:
				config = bankDir;
				break;
		}
		for (String ent : config.getConfig().getConfigurationSection("Index").getKeys(false)) {
			result = BigDecimal.valueOf(config.getConfig().getDouble("Index." + ent + ".accounts." + world + "." + accountId + ".balance"));
			break;
		}
		return result;
	}

	@Override
	public void depositWallet(String name, BigDecimal amount) {

	}

	@Override
	public void depositWallet(String name, String world, BigDecimal amount) {

	}

	@Override
	public void withdrawWallet(String name, BigDecimal amount) {

	}

	@Override
	public void withdrawWallet(String name, String world, BigDecimal amount) {

	}

	@Override
	public void depositWallet(UUID uuid, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current + amount.doubleValue();
		config.getConfig().set("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void depositWallet(UUID uuid, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + world + ".balance");
		double result = current + amount.doubleValue();
		config.getConfig().set("Index." + uuid.toString() + "." + world + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void withdrawWallet(UUID uuid, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		config.getConfig().set("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void withdrawWallet(UUID uuid, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		config.getConfig().set("Index." + uuid.toString() + "." + world + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void depositWallet(OfflinePlayer offlinePlayer, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current + amount.doubleValue();
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void depositWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance");
		double result = current + amount.doubleValue();
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void withdrawWallet(OfflinePlayer offlinePlayer, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void withdrawWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance", result);
		config.saveConfig();
	}

	@Override
	public void walletSetBalance(String name, BigDecimal amount) {

	}

	@Override
	public void walletSetBalance(String name, String world, BigDecimal amount) {

	}

	@Override
	public void walletSetBalance(UUID uuid, BigDecimal amount) {
		Config config = walletDir;
		config.getConfig().set("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", amount.doubleValue());
		config.saveConfig();
	}

	@Override
	public void walletSetBalance(UUID uuid, String world, BigDecimal amount) {
		Config config = walletDir;
		config.getConfig().set("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", amount.doubleValue());
		config.saveConfig();
	}

	@Override
	public void walletSetBalance(OfflinePlayer offlinePlayer, BigDecimal amount) {
		Config config = walletDir;
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance", amount.doubleValue());
		config.saveConfig();
	}

	@Override
	public void walletSetBalance(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		Config config = walletDir;
		config.getConfig().set("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance", amount.doubleValue());
		config.saveConfig();
	}

	@Override
	public boolean walletHas(String name, BigDecimal amount) {
		return false;
	}

	@Override
	public boolean walletHas(String name, String world, BigDecimal amount) {
		return false;
	}

	@Override
	public boolean walletHas(UUID uuid, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");
	}

	@Override
	public boolean walletHas(UUID uuid, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");
	}

	@Override
	public boolean walletHas(OfflinePlayer offlinePlayer, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");
	}

	@Override
	public boolean walletHas(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");
	}

	@Override
	public BigDecimal getWalletBalance(String name) {
		return null;
	}

	@Override
	public BigDecimal getWalletBalance(String name, String world) {
		return null;
	}

	@Override
	public BigDecimal getWalletBalance(UUID uuid) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		return BigDecimal.valueOf(current);
	}

	@Override
	public BigDecimal getWalletBalance(UUID uuid, String world) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + uuid.toString() + "." + world + ".balance");
		return BigDecimal.valueOf(current);
	}

	@Override
	public BigDecimal getWalletBalance(OfflinePlayer offlinePlayer) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		return BigDecimal.valueOf(current);
	}

	@Override
	public BigDecimal getWalletBalance(OfflinePlayer offlinePlayer, String world) {
		Config config = walletDir;
		double current = config.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance");
		return BigDecimal.valueOf(current);
	}


}

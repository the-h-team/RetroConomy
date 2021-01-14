package com.youtube.hempfest.retro.construct.economy;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.EconomyPriority;
import com.youtube.hempfest.economy.construct.account.Account;
import com.youtube.hempfest.economy.construct.account.Wallet;
import com.youtube.hempfest.economy.construct.account.permissive.AccountType;
import com.youtube.hempfest.economy.construct.currency.normal.EconomyCurrency;
import com.youtube.hempfest.economy.construct.implement.AdvancedEconomy;
import com.youtube.hempfest.hempcore.library.HUID;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import com.youtube.hempfest.retro.construct.account.RetroAccount;
import com.youtube.hempfest.retro.construct.account.RetroPlayerAccount;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.construct.entity.ServerEntity;
import com.youtube.hempfest.retro.construct.wallet.RetroPlayerWallet;
import com.youtube.hempfest.retro.construct.wallet.RetroServerWallet;
import com.youtube.hempfest.retro.data.Config;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class Economy implements AdvancedEconomy {

	@Override
	public Plugin getPlugin() {
		return RetroConomy.getInstance();
	}

	@Override
	public String getVersion() {
		return RetroConomy.getInstance().getDescription().getVersion();
	}

	@Override
	public EconomyCurrency getCurrency() {
		Config options = Config.getOptions();
		String ms = options.getConfig().getString("Currency.Major.Singular");
		String mp = options.getConfig().getString("Currency.Major.Plural");
		String mis = options.getConfig().getString("Currency.Minor.Singular");
		String mip = options.getConfig().getString("Currency.Minor.Plural");
		Locale locale = null;
		switch (options.getConfig().getString("Currency.Format.locale").toLowerCase()) {
			case "us":
				locale = Locale.ENGLISH;
				break;
			case "ru":
				locale = new Locale("ru", "RU");
				break;
			case "de":
				locale = Locale.GERMAN;
				break;
		}
		return EconomyCurrency.getCurrencyLayoutBuilder().setMajorSingular(ms).setMajorPlural(mp).setMinorSingular(mis).setMinorPlural(mip).setWorld(Bukkit.getWorlds().get(0).getName()).setLocale(locale).toCurrency();
	}

	@Override
	public EconomyCurrency getCurrency(String world) {
		return null;
	}

	@Override
	public EconomyPriority getPriority() {
		return EconomyPriority.HIGHEST;
	}

	@Override
	public String format(BigDecimal amount) {
		return null;
	}

	@Override
	public String format(BigDecimal amount, Locale locale) {
		return null;
	}

	@Override
	public BigDecimal getMaxWalletSize() {
		return null;
	}

	@Override
	public boolean isMultiWorld() {
		return false;
	}

	@Override
	public boolean isMultiCurrency() {
		return false;
	}

	@Override
	public boolean hasMultiAccountSupport() {
		return false;
	}

	@Override
	public boolean hasWalletSizeLimit() {
		return false;
	}

	@Override
	public boolean hasWalletAccount(String name) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(String name, String world) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(OfflinePlayer player) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(OfflinePlayer player, String world) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(UUID uuid) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(UUID uuid, String world) {
		return false;
	}

	@Override
	public boolean hasAccount(String name) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return false;
	}

	@Override
	public boolean hasAccount(String accountId, String name) {
		return false;
	}

	@Override
	public boolean hasAccount(UUID uuid) {
		return false;
	}

	@Override
	public Account getAccount(String name) {
		return new RetroAccount("server-main", AccountType.SERVER_ACCOUNT, new ServerEntity(name));
	}

	@Override
	public Account getAccount(String name, AccountType type) {
		return new RetroAccount("server-main", type, new ServerEntity(name));
	}

	@Override
	public Account getAccount(String accountId, String name) {
		return new RetroAccount(accountId, AccountType.SERVER_ACCOUNT, new ServerEntity(name));
	}

	@Override
	public Account getAccount(OfflinePlayer player, AccountType type) {
		return new RetroPlayerAccount("", type, player);
	}

	@Override
	public Account getAccount(OfflinePlayer player) {
		return new RetroPlayerAccount("", AccountType.BANK_ACCOUNT, player);
	}

	@Override
	public Account getAccount(String accountId, OfflinePlayer player) {
		return new RetroPlayerAccount(accountId, AccountType.BANK_ACCOUNT, player);
	}

	@Override
	public Account getAccount(UUID uuid) {
		// This has a good chance to blow up if we pass a non-player UUID
		//return new RetroAccount("", AccountType.BANK_ACCOUNT, new PlayerEntity(Bukkit.getOfflinePlayer(uuid)));
		// suggestion
		final Optional<OfflinePlayer> optionalPlayer = getOfflinePlayerByUUID(uuid);
		if (!optionalPlayer.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
		return new RetroPlayerAccount("", AccountType.BANK_ACCOUNT, optionalPlayer.get());
	}

	@Override
	public Account getAccount(UUID uuid, AccountType type) {
		//return new RetroAccount("", type, new PlayerEntity(Bukkit.getOfflinePlayer(uuid)));
		final Optional<OfflinePlayer> optionalPlayer = getOfflinePlayerByUUID(uuid);
		if (!optionalPlayer.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
		return new RetroPlayerAccount("", type, optionalPlayer.get());
	}

	@Override
	public Account getAccount(String accountId, UUID uuid) {
		//return new RetroAccount(accountId, AccountType.BANK_ACCOUNT, new PlayerEntity(Bukkit.getOfflinePlayer(uuid)));
		final Optional<OfflinePlayer> optionalPlayer = getOfflinePlayerByUUID(uuid);
		if (!optionalPlayer.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
		return new RetroPlayerAccount(accountId, AccountType.BANK_ACCOUNT, optionalPlayer.get());
	}

	@Override
	public Wallet getWallet(String name) {
		return new RetroServerWallet(new ServerEntity(name));
	}

	@Override
	public Wallet getWallet(OfflinePlayer player) {
		return new RetroPlayerWallet(player);
	}

	@Override
	public Wallet getWallet(UUID uuid) { // TODO: Decide handling when UUID not of player
		final Optional<OfflinePlayer> optionalPlayer = getOfflinePlayerByUUID(uuid);
		if (!optionalPlayer.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
		return new RetroPlayerWallet(optionalPlayer.get());
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, name, HUID.randomID().toString());
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, name, HUID.randomID().toString());
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, name, HUID.randomID().toString());
				break;
		}
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, name, accountId);
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, name, accountId);
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId);
				break;
		}
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, name, HUID.randomID().toString());
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, name, HUID.randomID().toString());
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, name, HUID.randomID().toString());
				break;
		}
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, name, accountId, world);
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, name, accountId, world);
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId, world);
				break;
		}
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, name, accountId, world);
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, name, accountId, world);
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId, world);
				break;
		}
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {
			case BANK_ACCOUNT:
				api.createAccount(FundingSource.BANK_ACCOUNT, player, HUID.randomID().toString());
				break;
			case ENTITY_ACCOUNT:
				api.createAccount(FundingSource.ENTITY_ACCOUNT, player, HUID.randomID().toString());
				break;
			case SERVER_ACCOUNT:
				api.createAccount(FundingSource.SERVER_ACCOUNT, player, HUID.randomID().toString());
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return null;
	}

	@Override
	public EconomyAction deleteWalletAccount(Wallet wallet) {
		return null;
	}

	@Override
	public EconomyAction deleteWalletAccount(Wallet wallet, String world) {
		return null;
	}

	@Override
	public EconomyAction deleteAccount(String accountID) {
		return null;
	}

	@Override
	public EconomyAction deleteAccount(String accountID, String world) {
		return null;
	}

	@Override
	public EconomyAction deleteAccount(Account account) {
		return null;
	}

	@Override
	public EconomyAction deleteAccount(Account account, String world) {
		return null;
	}

	@Override
	public List<Account> getAccounts() {
		return null;
	}

	@Override
	public List<String> getAccountList() {
		return null;
	}

	private static Optional<OfflinePlayer> getOfflinePlayerByUUID(UUID uuid) {
		final OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		return Optional.ofNullable(CompletableFuture.supplyAsync(() -> {
			for (OfflinePlayer offlinePlayer : offlinePlayers) {
				if (offlinePlayer.getUniqueId().equals(uuid)) return offlinePlayer;
			}
			return null;
		}).join());
	}
}

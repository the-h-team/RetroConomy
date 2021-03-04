package com.youtube.hempfest.retro.construct.economy;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.EconomyPriority;
import com.github.sanctum.economy.construct.account.Account;
import com.github.sanctum.economy.construct.account.Wallet;
import com.github.sanctum.economy.construct.account.permissive.AccountType;
import com.github.sanctum.economy.construct.currency.normal.EconomyCurrency;
import com.github.sanctum.economy.construct.entity.types.PlayerEntity;
import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import com.github.sanctum.labyrinth.library.HUID;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import com.youtube.hempfest.retro.construct.account.RetroAccount;
import com.youtube.hempfest.retro.construct.account.RetroPlayerAccount;
import com.youtube.hempfest.retro.util.Coin;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.construct.entity.ServerEntity;
import com.youtube.hempfest.retro.construct.wallet.RetroPlayerWallet;
import com.youtube.hempfest.retro.construct.wallet.RetroServerWallet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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
		return EconomyCurrency.getCurrencyLayoutBuilder().setMajorSingular(Coin.majorSingular()).setMajorPlural(Coin.majorPlural()).setMinorSingular(Coin.minorSingular()).setMinorPlural(Coin.minorPlural()).setWorld(Bukkit.getWorlds().get(0).getName()).setLocale(Coin.getLocale()).toCurrency();
	}

	@Override
	public EconomyCurrency getCurrency(String world) {
		return getCurrency();
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
		return true;
	}

	@Override
	public boolean isMultiCurrency() {
		return false;
	}

	@Override
	public boolean hasMultiAccountSupport() {
		return true;
	}

	@Override
	public boolean hasWalletSizeLimit() {
		return true;
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
		api.createAccount(FundingSource.SERVER_ACCOUNT, name, HUID.randomID().toString());
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId);
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(FundingSource.SERVER_ACCOUNT, name, HUID.randomID().toString());
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId, world);
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(FundingSource.SERVER_ACCOUNT, name, accountId, world);
		return new EconomyAction(new ServerEntity(name), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), player, HUID.randomID().toString());
		return new EconomyAction(new PlayerEntity(player), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), player, accountId);
		return new EconomyAction(new PlayerEntity(player), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), player, accountId, world);
		return new EconomyAction(new PlayerEntity(player), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), uuid, HUID.randomID().toString());
		return new EconomyAction(new PlayerEntity(Bukkit.getOfflinePlayer(uuid)), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), uuid, accountId);
		return new EconomyAction(new PlayerEntity(Bukkit.getOfflinePlayer(uuid)), true, "New " + type.name().toLowerCase().replace("_", "") + " created.");
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world) {
		RetroAPI api = RetroAPI.getInstance();
		api.createAccount(RetroAccount.convertAccountType(type), uuid, accountId, world);
		return new EconomyAction(new PlayerEntity(Bukkit.getOfflinePlayer(uuid)), true, "New " + type.name().toLowerCase().replace("_", "") + " created in world " + world);
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world, BigDecimal startingAmount) {
		RetroAPI api = RetroAPI.getInstance();
		return null;
	}

	@Override
	public EconomyAction deleteWalletAccount(Wallet wallet) {
		RetroAPI api = RetroAPI.getInstance();
		return null;
	}

	@Override
	public EconomyAction deleteWalletAccount(Wallet wallet, String world) {
		return null;
	}

	@Override
	public EconomyAction deleteAccount(String accountID) {
		final RetroAPI api = RetroAPI.getInstance();
		AtomicReference<Account> owner = new AtomicReference<>();
		getAccounts().forEach(a -> {
			if (a.getId().equals(accountID)) {
				owner.set(a);
				new BukkitRunnable() {
					@Override
					public void run() {
						api.deleteAccount(RetroAccount.convertAccountType(a.getType()), a.getId());
					}
				}.runTaskLaterAsynchronously(RetroConomy.getInstance(), 20);
			}
		});
		return new EconomyAction(owner.get().getHolder(), true, "Deleted bank account " + accountID);
	}

	@Override
	public EconomyAction deleteAccount(String accountID, String world) {
		final RetroAPI api = RetroAPI.getInstance();
		AtomicReference<Account> owner = new AtomicReference<>();
		getAccounts().forEach(a -> {
			if (a.getId().equals(accountID)) {
				owner.set(a);
				new BukkitRunnable() {
					@Override
					public void run() {
						api.deleteAccount(RetroAccount.convertAccountType(a.getType()), a.getId(), world);
					}
				}.runTaskLaterAsynchronously(RetroConomy.getInstance(), 20);
			}
		});
		return new EconomyAction(owner.get().getHolder(), true, "Deleted bank account " + accountID + " in world " + world);
	}

	@Override
	public EconomyAction deleteAccount(Account account) {
		RetroAPI api = RetroAPI.getInstance();
		new BukkitRunnable() {
			@Override
			public void run() {
				api.deleteAccount(RetroAccount.convertAccountType(account.getType()), account.getId());
			}
		}.runTaskLaterAsynchronously(RetroConomy.getInstance(), 20);
		return new EconomyAction(account.getHolder(), true, "Deleted bank account " + account.getId());
	}

	@Override
	public EconomyAction deleteAccount(Account account, String world) {
		RetroAPI api = RetroAPI.getInstance();
		new BukkitRunnable() {
			@Override
			public void run() {
				api.deleteAccount(RetroAccount.convertAccountType(account.getType()), account.getId(), world);
			}
		}.runTaskLaterAsynchronously(RetroConomy.getInstance(), 20);
		return new EconomyAction(account.getHolder(), true, "Deleted bank account " + account.getId());
	}

	@Override
	public List<Account> getAccounts() {
		List<Account> accounts = new ArrayList<>();
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			Account temp = getAccount(p);
			if (temp.exists() && temp.getType() == AccountType.BANK_ACCOUNT) {
				accounts.add(temp);
			}
		}
		return accounts;
	}

	@Override
	public List<String> getAccountList() {
		return getAccounts().stream().map(Account::getId).collect(Collectors.toList());
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

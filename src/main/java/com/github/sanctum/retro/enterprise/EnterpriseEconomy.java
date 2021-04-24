package com.github.sanctum.retro.enterprise;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.EconomyPriority;
import com.github.sanctum.economy.construct.account.Account;
import com.github.sanctum.economy.construct.account.Wallet;
import com.github.sanctum.economy.construct.account.permissive.AccountType;
import com.github.sanctum.economy.construct.currency.normal.EconomyCurrency;
import com.github.sanctum.economy.construct.entity.types.PlayerEntity;
import com.github.sanctum.economy.construct.implement.AdvancedEconomy;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroAccount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class EnterpriseEconomy implements AdvancedEconomy {

	public static void register() {
		Bukkit.getServicesManager().register(AdvancedEconomy.class, new EnterpriseEconomy(), JavaPlugin.getProvidingPlugin(RetroConomy.class), ServicePriority.High);
	}

	@Override
	public Plugin getPlugin() {
		return JavaPlugin.getProvidingPlugin(getClass());
	}

	@Override
	public String getVersion() {
		return "1.0_DEV";
	}

	@Override
	public EconomyCurrency getCurrency() {
		return EconomyCurrency.
				getCurrencyLayoutBuilder()
				.setLocale(RetroConomy.getInstance().getManager().getLocale())
				.setMajorPlural(RetroConomy.getInstance().getManager().getMajorPlural())
				.setMajorSingular(RetroConomy.getInstance().getManager().getMajorSingular())
				.setMinorPlural(RetroConomy.getInstance().getManager().getMinorPlural())
				.setMinorSingular(RetroConomy.getInstance().getManager().getMinorSingular())
				.setWorld(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.multi-world.falsify"))
				.toCurrency();
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
		return RetroConomy.getInstance().getManager().format(amount);
	}

	@Override
	public String format(BigDecimal amount, Locale locale) {
		return RetroConomy.getInstance().getManager().format(amount, locale);
	}

	@Override
	public BigDecimal getMaxWalletSize() {
		return BigDecimal.valueOf(RetroConomy.getInstance().getManager().getMain().getConfig().getDouble("Options.wallets.max-balance"));
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
		return false;
	}

	@Override
	public boolean hasWalletSizeLimit() {
		return true;
	}

	@Override
	@Deprecated
	public boolean hasWalletAccount(String name) {
		return false;
	}

	@Override
	@Deprecated
	public boolean hasWalletAccount(String name, String world) {
		return false;
	}

	@Override
	public boolean hasWalletAccount(OfflinePlayer player) {
		return RetroConomy.getInstance().getManager().getWallet(player).isPresent();
	}

	@Override
	public boolean hasWalletAccount(OfflinePlayer player, String world) {
		return RetroConomy.getInstance().getManager().getWallet(player).isPresent();
	}

	@Override
	public boolean hasWalletAccount(UUID uuid) {
		return RetroConomy.getInstance().getManager().getWallet(uuid).isPresent();
	}

	@Override
	public boolean hasWalletAccount(UUID uuid, String world) {
		return RetroConomy.getInstance().getManager().getWallet(uuid).isPresent();
	}

	@Override
	@Deprecated
	public boolean hasAccount(String name) {
		return RetroConomy.getInstance().getManager().getAccount(name).isPresent();
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return RetroConomy.getInstance().getManager().getAccount(player).isPresent();
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		return RetroConomy.getInstance().getManager().getAccount(player).isPresent();
	}

	@Override
	@Deprecated
	public boolean hasAccount(String accountId, String name) {
		return false;
	}

	@Override
	public boolean hasAccount(UUID uuid) {
		return RetroConomy.getInstance().getManager().getAccount(uuid).isPresent();
	}

	@Override
	public boolean hasAccount(UUID uuid, String world) {
		return RetroConomy.getInstance().getManager().getAccount(uuid).isPresent();
	}

	@Override
	@Deprecated
	public Account getAccount(String name) {
		return null;
	}

	@Override
	@Deprecated
	public Account getAccount(String name, AccountType type) {
		return null;
	}

	@Override
	@Deprecated
	public Account getAccount(String accountId, String name) {
		return null;
	}

	@Override
	public Account getAccount(OfflinePlayer player, AccountType type) {
		return new EnterpriseAccount(player, new PlayerEntity[0]);
	}

	@Override
	public Account getAccount(OfflinePlayer player) {
		return new EnterpriseAccount(player, new PlayerEntity[0]);
	}

	@Override
	public Account getAccount(String accountId, OfflinePlayer player) {
		Optional<RetroAccount> account = RetroConomy.getInstance().getManager().getAccount(HUID.fromString(accountId));
		if (account.isPresent()) {
			if (account.get().getOwner().equals(player.getUniqueId()) || account.get().getMembers().contains(player.getUniqueId().toString())) {
				return new EnterpriseAccount(player);
			}
		}
		return null;
	}

	@Override
	public Account getAccount(UUID uuid) {
		return new EnterpriseAccount(uuid, new PlayerEntity[0]);
	}

	@Override
	public Account getAccount(UUID uuid, AccountType type) {
		return new EnterpriseAccount(uuid, new PlayerEntity[0]);
	}

	@Override
	public Account getAccount(String accountId, UUID uuid) {
		Optional<RetroAccount> account = RetroConomy.getInstance().getManager().getAccount(HUID.fromString(accountId));
		if (account.isPresent()) {
			if (account.get().getOwner().equals(uuid) || account.get().getMembers().contains(uuid.toString())) {
				return new EnterpriseAccount(uuid, new PlayerEntity[0]);
			}
		}
		return null;
	}

	@Override
	@Deprecated
	public Wallet getWallet(String name) {
		return null;
	}

	@Override
	public Wallet getWallet(OfflinePlayer player) {
		return new EnterpriseWallet(player);
	}

	@Override
	public Wallet getWallet(UUID uuid) {
		return new EnterpriseWallet(uuid);
	}

	@Override
	@Deprecated
	public EconomyAction createAccount(AccountType type, String name) {
		return null;
	}

	@Override
	@Deprecated
	public EconomyAction createAccount(AccountType type, String name, String accountId) {
		return null;
	}

	@Override
	@Deprecated
	public EconomyAction createAccount(AccountType type, String name, BigDecimal startingAmount) {
		return null;
	}

	@Override
	@Deprecated
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world) {
		return null;
	}

	@Override
	@Deprecated
	public EconomyAction createAccount(AccountType type, String name, String accountId, String world, BigDecimal startingAmount) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, BigDecimal startingAmount) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, OfflinePlayer player, String accountId, String world, BigDecimal startingAmount) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, BigDecimal startingAmount) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world) {
		return null;
	}

	@Override
	public EconomyAction createAccount(AccountType type, UUID uuid, String accountId, String world, BigDecimal startingAmount) {
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
		List<Account> accounts = new ArrayList<>();
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (hasAccount(player)) {
				accounts.add(new EnterpriseAccount(player, new PlayerEntity[0]));
			}
		}
		return accounts;
	}

	@Override
	public List<String> getAccountList() {
		return RetroConomy.getInstance().getManager().getAccounts().map(RetroAccount::getId).map(HUID::toString).collect(Collectors.toList());
	}
}

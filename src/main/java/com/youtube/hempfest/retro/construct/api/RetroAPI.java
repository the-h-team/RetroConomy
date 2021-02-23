package com.youtube.hempfest.retro.construct.api;

import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.account.FundingSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public interface RetroAPI {

	static RetroAPI getInstance() {
		return RetroConomy.getInstance();
	}

	List<String> getAccounts(String name);

	List<String> getAccounts(String name, String world);

	List<String> getAccounts(UUID uuid);

	List<String> getAccounts(UUID uuid, String world);

	List<String> getAccounts(OfflinePlayer offlinePlayer);

	List<String> getAccounts(OfflinePlayer offlinePlayer, String world);

	void createAccount(FundingSource type, String holder, String accountId);

	void createAccount(FundingSource type, String holder, String accountId, String world);

	void createAccount(FundingSource type, UUID holder, String accountId);

	void createAccount(FundingSource type, UUID holder, String accountId, String world);

	void createAccount(FundingSource type, OfflinePlayer holder, String accountId);

	void createAccount(FundingSource type, OfflinePlayer holder, String accountId, String world);

	void deleteAccount(FundingSource type, String accountId);

	void deleteAccount(FundingSource type, String accountId, String world);

	String getAccountID(FundingSource type, String name);

	String getAccountID(FundingSource type, String name, String world);

	String getAccountID(FundingSource type, UUID uuid);

	String getAccountID(FundingSource type, UUID uuid, String world);

	String getAccountID(FundingSource type, OfflinePlayer offlinePlayer);

	String getAccountID(FundingSource type, OfflinePlayer offlinePlayer, String world);

	boolean isAccountMember(FundingSource type, String accountId, String name);

	boolean isAccountMember(FundingSource type, String accountId, String world, String name);

	boolean isAccountMember(FundingSource type, String accountId, UUID uuid);

	boolean isAccountMember(FundingSource type, String accountId, String world, UUID uuid);

	boolean isAccountMember(FundingSource type, String accountId, OfflinePlayer offlinePlayer);

	boolean isAccountMember(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer);

	boolean isAccountOwner(FundingSource type, String accountId, String name);

	boolean isAccountOwner(FundingSource type, String accountId, String world, String name);

	boolean isAccountOwner(FundingSource type, String accountId, UUID uuid);

	boolean isAccountOwner(FundingSource type, String accountId, String world, UUID uuid);

	boolean isAccountOwner(FundingSource type, String accountId, OfflinePlayer offlinePlayer);

	boolean isAccountOwner(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer);

	boolean isAccountJointOwner(FundingSource type, String accountId, String name);

	boolean isAccountJointOwner(FundingSource type, String accountId, String world, String name);

	boolean isAccountJointOwner(FundingSource type, String accountId, UUID uuid);

	boolean isAccountJointOwner(FundingSource type, String accountId, String world, UUID uuid);

	boolean isAccountJointOwner(FundingSource type, String accountId, OfflinePlayer offlinePlayer);

	boolean isAccountJointOwner(FundingSource type, String accountId, String world, OfflinePlayer offlinePlayer);

	default boolean accountHas(FundingSource type, String accountId, BigDecimal amount) {
		if (accountExists(type, accountId)) {
			return false;
		}
		return getAccountBalance(type, accountId).compareTo(amount) >= 0;
	}

	default boolean accountHas(FundingSource type, String accountId, String world, BigDecimal amount) {
		if (accountExists(type, accountId, world)) {
			return false;
		}
		return getAccountBalance(type, accountId, world).compareTo(amount) >= 0;
	}

	boolean accountExists(FundingSource type, String accountId);

	boolean accountExists(FundingSource type, String accountId, String world);

	default void depositAccount(FundingSource type, String accountId, BigDecimal amount) {
		if (!accountExists(type, accountId)) {
			return;
		}
		setAccountBalance(type, accountId, getAccountBalance(type, accountId).add(amount));
	}

	default void depositAccount(FundingSource type, String accountId, String world, BigDecimal amount) {
		if (!accountExists(type, accountId, world)) {
			return;
		}
		setAccountBalance(type, accountId, world, getAccountBalance(type, accountId, world).add(amount));
	}

	void setAccountBalance(FundingSource type, String accountId, BigDecimal amount);

	void setAccountBalance(FundingSource type, String accountId, String world, BigDecimal amount);

	default void withdrawAccount(FundingSource type, String accountId, BigDecimal amount) {
		if (!accountExists(type, accountId)) {
			return;
		}
		setAccountBalance(type, accountId, getAccountBalance(type, accountId).subtract(amount));
	}

	default void withdrawAccount(FundingSource type, String accountId, String world, BigDecimal amount) {
		if (!accountExists(type, accountId, world)) {
			return;
		}
		setAccountBalance(type, accountId, world, getAccountBalance(type, accountId, world).subtract(amount));
	}

	BigDecimal getAccountBalance(FundingSource type, String accountId);

	BigDecimal getAccountBalance(FundingSource type, String accountId, String world);

	default void depositWallet(String name, BigDecimal amount) {
		walletSetBalance(name, getWalletBalance(name).add(amount));
	}

	default void depositWallet(String name, String world, BigDecimal amount) {
		walletSetBalance(name, world, getWalletBalance(name, world).add(amount));
	}

	default void withdrawWallet(String name, BigDecimal amount) {
		walletSetBalance(name, getWalletBalance(name).subtract(amount));
	}

	default void withdrawWallet(String name, String world, BigDecimal amount) {
		walletSetBalance(name, world, getWalletBalance(name, world).subtract(amount));
	}

	default void depositWallet(UUID uuid, BigDecimal amount) {
		walletSetBalance(uuid, getWalletBalance(uuid).add(amount));
	}

	default void depositWallet(UUID uuid, String world, BigDecimal amount) {
		walletSetBalance(uuid, world, getWalletBalance(uuid, world).add(amount));
	}

	default void withdrawWallet(UUID uuid, BigDecimal amount) {
		walletSetBalance(uuid, getWalletBalance(uuid).subtract(amount));
	}

	default void withdrawWallet(UUID uuid, String world, BigDecimal amount) {
		walletSetBalance(uuid, world, getWalletBalance(uuid, world).subtract(amount));
	}

	default void depositWallet(OfflinePlayer offlinePlayer, BigDecimal amount) {
		walletSetBalance(offlinePlayer, getWalletBalance(offlinePlayer).add(amount));
	}

	default void depositWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		walletSetBalance(offlinePlayer, world, getWalletBalance(offlinePlayer, world).add(amount));
	}

	default void withdrawWallet(OfflinePlayer offlinePlayer, BigDecimal amount) {
		walletSetBalance(offlinePlayer, getWalletBalance(offlinePlayer).subtract(amount));
	}

	default void withdrawWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		walletSetBalance(offlinePlayer, world, getWalletBalance(offlinePlayer, world).subtract(amount));
	}

	void walletSetBalance(String name, BigDecimal amount);

	void walletSetBalance(String name, String world, BigDecimal amount);

	void walletSetBalance(UUID uuid, BigDecimal amount);

	void walletSetBalance(UUID uuid, String world, BigDecimal amount);

	void walletSetBalance(OfflinePlayer offlinePlayer, BigDecimal amount);

	void walletSetBalance(OfflinePlayer offlinePlayer, String world, BigDecimal amount);

	default boolean walletHas(String name, BigDecimal amount) {
		return getWalletBalance(name).compareTo(amount) >= 0;
	}

	default boolean walletHas(String name, String world, BigDecimal amount) {
		return getWalletBalance(name, world).compareTo(amount) >= 0;
	}

	default boolean walletHas(UUID uuid, BigDecimal amount) {
		/*double current = walletDir.getConfig().getDouble("Index." + uuid.toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");*/
		return getWalletBalance(uuid).compareTo(amount) >= 0;
	}

	default boolean walletHas(UUID uuid, String world, BigDecimal amount) {
		/*double current = walletDir.getConfig().getDouble("Index." + uuid.toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");*/
		return getWalletBalance(uuid, world).compareTo(amount) >= 0;
	}

	default boolean walletHas(OfflinePlayer offlinePlayer, BigDecimal amount) {
		/*double current = walletDir.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + Bukkit.getWorlds().get(0).getName() + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");*/
		return getWalletBalance(offlinePlayer).compareTo(amount) >= 0;
	}

	default boolean walletHas(OfflinePlayer offlinePlayer, String world, BigDecimal amount) {
		/*double current = walletDir.getConfig().getDouble("Index." + offlinePlayer.getUniqueId().toString() + "." + world + ".balance");
		double result = current - amount.doubleValue();
		return !String.valueOf(result).contains("-");*/
		return getWalletBalance(offlinePlayer, world).compareTo(amount) >= 0;
	}

	BigDecimal getWalletBalance(String name);

	BigDecimal getWalletBalance(String name, String world);

	BigDecimal getWalletBalance(UUID uuid);

	BigDecimal getWalletBalance(UUID uuid, String world);

	BigDecimal getWalletBalance(OfflinePlayer offlinePlayer);

	BigDecimal getWalletBalance(OfflinePlayer offlinePlayer, String world);

}

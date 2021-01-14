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

	boolean accountHas(FundingSource type, String accountId, BigDecimal amount);

	boolean accountHas(FundingSource type, String accountId, String world, BigDecimal amount);

	boolean accountExists(FundingSource type, String accountId);

	boolean accountExists(FundingSource type, String accountId, String world);

	void depositAccount(FundingSource type, String accountId, BigDecimal amount);

	void depositAccount(FundingSource type, String accountId, String world, BigDecimal amount);

	void setAccountBalance(FundingSource type, String accountId, BigDecimal amount);

	void setAccountBalance(FundingSource type, String accountId, String world, BigDecimal amount);

	void withdrawAccount(FundingSource type, String accountId, BigDecimal amount);

	void withdrawAccount(FundingSource type, String accountId, String world, BigDecimal amount);

	BigDecimal getAccountBalance(FundingSource type, String accountId);

	BigDecimal getAccountBalance(FundingSource type, String accountId, String world);

	void depositWallet(String name, BigDecimal amount);

	void depositWallet(String name, String world, BigDecimal amount);

	void withdrawWallet(String name, BigDecimal amount);

	void withdrawWallet(String name, String world, BigDecimal amount);

	void depositWallet(UUID uuid, BigDecimal amount);

	void depositWallet(UUID uuid, String world, BigDecimal amount);

	void withdrawWallet(UUID uuid, BigDecimal amount);

	void withdrawWallet(UUID uuid, String world, BigDecimal amount);

	void depositWallet(OfflinePlayer offlinePlayer, BigDecimal amount);

	void depositWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount);

	void withdrawWallet(OfflinePlayer offlinePlayer, BigDecimal amount);

	void withdrawWallet(OfflinePlayer offlinePlayer, String world, BigDecimal amount);

	void walletSetBalance(String name, BigDecimal amount);

	void walletSetBalance(String name, String world, BigDecimal amount);

	void walletSetBalance(UUID uuid, BigDecimal amount);

	void walletSetBalance(UUID uuid, String world, BigDecimal amount);

	void walletSetBalance(OfflinePlayer offlinePlayer, BigDecimal amount);

	void walletSetBalance(OfflinePlayer offlinePlayer, String world, BigDecimal amount);

	boolean walletHas(String name, BigDecimal amount);

	boolean walletHas(String name, String world, BigDecimal amount);

	boolean walletHas(UUID uuid, BigDecimal amount);

	boolean walletHas(UUID uuid, String world, BigDecimal amount);

	boolean walletHas(OfflinePlayer offlinePlayer, BigDecimal amount);

	boolean walletHas(OfflinePlayer offlinePlayer, String world, BigDecimal amount);

	BigDecimal getWalletBalance(String name);

	BigDecimal getWalletBalance(String name, String world);

	BigDecimal getWalletBalance(UUID uuid);

	BigDecimal getWalletBalance(UUID uuid, String world);

	BigDecimal getWalletBalance(OfflinePlayer offlinePlayer);

	BigDecimal getWalletBalance(OfflinePlayer offlinePlayer, String world);

}

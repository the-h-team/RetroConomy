package com.youtube.hempfest.retro.construct.token;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.EconomyPriority;
import com.youtube.hempfest.economy.construct.account.Account;
import com.youtube.hempfest.economy.construct.account.Balance;
import com.youtube.hempfest.economy.construct.account.Wallet;
import com.youtube.hempfest.economy.construct.account.permissive.AccountType;
import com.youtube.hempfest.economy.construct.currency.special.TokenCurrency;
import com.youtube.hempfest.economy.construct.implement.TokenEconomy;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.data.Config;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.*;

public class TokenEconomyImpl implements TokenEconomy {
    private final Plugin plugin;
    private final String mainWorldName;
    private final Map<String, TokenCurrency> currencies = new HashMap<>();
    private final Config wallets;
    private final Config accounts;

    public TokenEconomyImpl(RetroConomy plugin) {
        this.plugin = plugin;
        this.mainWorldName = plugin.getServer().getWorlds().get(0).getName();
        initializeCurrencies(this);
        this.wallets = Config.get("Wallets", "Token");
        this.accounts = Config.get("Accounts", "Token");
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public EconomyPriority getPriority() {
        return EconomyPriority.LOW;
    }

    @Override
    public TokenCurrency getCurrency() {
        return currencies.get(mainWorldName);
    }

    @Override
    public TokenCurrency getCurrency(String s) {
        return currencies.get(s);
    }

    @Override
    public String format(BigDecimal bigDecimal) {
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
    public boolean hasAccount(String s) {
        return getAccount(s).exists();
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return getAccount(s).exists(s1);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return getAccount(offlinePlayer).exists();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return getAccount(offlinePlayer).exists(s);
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return TokenAccount.getTokenAccount(uuid).map(Balance::exists).orElse(false);
    }

    @Override
    public boolean hasAccount(UUID uuid, String s) {
        return TokenAccount.getTokenAccount(uuid).map(account -> account.exists(s)).orElse(false);
    }

    @Override
    public boolean hasWallet(String s) {
        return getWallet(s).exists();
    }

    @Override
    public boolean hasWallet(String s, String s1) {
        return getWallet(s).exists(s1);
    }

    @Override
    public boolean hasWallet(OfflinePlayer offlinePlayer) {
        return getWallet(offlinePlayer).exists();
    }

    @Override
    public boolean hasWallet(OfflinePlayer offlinePlayer, String s) {
        return getWallet(offlinePlayer).exists(s);
    }

    @Override
    public boolean hasWallet(UUID uuid) {
        return TokenWallet.getTokenWallet(uuid).map(Balance::exists).orElse(false);
    }

    @Override
    public boolean hasWallet(UUID uuid, String s) {
        return TokenWallet.getTokenWallet(uuid).map(wallet -> wallet.exists(s)).orElse(false);
    }

    @Override
    public Wallet getWallet(String s) {
        return TokenWallet.getTokenWallet(s);
    }

    @Override
    public Wallet getWallet(OfflinePlayer offlinePlayer) {
        return TokenWallet.getTokenWallet(offlinePlayer);
    }

    @Override
    public Wallet getWallet(UUID uuid) {
        final Optional<Wallet> optionalWallet = TokenWallet.getTokenWallet(uuid);
        if (!optionalWallet.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
        return optionalWallet.get();
    }

    @Override
    public Account getAccount(String s) {
        return TokenAccount.getTokenAccount(s);
    }

    @Override
    public Account getAccount(String s, AccountType accountType) {
        return TokenAccount.getTokenAccount(accountType, s);
    }

    @Override
    public Account getAccount(OfflinePlayer offlinePlayer) {
        return TokenAccount.getTokenAccount(offlinePlayer);
    }

    @Override
    public Account getAccount(OfflinePlayer offlinePlayer, AccountType accountType) {
        return TokenAccount.getTokenAccount(accountType, offlinePlayer);
    }

    @Override
    public Account getAccount(UUID uuid) {
        final Optional<Account> tokenAccount = TokenAccount.getTokenAccount(uuid);
        if (!tokenAccount.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
        return tokenAccount.get();
    }

    @Override
    public Account getAccount(UUID uuid, AccountType accountType) {
        final Optional<Account> tokenAccount = TokenAccount.getTokenAccount(accountType, uuid);
        if (!tokenAccount.isPresent()) throw new IllegalStateException("Invalid Player Uid!");
        return tokenAccount.get();
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, String s) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, String s, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, String s, String s1, String s2) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, String s, String s1, String s2, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, OfflinePlayer offlinePlayer, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, OfflinePlayer offlinePlayer, String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, OfflinePlayer offlinePlayer, String s, String s1, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, UUID uuid, String s) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, UUID uuid, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, UUID uuid, String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction createAccount(AccountType accountType, UUID uuid, String s, String s1, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction deleteWalletAccount(Wallet wallet) {
        return null;
    }

    @Override
    public EconomyAction deleteWalletAccount(Wallet wallet, String s) {
        return null;
    }

    @Override
    public EconomyAction deleteAccount(String s) {
        return null;
    }

    @Override
    public EconomyAction deleteAccount(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction deleteAccount(Account account) {
        return null;
    }

    @Override
    public EconomyAction deleteAccount(Account account, String s) {
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

    private static void initializeCurrencies(final TokenEconomyImpl instance) {
        synchronized (instance) {
            instance.plugin.getServer().getWorlds().stream().map(World::getName).forEach(name -> {
                instance.currencies.computeIfAbsent(name, TokenEconomyImpl::readCurrencyFromConfig);
            });
        }
    }

    private static TokenCurrency readCurrencyFromConfig(String world) {
        return TokenCurrency.getCurrencyLayoutBuilder().toSystem(); // TODO: actually make this
    }
}

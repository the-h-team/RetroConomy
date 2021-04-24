package com.github.sanctum.retro.vault;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroAccount;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultEconomy implements Economy {

	public static void register() {
		Bukkit.getServicesManager().register(Economy.class, new VaultEconomy(), JavaPlugin.getProvidingPlugin(RetroConomy.class), ServicePriority.High);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return "RetroConomy";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double v) {
		return RetroConomy.getInstance().getManager().format(v);
	}

	@Override
	public String currencyNamePlural() {
		return RetroConomy.getInstance().getManager().getMajorPlural();
	}

	@Override
	public String currencyNameSingular() {
		return RetroConomy.getInstance().getManager().getMajorSingular();
	}

	@Override
	public boolean hasAccount(String s) {
		return RetroConomy.getInstance().getManager().getAccount(s).isPresent();
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		return RetroConomy.getInstance().getManager().getAccount(offlinePlayer).isPresent();
	}

	@Override
	@Deprecated
	public boolean hasAccount(String s, String s1) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
		return RetroConomy.getInstance().getManager().getAccount(offlinePlayer).isPresent();
	}

	@Override
	public double getBalance(String s) {
		return RetroConomy.getInstance().getManager().getWallet(s).get().getBalance().doubleValue();
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		return RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().getBalance().doubleValue();
	}

	@Override
	public double getBalance(String s, String s1) {
		return RetroConomy.getInstance().getManager().getWallet(s).get().getBalance(Bukkit.getWorld(s1)).doubleValue();
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String s) {
		return RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().getBalance(Bukkit.getWorld(s)).doubleValue();
	}

	@Override
	public boolean has(String s, double v) {
		if (String.valueOf(v).contains("-"))
			return false;
		return getBalance(s) >= v;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, double v) {
		if (String.valueOf(v).contains("-"))
			return false;
		return getBalance(offlinePlayer) >= v;
	}

	@Override
	public boolean has(String s, String s1, double v) {
		if (String.valueOf(v).contains("-"))
			return false;
		return getBalance(s, s1) >= v;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
		if (String.valueOf(v).contains("-"))
			return false;
		return getBalance(offlinePlayer, s) >= v;
	}

	@Override
	public EconomyResponse withdrawPlayer(String s, double v) {
		if (has(s, v)) {
			RetroConomy.getInstance().getManager().getWallet(s).get().withdraw(BigDecimal.valueOf(v));
			return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, "");
		} else
		return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.FAILURE, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
		if (has(offlinePlayer, v)) {
			RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().withdraw(BigDecimal.valueOf(v));
			return new EconomyResponse(v, getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, "");
		} else
			return new EconomyResponse(v, getBalance(offlinePlayer), EconomyResponse.ResponseType.FAILURE, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(String s, String s1, double v) {
		if (has(s, s1, v)) {
			RetroConomy.getInstance().getManager().getWallet(s).get().withdraw(BigDecimal.valueOf(v), Bukkit.getWorld(s1));
			return new EconomyResponse(v, getBalance(s, s1), EconomyResponse.ResponseType.SUCCESS, "");
		} else
			return new EconomyResponse(v, getBalance(s, s1), EconomyResponse.ResponseType.FAILURE, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		if (has(offlinePlayer, s, v)) {
			RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().withdraw(BigDecimal.valueOf(v), Bukkit.getWorld(s));
			return new EconomyResponse(v, getBalance(offlinePlayer, s), EconomyResponse.ResponseType.SUCCESS, "");
		} else
			return new EconomyResponse(v, getBalance(offlinePlayer, s), EconomyResponse.ResponseType.FAILURE, "");
	}

	@Override
	public EconomyResponse depositPlayer(String s, double v) {
		RetroConomy.getInstance().getManager().getWallet(s).get().deposit(BigDecimal.valueOf(v));
		return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
		RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().deposit(BigDecimal.valueOf(v));
		return new EconomyResponse(v, getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(String s, String s1, double v) {
		RetroConomy.getInstance().getManager().getWallet(s).get().deposit(BigDecimal.valueOf(v), Bukkit.getWorld(s1));
		return new EconomyResponse(v, getBalance(s, s1), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		RetroConomy.getInstance().getManager().getWallet(offlinePlayer).get().deposit(BigDecimal.valueOf(v), Bukkit.getWorld(s));
		return new EconomyResponse(v, getBalance(offlinePlayer, s), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse createBank(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String s) {
		return null;
	}

	@Override
	public EconomyResponse bankBalance(String s) {
		return null;
	}

	@Override
	public EconomyResponse bankHas(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public List<String> getBanks() {
		return RetroConomy.getInstance().getManager().getAccounts().map(RetroAccount::getId).map(HUID::toString).collect(Collectors.toList());
	}

	@Override
	public boolean createPlayerAccount(String s) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String s, String s1) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
		return false;
	}
}

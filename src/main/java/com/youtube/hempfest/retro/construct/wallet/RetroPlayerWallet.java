package com.youtube.hempfest.retro.construct.wallet;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.PlayerWallet;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.UUID;

public class RetroPlayerWallet extends PlayerWallet {
	private transient OfflinePlayer player;

	public RetroPlayerWallet(OfflinePlayer holder) {
		super(holder);
		this.player = holder;
	}

	@Override
	public void setBalance(BigDecimal amount) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().walletSetBalance(holder.friendlyName(), amount);
		} else {
			RetroAPI.getInstance().walletSetBalance(UUID.fromString(holder.id()), amount);
		}
	}

	@Override
	public void setBalance(BigDecimal amount, String world) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().walletSetBalance(holder.friendlyName(), world, amount);
		} else {
			RetroAPI.getInstance().walletSetBalance(UUID.fromString(holder.id()), world, amount);
		}
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean exists(String world) {
		return true;
	}

	@Override
	public BigDecimal getBalance() {
		/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().getWalletBalance(holder.friendlyName());
		} else {
			return RetroAPI.getInstance().getWalletBalance(UUID.fromString(holder.id()));
		}*/
		return RetroAPI.getInstance().getWalletBalance(player);
	}

	@Override
	public BigDecimal getBalance(String world) {
		/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().getWalletBalance(holder.friendlyName(), world);
		} else {
			return RetroAPI.getInstance().getWalletBalance(UUID.fromString(holder.id()), world);
		}*/
		return RetroAPI.getInstance().getWalletBalance(player, world);
	}

	@Override
	public boolean has(BigDecimal amount) {
		/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().walletHas(holder.friendlyName(), amount);
		} else {
			return RetroAPI.getInstance().walletHas(UUID.fromString(holder.id()), amount);
		}*/
		return RetroAPI.getInstance().walletHas(player, amount);
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().walletHas(holder.friendlyName(), world, amount);
		} else {
			return RetroAPI.getInstance().walletHas(UUID.fromString(holder.id()), world, amount);
		}*/
		return RetroAPI.getInstance().walletHas(player, world, amount);
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().depositWallet(holder.friendlyName(), amount);
		} else {
			RetroAPI.getInstance().depositWallet(UUID.fromString(holder.id()), amount);
		}
		// status?
		return new EconomyAction(amount, holder, true, "Deposited " + amount.doubleValue() + " into " + holder.friendlyName() + " wallet.");
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().depositWallet(holder.friendlyName(), world, amount);
		} else {
			RetroAPI.getInstance().depositWallet(UUID.fromString(holder.id()), world, amount);
		}*/
		RetroAPI.getInstance().depositWallet(player, world, amount);
		return new EconomyAction(amount, holder, true, "Deposited " + amount.doubleValue() + " into " + holder.friendlyName() + " wallet.");
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		EconomyAction action;
		if (has(amount)) {
			/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
				RetroAPI.getInstance().withdrawWallet(holder.friendlyName(), amount);
			} else {
				RetroAPI.getInstance().withdrawWallet(UUID.fromString(holder.id()), amount);
			}*/
			RetroAPI.getInstance().withdrawWallet(player, amount);
			action = new EconomyAction(amount, holder, true, "Withdrew amount of " + amount.doubleValue() + " from wallet.");
		} else {
			action = new EconomyAction(amount, holder, false, "Unable to withdraw amount of " + amount.doubleValue() + " from wallet.");
		}
		return action;
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount, String world) {
		EconomyAction action;
		if (has(amount)) {
			/*if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
				RetroAPI.getInstance().withdrawWallet(holder.friendlyName(), world, amount);
			} else {
				RetroAPI.getInstance().withdrawWallet(UUID.fromString(holder.id()), world, amount);
			}*/
			RetroAPI.getInstance().withdrawWallet(player, world, amount);
			action = new EconomyAction(amount, holder, true, "Withdrew amount of " + amount.doubleValue() + " from wallet.");
		} else {
			action = new EconomyAction(amount, holder, false, "Unable to withdraw amount of " + amount.doubleValue() + " from wallet.");
		}
		return action;
	}
}

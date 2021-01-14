package com.youtube.hempfest.retro.construct.wallet;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.Balance;
import com.youtube.hempfest.economy.construct.account.Wallet;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.economy.construct.entity.types.PlayerEntity;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import java.math.BigDecimal;
import java.util.UUID;

public class RetroWallet extends Wallet {

	private final EconomyEntity holder;

	public RetroWallet(EconomyEntity holder) {
		super(holder);
		this.holder = holder;
	}

	public RetroWallet(PlayerEntity holder) {
		super(holder);
		this.holder = holder;
	}

	@Override
	public EconomyEntity getHolder() {
		return super.getHolder();
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
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().getWalletBalance(holder.friendlyName());
		} else {
			return RetroAPI.getInstance().getWalletBalance(UUID.fromString(holder.id()));
		}
	}

	@Override
	public BigDecimal getBalance(String world) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().getWalletBalance(holder.friendlyName(), world);
		} else {
			return RetroAPI.getInstance().getWalletBalance(UUID.fromString(holder.id()), world);
		}
	}

	@Override
	public boolean has(BigDecimal amount) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().walletHas(holder.friendlyName(), amount);
		} else {
			return RetroAPI.getInstance().walletHas(UUID.fromString(holder.id()), amount);
		}
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			return RetroAPI.getInstance().walletHas(holder.friendlyName(), world, amount);
		} else {
			return RetroAPI.getInstance().walletHas(UUID.fromString(holder.id()), world, amount);
		}
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().depositWallet(holder.friendlyName(), amount);
		} else {
			RetroAPI.getInstance().depositWallet(UUID.fromString(holder.id()), amount);
		}
		return new EconomyAction(amount, holder, true, "Deposited " + amount.doubleValue() + " into " + holder.friendlyName() + " wallet.");
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
			RetroAPI.getInstance().depositWallet(holder.friendlyName(), world, amount);
		} else {
			RetroAPI.getInstance().depositWallet(UUID.fromString(holder.id()), world, amount);
		}
		return new EconomyAction(amount, holder, true, "Deposited " + amount.doubleValue() + " into " + holder.friendlyName() + " wallet.");
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		EconomyAction action;
		if (has(amount)) {
			if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
				RetroAPI.getInstance().withdrawWallet(holder.friendlyName(), amount);
			} else {
				RetroAPI.getInstance().withdrawWallet(UUID.fromString(holder.id()), amount);
			}
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
			if (holder.friendlyName().equals(RetroConomy.getInstance().getName())) {
				RetroAPI.getInstance().withdrawWallet(holder.friendlyName(), world, amount);
			} else {
				RetroAPI.getInstance().withdrawWallet(UUID.fromString(holder.id()), world, amount);
			}
			action = new EconomyAction(amount, holder, true, "Withdrew amount of " + amount.doubleValue() + " from wallet.");
		} else {
			action = new EconomyAction(amount, holder, false, "Unable to withdraw amount of " + amount.doubleValue() + " from wallet.");
		}
		return action;
	}
}

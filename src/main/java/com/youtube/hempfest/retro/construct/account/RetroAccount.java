package com.youtube.hempfest.retro.construct.account;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.Account;
import com.youtube.hempfest.economy.construct.account.permissive.AccountType;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.ActionUtil;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.data.Config;
import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public class RetroAccount extends Account {

	// note inherited fields `holder` (from Balance) `members` and `accountType` (from Account)

	private String accountId;

	private Config config;

	public RetroAccount(String accountId, AccountType accountType, EconomyEntity holder, EconomyEntity... members) {
		super(accountType, holder, members);
		switch (accountType) {
			case SERVER_ACCOUNT:
				config = RetroConomy.getInstance().serverDir;
				break;
			case ENTITY_ACCOUNT:
				config = RetroConomy.getInstance().entityDir;
				break;
			case BANK_ACCOUNT:
				config = RetroConomy.getInstance().bankDir;
				break;
		}
		this.accountId = accountId;
	}

	// TODO: did these need overriding or nah?
/*	@Override
	public AccountType getType() {
		return super.getType();
	}

	@Override
	public List<String> getMembers() {
		return super.getMembers();
	}

	@Override
	public EconomyEntity getHolder() {
		return super.getHolder();
	}*/

	@Override
	public EconomyAction isOwner(String name) {
		if (name.equals(RetroConomy.getInstance().getName())) {
			return new EconomyAction(holder, true, "Server account accessed.");
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isOwner(String name, String world) {
		if (name.equals(RetroConomy.getInstance().getName())) {
			return new EconomyAction(holder, true, "Server account accessed.");
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isOwner(OfflinePlayer player) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isOwner(OfflinePlayer player, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isOwner(UUID uuid) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isOwner(UUID uuid, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(String name) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(String name, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(OfflinePlayer player) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(OfflinePlayer player, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(UUID uuid) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isJointOwner(UUID uuid, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isMember(String name) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isMember(String name, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				break;
			case ENTITY_ACCOUNT:
				break;
			case SERVER_ACCOUNT:
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isMember(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (accountType) {
			case BANK_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, player), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, player) ? "The given player has member access" : "The given player has no access.");
				break;
			case ENTITY_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, player), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, player) ? "The given player has member access" : "The given player has no access.");
				break;
			case SERVER_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, player), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, player) ? "The given player has member access" : "The given player has no access.");
				break;
		}
		return action;
	}

	@Override
	public EconomyAction isMember(OfflinePlayer player, String world) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
			case ENTITY_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
			case SERVER_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isMember(UUID uuid) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
			case ENTITY_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
			case SERVER_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction isMember(UUID uuid, String world) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
			case ENTITY_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
			case SERVER_ACCOUNT:
				return new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(String name) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(String name, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(UUID uuid) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction addMember(UUID uuid, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(String name) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(String name, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(UUID uuid) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction removeMember(UUID uuid, String world) {
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public void setBalance(BigDecimal amount) {
		switch (accountType) {

			case BANK_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.BANK_ACCOUNT, accountId, amount);
				break;
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId, amount);
				break;
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.SERVER_ACCOUNT, accountId, amount);
				break;
		}
	}

	@Override
	public void setBalance(BigDecimal amount, String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.BANK_ACCOUNT, accountId, world, amount);
				break;
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
				break;
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().setAccountBalance(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
				break;
		}
	}

	@Override
	public boolean exists() {
		switch (accountType) {
			case BANK_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.BANK_ACCOUNT, accountId);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.ENTITY_ACCOUNT, accountId);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.SERVER_ACCOUNT, accountId);
		}
		return false;
	}

	@Override
	public boolean exists(String world) {
		switch (accountType) {

			case BANK_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.BANK_ACCOUNT, accountId, world);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.ENTITY_ACCOUNT, accountId, world);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().accountExists(FundingSource.SERVER_ACCOUNT, accountId, world);
		}
		return false;
	}

	@Override
	public BigDecimal getBalance() {
		switch (accountType) {
			case BANK_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.BANK_ACCOUNT, accountId);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.SERVER_ACCOUNT, accountId);
		}
		return null;
	}

	@Override
	public BigDecimal getBalance(String world) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.BANK_ACCOUNT, accountId, world);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId, world);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().getAccountBalance(FundingSource.SERVER_ACCOUNT, accountId, world);
		}
		return null;
	}

	@Override
	public boolean has(BigDecimal amount) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.BANK_ACCOUNT, accountId, amount);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.ENTITY_ACCOUNT, accountId, amount);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.SERVER_ACCOUNT, accountId, amount);
		}
		return false;
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		switch (accountType) {
			case BANK_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.BANK_ACCOUNT, accountId, world, amount);
			case ENTITY_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
			case SERVER_ACCOUNT:
				return RetroAPI.getInstance().accountHas(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
		}
		return false;
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		switch (accountType) {
			case BANK_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.BANK_ACCOUNT, accountId, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.ENTITY_ACCOUNT, accountId, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.SERVER_ACCOUNT, accountId, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		switch (accountType) {
			case BANK_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.BANK_ACCOUNT, accountId, world, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
				return new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		RetroAPI api = RetroAPI.getInstance();
		switch (accountType) {

			case BANK_ACCOUNT:
				if (api.accountExists(FundingSource.BANK_ACCOUNT, accountId)) {
						if (api.accountHas(FundingSource.BANK_ACCOUNT, accountId, amount)) {
							api.withdrawAccount(FundingSource.BANK_ACCOUNT, accountId, amount);
							return ActionUtil.withdrewAccount(holder, amount, accountId);
						} else {
							return ActionUtil.notEnoughMoney(holder);
						}
				}
				break;
			case ENTITY_ACCOUNT:
				if (api.accountExists(FundingSource.ENTITY_ACCOUNT, accountId)) {
					if (api.accountHas(FundingSource.ENTITY_ACCOUNT, accountId, amount)) {
						api.withdrawAccount(FundingSource.ENTITY_ACCOUNT, accountId, amount);
						return ActionUtil.withdrewAccount(holder, amount, accountId);
					} else {
						return ActionUtil.notEnoughMoney(holder);
					}
				}
				break;
			case SERVER_ACCOUNT:
				if (api.accountExists(FundingSource.SERVER_ACCOUNT, accountId)) {
					if (api.accountHas(FundingSource.SERVER_ACCOUNT, accountId, amount)) {
						api.withdrawAccount(FundingSource.SERVER_ACCOUNT, accountId, amount);
						return ActionUtil.withdrewAccount(holder, amount, accountId);
					} else {
						return ActionUtil.notEnoughMoney(holder);
					}
				}
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount, String world) {
		RetroAPI api = RetroAPI.getInstance();
		switch (accountType) {

			case BANK_ACCOUNT:
				if (api.accountExists(FundingSource.BANK_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.BANK_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.BANK_ACCOUNT, accountId, world, amount);
						return ActionUtil.withdrewAccount(holder, amount, accountId);
					} else {
						return ActionUtil.notEnoughMoney(holder);
					}
				}
				break;
			case ENTITY_ACCOUNT:
				if (api.accountExists(FundingSource.ENTITY_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.ENTITY_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
						return ActionUtil.withdrewAccount(holder, amount, accountId);
					} else {
						return ActionUtil.notEnoughMoney(holder);
					}
				}
				break;
			case SERVER_ACCOUNT:
				if (api.accountExists(FundingSource.SERVER_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.SERVER_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
						return ActionUtil.withdrewAccount(holder, amount, accountId);
					} else {
						return ActionUtil.notEnoughMoney(holder);
					}
				}
				break;
		}
		return ActionUtil.unsuccessful(holder);
	}
}

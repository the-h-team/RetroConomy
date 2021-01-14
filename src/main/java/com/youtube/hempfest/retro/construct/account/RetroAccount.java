package com.youtube.hempfest.retro.construct.account;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.Account;
import com.youtube.hempfest.economy.construct.account.permissive.AccountType;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.economy.construct.entity.types.PlayerEntity;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.api.RetroAPI;
import com.youtube.hempfest.retro.data.Config;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public class RetroAccount extends Account {

	private final AccountType type;

	private final EconomyEntity holder;

	private PlayerEntity player;

	private String accountId;

	private final List<EconomyEntity> members;

	private Config config;

	public RetroAccount(String accountId, AccountType accountType, EconomyEntity holder, EconomyEntity... members) {
		super(accountType, holder, members);
		this.type = accountType;
		this.holder = holder;
		this.members = Arrays.asList(members);
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

	public RetroAccount(String accountId, AccountType accountType, PlayerEntity holder, EconomyEntity... members) {
		super(accountType, holder, members);
		this.type = accountType;
		this.holder = holder;
		this.player = holder;
		this.members = Arrays.asList(members);
		switch (accountType) {
			case SERVER_ACCOUNT:
				config = RetroConomy.getInstance().serverDir;
				if (accountId == null || accountId.isEmpty()) {
					this.accountId = RetroAPI.getInstance().getAccountID(FundingSource.SERVER_ACCOUNT, UUID.fromString(holder.id()));
				} else {
					this.accountId = accountId;
				}
				break;
			case ENTITY_ACCOUNT:
				config = RetroConomy.getInstance().entityDir;
				if (accountId == null || accountId.isEmpty()) {
					this.accountId = RetroAPI.getInstance().getAccountID(FundingSource.ENTITY_ACCOUNT, UUID.fromString(holder.id()));
				} else {
					this.accountId = accountId;
				}
				break;
			case BANK_ACCOUNT:
				config = RetroConomy.getInstance().bankDir;
				if (accountId == null || accountId.isEmpty()) {
					this.accountId = RetroAPI.getInstance().getAccountID(FundingSource.BANK_ACCOUNT, UUID.fromString(holder.id()));
				} else {
					this.accountId = accountId;
				}
				break;
		}
	}

	@Override
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
	}

	@Override
	public EconomyAction isOwner(String name) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		if (name.equals(RetroConomy.getInstance().getName())) {
			action = new EconomyAction(holder, true, "Server account accessed.");
		}
		return action;
	}

	@Override
	public EconomyAction isOwner(String name, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
			if (name.equals(RetroConomy.getInstance().getName())) {
				action = new EconomyAction(holder, true, "Server account accessed.");
			}
		return action;
	}

	@Override
	public EconomyAction isOwner(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isOwner(OfflinePlayer player, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isOwner(UUID uuid) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isOwner(UUID uuid, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(String name) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(String name, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(OfflinePlayer player, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(UUID uuid) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isJointOwner(UUID uuid, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isMember(String name) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isMember(String name, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
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
	public EconomyAction isMember(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
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
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
			case BANK_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
				break;
			case ENTITY_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
				break;
			case SERVER_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, player), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, player) ? "The given player has member access" : "The given player has no access.");
				break;
		}
		return action;
	}

	@Override
	public EconomyAction isMember(UUID uuid) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
			case BANK_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
			case ENTITY_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
			case SERVER_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
		}
		return action;
	}

	@Override
	public EconomyAction isMember(UUID uuid, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
			case BANK_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.BANK_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
			case ENTITY_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.ENTITY_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
			case SERVER_ACCOUNT:
				action = new EconomyAction(holder, RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, uuid), RetroAPI.getInstance().isAccountMember(FundingSource.SERVER_ACCOUNT, accountId, world, uuid) ? "The given holder has member access" : "The given holder has no access.");
				break;
		}
		return action;
	}

	@Override
	public EconomyAction addMember(String name) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction addMember(String name, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction addMember(OfflinePlayer player, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction addMember(UUID uuid) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction addMember(UUID uuid, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(String name) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(String name, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(OfflinePlayer player, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(UUID uuid) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public EconomyAction removeMember(UUID uuid, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		return null;
	}

	@Override
	public void setBalance(BigDecimal amount) {
		switch (type) {

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
		switch (type) {

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
		boolean result = false;
		switch (type) {
			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.BANK_ACCOUNT, accountId);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.ENTITY_ACCOUNT, accountId);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.SERVER_ACCOUNT, accountId);
				break;
		}
		return result;
	}

	@Override
	public boolean exists(String world) {
		boolean result = false;
		switch (type) {

			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.BANK_ACCOUNT, accountId, world);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.ENTITY_ACCOUNT, accountId, world);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().accountExists(FundingSource.SERVER_ACCOUNT, accountId, world);
				break;
		}
		return result;
	}

	@Override
	public BigDecimal getBalance() {
		BigDecimal result = null;
		switch (type) {
			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.BANK_ACCOUNT, accountId);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.SERVER_ACCOUNT, accountId);
				break;
		}
		return result;
	}

	@Override
	public BigDecimal getBalance(String world) {
		BigDecimal result = null;
		switch (type) {
			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.BANK_ACCOUNT, accountId, world);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.ENTITY_ACCOUNT, accountId, world);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().getAccountBalance(FundingSource.SERVER_ACCOUNT, accountId, world);
				break;
		}
		return result;
	}

	@Override
	public boolean has(BigDecimal amount) {
		boolean result = false;
		switch (type) {
			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.BANK_ACCOUNT, accountId, amount);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.ENTITY_ACCOUNT, accountId, amount);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.SERVER_ACCOUNT, accountId, amount);
				break;
		}
		return result;
	}

	@Override
	public boolean has(BigDecimal amount, String world) {
		boolean result = false;
		switch (type) {
			case BANK_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.BANK_ACCOUNT, accountId, world, amount);
				break;
			case ENTITY_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
				break;
			case SERVER_ACCOUNT:
				result = RetroAPI.getInstance().accountHas(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
				break;
		}
		return result;
	}

	@Override
	public EconomyAction deposit(BigDecimal amount) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
			case BANK_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.BANK_ACCOUNT, accountId, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.ENTITY_ACCOUNT, accountId, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.SERVER_ACCOUNT, accountId, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
		}
		return action;
	}

	@Override
	public EconomyAction deposit(BigDecimal amount, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		switch (type) {
			case BANK_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.BANK_ACCOUNT, accountId, world, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
			case ENTITY_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
			case SERVER_ACCOUNT:
				RetroAPI.getInstance().depositAccount(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
				action = new EconomyAction(amount, holder, true, "Successfully updated " + holder.friendlyName() + " balance to " + amount.doubleValue());
				break;
		}
		return action;
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				if (api.accountExists(FundingSource.BANK_ACCOUNT, accountId)) {
						if (api.accountHas(FundingSource.BANK_ACCOUNT, accountId, amount)) {
							api.withdrawAccount(FundingSource.BANK_ACCOUNT, accountId, amount);
							action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
						} else {
							action = new EconomyAction(holder, false, "Not enough money.");
						}
				}
				break;
			case ENTITY_ACCOUNT:
				if (api.accountExists(FundingSource.ENTITY_ACCOUNT, accountId)) {
					if (api.accountHas(FundingSource.ENTITY_ACCOUNT, accountId, amount)) {
						api.withdrawAccount(FundingSource.ENTITY_ACCOUNT, accountId, amount);
						action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
					} else {
						action = new EconomyAction(holder, false, "Not enough money.");
					}
				}
				break;
			case SERVER_ACCOUNT:
				if (api.accountExists(FundingSource.SERVER_ACCOUNT, accountId)) {
					if (api.accountHas(FundingSource.SERVER_ACCOUNT, accountId, amount)) {
						api.withdrawAccount(FundingSource.SERVER_ACCOUNT, accountId, amount);
						action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
					} else {
						action = new EconomyAction(holder, false, "Not enough money.");
					}
				}
				break;
		}
		return action;
	}

	@Override
	public EconomyAction withdraw(BigDecimal amount, String world) {
		EconomyAction action = new EconomyAction(holder, false, "Something went wrong. Information unavailable.");
		RetroAPI api = RetroAPI.getInstance();
		switch (type) {

			case BANK_ACCOUNT:
				if (api.accountExists(FundingSource.BANK_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.BANK_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.BANK_ACCOUNT, accountId, world, amount);
						action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
					} else {
						action = new EconomyAction(holder, false, "Not enough money.");
					}
				}
				break;
			case ENTITY_ACCOUNT:
				if (api.accountExists(FundingSource.ENTITY_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.ENTITY_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.ENTITY_ACCOUNT, accountId, world, amount);
						action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
					} else {
						action = new EconomyAction(holder, false, "Not enough money.");
					}
				}
				break;
			case SERVER_ACCOUNT:
				if (api.accountExists(FundingSource.SERVER_ACCOUNT, accountId, world)) {
					if (api.accountHas(FundingSource.SERVER_ACCOUNT, accountId, world, amount)) {
						api.withdrawAccount(FundingSource.SERVER_ACCOUNT, accountId, world, amount);
						action = new EconomyAction(holder, true, "Withdrew " + amount.doubleValue() + " from account " + accountId);
					} else {
						action = new EconomyAction(holder, false, "Not enough money.");
					}
				}
				break;
		}
		return action;
	}
}

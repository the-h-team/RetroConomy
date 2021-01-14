package com.youtube.hempfest.retro.construct.account;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.PlayerAccount;
import com.youtube.hempfest.economy.construct.account.permissive.AccountType;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.UUID;

public class RetroPlayerAccount extends PlayerAccount {
    private String accountId;
    public final UUID playerUid;

    public RetroPlayerAccount(String accountId, AccountType accountType, OfflinePlayer holder, EconomyEntity... members) {
        super(accountType, holder, members);
        this.accountId = accountId;
        this.playerUid = holder.getUniqueId();
    }

    /* TODO: Convert this logic to work in this object

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

     */

    @Override
    public EconomyAction isOwner(String s) {
        return null;
    }

    @Override
    public EconomyAction isOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction isOwner(OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction isOwner(OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction isOwner(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isOwner(UUID uuid, String s) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(String s) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(UUID uuid, String s) {
        return null;
    }

    @Override
    public EconomyAction isMember(String s) {
        return null;
    }

    @Override
    public EconomyAction isMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction isMember(OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction isMember(OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction isMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isMember(UUID uuid, String s) {
        return null;
    }

    @Override
    public EconomyAction addMember(String s) {
        return null;
    }

    @Override
    public EconomyAction addMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction addMember(OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction addMember(OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction addMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction addMember(UUID uuid, String s) {
        return null;
    }

    @Override
    public EconomyAction removeMember(String s) {
        return null;
    }

    @Override
    public EconomyAction removeMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyAction removeMember(OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyAction removeMember(OfflinePlayer offlinePlayer, String s) {
        return null;
    }

    @Override
    public EconomyAction removeMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction removeMember(UUID uuid, String s) {
        return null;
    }

    @Override
    public void setBalance(BigDecimal bigDecimal) {

    }

    @Override
    public void setBalance(BigDecimal bigDecimal, String s) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean exists(String s) {
        return false;
    }

    @Override
    public BigDecimal getBalance() {
        return null;
    }

    @Override
    public BigDecimal getBalance(String s) {
        return null;
    }

    @Override
    public boolean has(BigDecimal bigDecimal) {
        return false;
    }

    @Override
    public boolean has(BigDecimal bigDecimal, String s) {
        return false;
    }

    @Override
    public EconomyAction withdraw(BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction withdraw(BigDecimal bigDecimal, String s) {
        return null;
    }

    @Override
    public EconomyAction deposit(BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public EconomyAction deposit(BigDecimal bigDecimal, String s) {
        return null;
    }
}

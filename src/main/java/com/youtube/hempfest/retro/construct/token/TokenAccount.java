package com.youtube.hempfest.retro.construct.token;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.account.Account;
import com.github.sanctum.economy.construct.account.permissive.AccountType;
import com.github.sanctum.economy.construct.entity.EconomyEntity;
import com.github.sanctum.economy.construct.entity.types.PlayerEntity;
import com.youtube.hempfest.retro.construct.entity.ServerEntity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TokenAccount extends Account {
    private TokenAccount(AccountType accountType, EconomyEntity holder, EconomyEntity... members) {
        super(accountType, holder, members);
    }

    @Override
    public EconomyAction isOwner(String name) {
        return null;
    }

    @Override
    public EconomyAction isOwner(String name, String world) {
        return null;
    }

    @Override
    public EconomyAction isOwner(OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyAction isOwner(OfflinePlayer player, String world) {
        return null;
    }

    @Override
    public EconomyAction isOwner(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isOwner(UUID uuid, String world) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(String name) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(String name, String world) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(OfflinePlayer player, String world) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isJointOwner(UUID uuid, String world) {
        return null;
    }

    @Override
    public EconomyAction isMember(String name) {
        return null;
    }

    @Override
    public EconomyAction isMember(String name, String world) {
        return null;
    }

    @Override
    public EconomyAction isMember(OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyAction isMember(OfflinePlayer player, String world) {
        return null;
    }

    @Override
    public EconomyAction isMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction isMember(UUID uuid, String world) {
        return null;
    }

    @Override
    public EconomyAction addMember(String name) {
        return null;
    }

    @Override
    public EconomyAction addMember(String name, String world) {
        return null;
    }

    @Override
    public EconomyAction addMember(OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyAction addMember(OfflinePlayer player, String world) {
        return null;
    }

    @Override
    public EconomyAction addMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction addMember(UUID uuid, String world) {
        return null;
    }

    @Override
    public EconomyAction removeMember(String name) {
        return null;
    }

    @Override
    public EconomyAction removeMember(String name, String world) {
        return null;
    }

    @Override
    public EconomyAction removeMember(OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyAction removeMember(OfflinePlayer player, String world) {
        return null;
    }

    @Override
    public EconomyAction removeMember(UUID uuid) {
        return null;
    }

    @Override
    public EconomyAction removeMember(UUID uuid, String world) {
        return null;
    }

    @Override
    public void setBalance(BigDecimal amount) {

    }

    @Override
    public void setBalance(BigDecimal amount, String world) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean exists(String world) {
        return false;
    }

    @Override
    public @Nullable BigDecimal getBalance() {
        return null;
    }

    @Override
    public @Nullable BigDecimal getBalance(String world) {
        return null;
    }

    @Override
    public boolean has(BigDecimal amount) {
        return false;
    }

    @Override
    public boolean has(BigDecimal amount, String world) {
        return false;
    }

    @Override
    public EconomyAction withdraw(BigDecimal amount) {
        return null;
    }

    @Override
    public EconomyAction withdraw(BigDecimal amount, String world) {
        return null;
    }

    @Override
    public EconomyAction deposit(BigDecimal amount) {
        return null;
    }

    @Override
    public EconomyAction deposit(BigDecimal amount, String world) {
        return null;
    }

    public static Account getTokenAccount(String holder) {
        return getTokenAccount(AccountType.BANK_ACCOUNT, holder);
    }
    public static Account getTokenAccount(AccountType accountType, String holder) {
        return new TokenAccount(accountType, new ServerEntity(holder));
    }
    public static Account getTokenAccount(OfflinePlayer offlinePlayer) {
        return getTokenAccount(AccountType.BANK_ACCOUNT, offlinePlayer);
    }
    public static Account getTokenAccount(AccountType accountType, OfflinePlayer offlinePlayer) {
        return new TokenAccount(accountType, new PlayerEntity(offlinePlayer));
    }
    public static Optional<Account> getTokenAccount(UUID uuid) {
        return getOfflinePlayerByUUID(uuid).map(PlayerEntity::new).map(playerEntity -> new TokenAccount(AccountType.BANK_ACCOUNT, playerEntity));
    }
    public static Optional<Account> getTokenAccount(AccountType accountType, UUID uuid) {
        return getOfflinePlayerByUUID(uuid).map(PlayerEntity::new).map(playerEntity -> new TokenAccount(accountType, playerEntity));
    }

    private static Optional<OfflinePlayer> getOfflinePlayerByUUID(UUID uuid) {
        final OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        return Optional.ofNullable(CompletableFuture.supplyAsync(() -> {
            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                if (offlinePlayer.getUniqueId().equals(uuid)) return offlinePlayer;
            }
            return null;
        }).join());
    }
}

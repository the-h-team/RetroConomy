package com.youtube.hempfest.retro.construct.token;

import com.youtube.hempfest.economy.construct.EconomyAction;
import com.youtube.hempfest.economy.construct.account.Wallet;
import com.youtube.hempfest.economy.construct.entity.EconomyEntity;
import com.youtube.hempfest.economy.construct.entity.types.PlayerEntity;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.construct.ActionUtil;
import com.youtube.hempfest.retro.construct.entity.ServerEntity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TokenWallet extends Wallet {
    private TokenWallet(EconomyEntity holder) {
        super(holder);
    }

    @Override
    public void setBalance(BigDecimal amount) {
        setBalance(amount, RetroConomy.getTokenEconomy().mainWorldName);
    }

    @Override
    public void setBalance(BigDecimal amount, String world) {
        final FileConfiguration config = RetroConomy.getTokenEconomy().wallets.getConfig();
        ConfigurationSection wallets = config.getConfigurationSection("Wallets");
        if (wallets == null) {
            wallets = config.createSection("Wallets");
        }
        final String id = holder.id();
        wallets.set(id + "." + world, amount.toString());
        new BukkitRunnable() {
            @Override
            public void run() {
                RetroConomy.getTokenEconomy().wallets.saveConfig();
            }
        }.runTask(RetroConomy.getInstance()); // just schedule sync task, file io already async
    }

    @Override
    public boolean exists() {
        final FileConfiguration config = RetroConomy.getTokenEconomy().wallets.getConfig();
        final ConfigurationSection wallets = config.getConfigurationSection("Wallets");
        if (wallets != null) {
            final String id = holder.id();
            for (String key : wallets.getKeys(false)) {
                if (key.equals(id)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean exists(String world) {
        final FileConfiguration config = RetroConomy.getTokenEconomy().wallets.getConfig();
        final ConfigurationSection wallets = config.getConfigurationSection("Wallets");
        if (wallets != null) {
            final String id = holder.id();
            for (String key : wallets.getKeys(false)) {
                if (key.equals(id)) {
                    final ConfigurationSection holderSection = config.getConfigurationSection("Wallets." + key);
                    if (holderSection != null) {
                        return holderSection.get(world) != null;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable BigDecimal getBalance() {
        return getBalance(RetroConomy.getTokenEconomy().mainWorldName);
    }

    @Override
    public @Nullable BigDecimal getBalance(String world) {
        final FileConfiguration config = RetroConomy.getTokenEconomy().wallets.getConfig();
        final ConfigurationSection wallets = config.getConfigurationSection("Wallets");
        if (wallets != null) {
            final String id = holder.id();
            for (String key : wallets.getKeys(false)) {
                if (key.equals(id)) {
                    final ConfigurationSection holderSection = config.getConfigurationSection("Wallets." + key);
                    if (holderSection != null) {
                        final String string = holderSection.getString(world);
                        try {
                            return (string != null) ? new BigDecimal(string) : null;
                        } catch (NumberFormatException e) {
                            System.out.println("Unable to parse token wallet balance for " + id + " in world " + world);;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean has(BigDecimal amount) {
        return Optional.ofNullable(getBalance()).map(balance -> balance.compareTo(amount) >= 0).orElse(false);
    }

    @Override
    public boolean has(BigDecimal amount, String world) {
        return Optional.ofNullable(getBalance(world)).map(balance -> balance.compareTo(amount) >= 0).orElse(false);
    }

    @Override
    public EconomyAction withdraw(BigDecimal amount) {
        final BigDecimal balance = getBalance();
        return Optional.ofNullable(balance).map(bigDecimal -> bigDecimal.compareTo(amount) >= 0).map(has -> {
            if (has) {
                setBalance(balance.subtract(amount));
                return ActionUtil.withdrewWallet(holder, amount);
            }
            return ActionUtil.notEnoughMoney(holder);
        }).orElse(ActionUtil.unsuccessful(holder));
    }

    @Override
    public EconomyAction withdraw(BigDecimal amount, String world) {
        final BigDecimal worldBalance = getBalance(world);
        return Optional.ofNullable(worldBalance).map(bigDecimal -> bigDecimal.compareTo(amount) >= 0).map(has -> {
            if (has) {
                setBalance(worldBalance.subtract(amount), world);
                return ActionUtil.withdrewWallet(holder, amount, world);
            }
            return ActionUtil.notEnoughMoney(holder);
        }).orElse(ActionUtil.unsuccessful(holder));
    }

    @Override
    public EconomyAction deposit(BigDecimal amount) {
        final BigDecimal balance = getBalance();
        return Optional.ofNullable(balance).map(original -> {
            setBalance(original.add(amount));
            return ActionUtil.depositedWallet(holder, amount);
        }).orElse(ActionUtil.unsuccessful(holder));
    }

    @Override
    public EconomyAction deposit(BigDecimal amount, String world) {
        final BigDecimal worldBalance = getBalance(world);
        return Optional.ofNullable(worldBalance).map(original -> {
            setBalance(original.add(amount), world);
            return ActionUtil.depositedWallet(holder, amount, world);
        }).orElse(ActionUtil.unsuccessful(holder));
    }

    public static Wallet getTokenWallet(String s) {
        return new TokenWallet(new ServerEntity(s));
    }
    public static Wallet getTokenWallet(OfflinePlayer offlinePlayer) {
        return new TokenWallet(new PlayerEntity(offlinePlayer));
    }
    public static Optional<Wallet> getTokenWallet(UUID uuid) {
        return getOfflinePlayerByUUID(uuid).map(PlayerEntity::new).map(TokenWallet::new);
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

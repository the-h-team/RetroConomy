package com.youtube.hempfest.retro.construct.wallet;

import com.github.sanctum.economy.construct.EconomyAction;
import com.github.sanctum.economy.construct.account.Wallet;
import com.youtube.hempfest.retro.construct.entity.ServerEntity;

import java.math.BigDecimal;

public class RetroServerWallet extends Wallet {
    public RetroServerWallet(ServerEntity holder) {
        super(holder);
    }

    @Override
    public ServerEntity getHolder() {
        return (ServerEntity) holder;
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

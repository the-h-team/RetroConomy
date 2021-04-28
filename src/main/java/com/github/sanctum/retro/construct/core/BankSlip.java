/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class BankSlip implements Savable {

	private static final long serialVersionUID = -6982006703415280538L;
	private final OfflinePlayer holder;
	private final BigDecimal amount;
	private BigDecimal tax;
	private transient final BankAccount account;
	private final TransactionType type;
	private final HUID slipId;

	protected BankSlip(OfflinePlayer holder, BigDecimal amount, BankAccount account, TransactionType type) {
		this.holder = holder;
		this.amount = amount;
		this.type = type;
		this.account = account;
		this.slipId = HUID.randomID();

	}

	public static BankSlip from(OfflinePlayer holder, BigDecimal amount, BankAccount account, TransactionType type) {
		return new BankSlip(holder, amount, account, type);
	}

	public static BankSlip from(OfflinePlayer holder, BigDecimal amount, BigDecimal tax, BankAccount account, TransactionType type) {
		return new BankSlip(holder, amount, account, type).setTax(tax);
	}

	public BigDecimal getTax() {
		return tax;
	}

	public BankSlip setTax(BigDecimal tax) {
		this.tax = tax;
		return this;
	}

	public OfflinePlayer getHolder() {
		return holder;
	}

	public @Nullable BankAccount getAccount() {
		return account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public TransactionType getType() {
		return type;
	}

	@Override
	public ItemStack toItem() {
		ItemStack make = new ItemStack(Material.PAPER);
		ItemMeta meta = make.getItemMeta();
		meta.setDisplayName(StringUtils.use("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ &b[NOTE] &6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").translate());
		if (getAmount().doubleValue() == 0) {
			meta.setLore(Arrays.asList(StringUtils.use("&bHolder: &7" + holder.getName()).translate(),
					StringUtils.use("&b#: &7" + account.getId().toString()).translate(),
					StringUtils.use("&bAmount: &c" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate(),
					StringUtils.use("&bTax: &c" + RetroConomy.getInstance().getManager().format(tax.doubleValue())).translate(),
					StringUtils.use("&bDate: &7" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()).translate(),
					StringUtils.use("&c&oTransaction failed.").translate()));
		} else {
			meta.setLore(Arrays.asList(StringUtils.use("&bHolder: &7" + holder.getName()).translate(),
					this.type == TransactionType.DEPOSIT ? StringUtils.use("&bAmount: &a+&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate() : StringUtils.use("&bAmount: &4-&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate(),
					StringUtils.use("&bTax: &c" + RetroConomy.getInstance().getManager().format(tax.doubleValue())).translate(),
					StringUtils.use("&b#: &7" + account.getId().toString()).translate(),
					StringUtils.use("&bDate: &7" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()).translate()));
		}
		make.setItemMeta(meta);
		return make;
	}

	public HUID slipId() {
		return slipId;
	}

	@Override
	public HUID id() {
		return account.getId();
	}
}

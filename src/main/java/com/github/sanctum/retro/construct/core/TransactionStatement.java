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

public class TransactionStatement implements Savable {

	private static final long serialVersionUID = -6982006703415280538L;
	private final OfflinePlayer holder;
	private final BigDecimal amount;
	private BigDecimal tax = BigDecimal.ZERO;
	private final TransactionType type;
	private final HUID slipId;
	private final HUID id;

	protected TransactionStatement(OfflinePlayer holder, BigDecimal amount, RetroAccount account, TransactionType type) {
		this.holder = holder;
		this.amount = amount;
		this.type = type;
		this.id = account.getId();
		this.slipId = HUID.randomID();
	}

	public static TransactionStatement from(OfflinePlayer holder, BigDecimal amount, RetroAccount account, TransactionType type) {
		return new TransactionStatement(holder, amount, account, type);
	}

	public static TransactionStatement from(OfflinePlayer holder, BigDecimal amount, BigDecimal tax, RetroAccount account, TransactionType type) {
		return new TransactionStatement(holder, amount, account, type).setTax(tax);
	}

	public BigDecimal getTax() {
		return tax;
	}

	public TransactionStatement setTax(BigDecimal tax) {
		this.tax = tax;
		return this;
	}

	public OfflinePlayer getHolder() {
		return holder;
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
		meta.setDisplayName(StringUtils.use("&6&l&m◄▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬‡&b [Receipt] &6&l&m‡▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬►").translate());
		if (getAmount().doubleValue() == 0) {
			meta.setLore(Arrays.asList(StringUtils.use("&bHolder &8&m»&r &7" + holder.getName()).translate(),
					StringUtils.use("&b# &8&m»&r &7" + id.toString()).translate(),
					StringUtils.use("&bAmount &8&m»&r &c" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate(),
					StringUtils.use("&bTax &8&m»&r &c" + RetroConomy.getInstance().getManager().format(tax.doubleValue())).translate(),
					StringUtils.use("&bDate &8&m»&r &7" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()).translate(),
					StringUtils.use("&c&oTransaction failed.").translate()));
		} else {
			meta.setLore(Arrays.asList(StringUtils.use("&bHolder &8&m»&r &7" + holder.getName()).translate(),
					this.type == TransactionType.DEPOSIT ? StringUtils.use("&bAmount &8&m»&r &a+&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate() : StringUtils.use("&bAmount &8&m»&r &4-&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate(),
					StringUtils.use("&bTax &8&m»&r &c" + RetroConomy.getInstance().getManager().format(tax.doubleValue())).translate(),
					StringUtils.use("&b# &8&m»&r &7" + id.toString()).translate(),
					StringUtils.use("&bDate &8&m»&r &7" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()).translate()));
		}
		make.setItemMeta(meta);
		return make;
	}

	public HUID slipId() {
		return slipId;
	}

	@Override
	public HUID id() {
		return id;
	}
}

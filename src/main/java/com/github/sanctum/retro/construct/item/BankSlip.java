package com.github.sanctum.retro.construct.item;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.util.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class BankSlip implements Serializable {

	private final OfflinePlayer holder;
	private final BigDecimal amount;
	private BigDecimal tax;
	private final RetroAccount account;
	private final TransactionType type;

	protected BankSlip(OfflinePlayer holder, BigDecimal amount, RetroAccount account, TransactionType type) {
		this.holder = holder;
		this.amount = amount;
		this.type = type;
		this.account = account;
	}

	public static BankSlip from(OfflinePlayer holder, BigDecimal amount, RetroAccount account, TransactionType type) {
		return new BankSlip(holder, amount, account, type);
	}

	public static BankSlip from(OfflinePlayer holder, BigDecimal amount, BigDecimal tax, RetroAccount account, TransactionType type) {
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

	public @Nullable RetroAccount getAccount() {
		return account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public TransactionType getType() {
		return type;
	}

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


}

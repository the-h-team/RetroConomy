package com.github.sanctum.retro.construct.item;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroAccount;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BankSlip {

	private final OfflinePlayer holder;
	private final BigDecimal amount;
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

	public TransactionType getType() {
		return type;
	}

	public ItemStack toItem() {
		ItemStack make = new ItemStack(Material.PAPER);
		ItemMeta meta = make.getItemMeta();
		meta.setDisplayName(StringUtils.use("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ &b[NOTE] &6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").translate());
		meta.setLore(Arrays.asList(StringUtils.use("&bHolder: &7" + holder.getName()).translate(),
				this.type == TransactionType.DEPOSIT ? StringUtils.use("&bAmount: &a+&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate() : StringUtils.use("&bAmount: &4-&7" + RetroConomy.getInstance().getManager().format(amount.doubleValue())).translate(),
				StringUtils.use("&bSku#: &7" + account.getId().toString()).translate(),
				StringUtils.use("&bDate: &7" + new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()).translate()));
		make.setItemMeta(meta);
		return make;
	}


}

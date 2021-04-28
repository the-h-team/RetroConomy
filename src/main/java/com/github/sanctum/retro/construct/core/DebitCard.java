package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DebitCard implements Savable {

	public static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "retro_debit_card");
	private static final long serialVersionUID = 2707453733366587226L;
	private final BankAccount account;

	public DebitCard(BankAccount account) {
		this.account = account;
	}

	public static boolean matches(ItemStack itemStack) {
		return itemStack.hasItemMeta() && !itemStack.getItemMeta().getPersistentDataContainer().isEmpty() && itemStack.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.STRING);
	}

	@Override
	public ItemStack toItem() {
		ItemStack item = new ItemStack(SkullItem.Head.find(account.getOwner()));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.use("&7[&6Debit&7]").translate());
		meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, id().toString());
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public HUID id() {
		return account.getId();
	}
}

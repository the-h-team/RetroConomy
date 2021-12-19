package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.Savable;
import com.github.sanctum.skulls.CustomHead;
import org.bukkit.Material;
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
		if (itemStack == null) return false;
		return itemStack.hasItemMeta() && !itemStack.getItemMeta().getPersistentDataContainer().isEmpty() && itemStack.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.STRING);
	}

	@Override
	public ItemStack toItem() {
		ItemStack copy = CustomHead.Manager.get(account.getOwner());
		if (copy == null) {
			copy = new ItemStack(Material.PLAYER_HEAD);
		}
		ItemStack item = new ItemStack(copy);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.use("&7[&6Debit&7] &6" + account.getId().toString()).translate());
		meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, id().toString());
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public HUID id() {
		return account.getId();
	}
}

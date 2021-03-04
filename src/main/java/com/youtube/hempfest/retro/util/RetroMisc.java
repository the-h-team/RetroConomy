package com.youtube.hempfest.retro.util;

import com.github.ms5984.lib.menuman.Menu;
import com.github.ms5984.lib.menuman.MenuBuilder;
import com.github.ms5984.lib.menuman.MenuClick;
import com.github.ms5984.lib.menuman.MenuClose;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.youtube.hempfest.retro.RetroConomy;
import com.youtube.hempfest.retro.data.Config;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RetroMisc {

	private static final Config options = Config.getOptions();

	private static final Message msg = new Message("&f[&6RetroConomy&f]");

	private static String color(String text) {
		return StringUtils.translate(text);
	}

	public static String format(String text) {
		return text.replace("{MAJOR}", Coin.majorSingular())
				.replace("{MAJORS}", Coin.majorPlural())
				.replace("{MINOR}", Coin.minorSingular())
				.replace("{MINORS}", Coin.minorPlural());
	}

	public static String format(Player p, String text) {
		return format(text).replace("{BALANCE}", RetroConomy.getInstance().getWalletBalance(p, p.getWorld().getName()) + "");
	}

	public static String format(Player p, String text, double amount) {
		String newAmount = NumberFormat.getNumberInstance(Coin.getLocale()).format(BigDecimal.valueOf(amount).doubleValue());
		return format(p, text).replace("{AMOUNT}", newAmount + "")
				.replace("{AMOUNT_MAJOR}", newAmount.split("\\.")[0])
				.replace("{AMOUNT_MINOR}", newAmount.split("\\.")[1]);
	}

	public static Menu getMain(OfflinePlayer viewer) {
		MenuBuilder builder = new MenuBuilder(Menu.InventoryRows.THREE, color("&3&l▬▬▬▬▬▬▬▬&7[&6&lEconomy Menu&7]&3&l▬▬▬▬▬▬▬▬"), Collections.singletonList(new ItemStack(Material.AIR)).toArray(new ItemStack[0]));
		return fillBorder(builder, 27)
				.addElement(SkullItem.Head.find(viewer.getUniqueId()))
				.setText(color("&7[&eWallet&7]"))
				.setLore(color("&fUser: &6" + viewer.getName()))
				.setAction(RetroMisc::walletClick)
				.assignToSlots(14)
				.allowLowerShiftClicks(false)
				.cancelLowerInventoryClicks(false)
				.setCloseAction(RetroMisc::mainClose)
				.create(RetroConomy.getInstance());
	}

	private static MenuBuilder fillBorder(MenuBuilder builder, int size) {
		for (int i = 0; i < size; i++) {
			builder.addElement(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
					.setText(" ")
					.setAction(MenuClick::disallowClick)
					.assignToSlots(i);
		}
		return builder;
	}

	public static Menu getWallet(OfflinePlayer player) {
		MenuBuilder builder = new MenuBuilder(Menu.InventoryRows.THREE, color("&3&l▬▬▬▬▬▬▬▬&7[&6&lPlayer Wallet&7]&3&l▬▬▬▬▬▬▬▬"), Collections.singletonList(new ItemStack(Material.AIR)).toArray(new ItemStack[0]));
		fillBorder(builder, 27);
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 1)).setText(color("&cWithdraw &f{&e1&f} " + Coin.majorSingular())).setAction(RetroMisc::withdraw1).assignToSlots(10);
		// 2
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 32)).setText(color("&cWithdraw &f{&e32&f} " + Coin.majorSingular())).setAction(RetroMisc::withdraw2).assignToSlots(11);
		// 3
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 64)).setText(color("&cWithdraw &f{&e64&f} " + Coin.majorSingular())).setAction(RetroMisc::withdraw3).assignToSlots(12);

		// 1
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 1)).setText(color("&cWithdraw &f{&e1&f} " + Coin.minorSingular())).setAction(RetroMisc::withdraw1M).assignToSlots(19);
		// 2
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 32)).setText(color("&cWithdraw &f{&e32&f} " + Coin.minorSingular())).setAction(RetroMisc::withdraw2M).assignToSlots(20);
		// 3
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 64)).setText(color("&cWithdraw &f{&e64&f} " + Coin.minorSingular())).setAction(RetroMisc::withdraw3M).assignToSlots(21);

		// 1
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 1)).setText(color("&aDeposit &f{&e1&f} " + Coin.majorSingular())).setAction(RetroMisc::deposit1).assignToSlots(14);
		// 2
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 32)).setText(color("&aDeposit &f{&e32&f} " + Coin.majorSingular())).setAction(RetroMisc::deposit2).assignToSlots(15);
		// 3
		builder.addElement(new ItemStack(Coin.getMajor().getType(), 64)).setText(color("&aDeposit &f{&e64&f} " + Coin.majorSingular())).setAction(RetroMisc::deposit3).assignToSlots(16);

		// 1
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 1)).setText(color("&aDeposit &f{&e1&f} " + Coin.minorSingular())).setAction(RetroMisc::deposit1M).assignToSlots(23);
		// 2
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 32)).setText(color("&aDeposit &f{&e32&f} " + Coin.minorSingular())).setAction(RetroMisc::deposit2M).assignToSlots(24);
		// 3
		builder.addElement(new ItemStack(Coin.getMinor().getType(), 64)).setText(color("&aDeposit &f{&e64&f} " + Coin.minorSingular())).setAction(RetroMisc::deposit3M).assignToSlots(25);

		builder.addElement(new ItemStack(Material.TOTEM_OF_UNDYING)).setText(color("&7[&eMain&7]")).setAction(RetroMisc::mainOpen).assignToSlots(6);

		builder.addElement(SkullItem.Head.find(player.getUniqueId())).setText(color("&7[&9Balance&7] &6" + RetroConomy.getInstance().getWalletBalance(player))).setAction(RetroMisc::balanceClick).assignToSlots(2);
		builder.setCloseAction(RetroMisc::mainClose);
		return builder.create(RetroConomy.getInstance());
	}

	private static void mainClose(MenuClose close) {
		Player p = close.getPlayer();
	}

	private static void balanceClick(MenuClick click) {
		click.disallowClick();
		getWallet(click.getPlayer()).open(click.getPlayer());
	}

	private static void mainOpen(MenuClick click) {
		Player p = click.getPlayer();
		getMain(p).open(p);
	}

	private static void withdraw1(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 1, WithdrawType.MAJOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.majorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void withdraw2(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 32, WithdrawType.MAJOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.majorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void withdraw3(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 64, WithdrawType.MAJOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.majorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void withdraw1M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 1, WithdrawType.MINOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.minorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void withdraw2M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 32, WithdrawType.MINOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.minorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void withdraw3M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.withdrawItems(p, 64, WithdrawType.MINOR);

		if (success > 0) {
			msg.send("&a" + success + " " + Coin.minorSingular() + " successfully withdrawn.");
		} else {
			msg.send("&cYou don't have enough money.");
		}
		balanceClick(click);
	}

	private static void deposit1(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 1);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void deposit2(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 32);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void deposit3(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 64);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void deposit1M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 1);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void deposit2M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 32);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void deposit3M(MenuClick click) {
		Player p = click.getPlayer();
		msg.assignPlayer(p);
		double success = RetroMisc.depositItems(p, 64);
		if (success > 0) {
			msg.send("&aYou successfully deposited " + 1 + " " + Coin.majorSingular() + " and got " + success);
		} else {
			msg.send("&c&oYou don't have enough " + Coin.majorSingular() + " in your inventory.");
		}
		balanceClick(click);
	}

	private static void walletClick(MenuClick click) {
		Player p = click.getPlayer();
		// open wallet menu.
		getWallet(p).open(p);
	}

	public static boolean isMultiWorld() {
		return options.getConfig().getBoolean("Options.world-settings.multi-world");
	}

	public static World getMainWorld() {
		return Bukkit.getWorld(options.getConfig().getString("Options.world-settings.main-world"));
	}

	public static int getAmount(Player arg0, ItemStack arg1) {
		if (arg1 == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = arg0.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(arg1))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	public static PlayerTransactionResult removeItem(Player p, Material mat, int amount) {
		if (getAmount(p, new ItemStack(mat)) < amount) {
			return PlayerTransactionResult.FAILED;
		}
		if (amount == 0) {
			return PlayerTransactionResult.SUCCESS;
		}
		ItemStack item = new ItemStack(mat);
		if (p.getInventory().contains(item.getType())) {
			for (int i = 0; i < amount; i++) {
				p.getInventory().removeItem(item);
			}
			return PlayerTransactionResult.SUCCESS;
		}
		return PlayerTransactionResult.FAILED;
	}

	public static PlayerTransactionResult removeItem(Player p, ItemStack item, int amount) {
		if (getAmount(p, item) < amount) {
			return PlayerTransactionResult.FAILED;
		}
		if (amount == 0) {
			return PlayerTransactionResult.SUCCESS;
		}
		if (p.getInventory().contains(item)) {
			for (int i = 0; i < amount; i++) {
				int amt = item.getAmount() - 1;
				item.setAmount(amt);
			}
			return PlayerTransactionResult.SUCCESS;
		}
		return PlayerTransactionResult.FAILED;
	}

	public enum WithdrawType {
		MAJOR, MINOR
	}

	public static double withdrawItems(Player target, int amount, WithdrawType type) {
		ItemStack toDrop = null;
		switch (type) {
			case MAJOR:
				toDrop = Coin.getMajor();
				break;
			case MINOR:
				toDrop = Coin.getMinor();
				break;
		}
		double value = 0.0;

		if (toDrop.equals(Coin.getMajor())) {
			value = Coin.getWorth(Coin.CurrencySize.MAJOR) * amount;
		}
		if (toDrop.equals(Coin.getMinor())) {
			value = Coin.getWorth(Coin.CurrencySize.MINOR) * amount;
		}
		if (RetroConomy.getInstance().walletHas(target, BigDecimal.valueOf(value))) {
			RetroConomy.getInstance().withdrawWallet(target, BigDecimal.valueOf(value));
			for (int i = 0; i < amount; i++) {
				target.getLocation().getWorld().dropItem(target.getLocation(), toDrop);
			}
		}
		return value;
	}

	public static double depositItems(Player target, int amount) {
		boolean transactionSuccess = false;
		List<Material> mats = new ArrayList<>(Arrays.asList(Coin.getMajor().getType(), Coin.getMinor().getType()));
		mats.addAll(Coin.getAltMap().keySet());
		Material toPick = null;
		if (Coin.usingSpecial) {
			for (ItemStack item : target.getInventory().getContents()) {
				if (item != null) {
					if (item.isSimilar(Coin.getMajor()) || item.isSimilar(Coin.getMinor())) {
						toPick = item.getType();
						transactionSuccess = removeItem(target, item, amount).transactionSuccess;
						break;
					}
				}
			}
		} else {
			for (Material mat : mats) {
				if (target.getInventory().contains(mat)) {
					toPick = mat;
					transactionSuccess = removeItem(target, mat, amount).transactionSuccess;
					break;
				}
			}
		}
		double value = 0.0;
		if (transactionSuccess) {
			if (toPick == Coin.getMajor().getType()) {
				value = Coin.getWorth(Coin.CurrencySize.MAJOR) * amount;
			} else
			if (toPick == Coin.getMinor().getType()) {
				value = Coin.getWorth(Coin.CurrencySize.MINOR) * amount;
			} else {
				if (!Coin.usingSpecial) {
					if (toPick != null) {
						value = Coin.getWorth(toPick) * amount;
					}
				}
			}
			RetroConomy.getInstance().depositWallet(target, BigDecimal.valueOf(value));
		}

		return value;
	}

}

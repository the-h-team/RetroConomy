package com.youtube.hempfest.retro.util;

import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.youtube.hempfest.retro.data.Config;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Coin {

	private static final Config options = Config.getOptions();

	public static boolean usingSpecial = options.getConfig().getBoolean("Options.use-special-currency");

	public static String majorSingular() {
		return options.getConfig().getString("Currency.Major.singular");
	}

	public static String majorPlural() {
		return options.getConfig().getString("Currency.Major.plural");
	}

	public static String minorSingular() {
		return options.getConfig().getString("Currency.Minor.singular");
	}

	public static String minorPlural() {
		return options.getConfig().getString("Currency.Minor.plural");
	}

	public static Locale getLocale() {
		Locale locale = null;
		switch (options.getConfig().getString("Currency.Format.locale").toLowerCase()) {
			case "en":
				locale = Locale.ENGLISH;
				break;
			case "ru":
				locale = new Locale("ru", "RU");
				break;
			case "de":
				locale = Locale.GERMAN;
				break;
		}
		return locale;
	}

	public static double getWorth(CurrencySize size) {
		double result = 0.0;
		switch (size) {

			case MAJOR:
				result = options.getConfig().getDouble("Currency.Major.worth");
				break;
			case MINOR:
				result = options.getConfig().getDouble("Currency.Minor.worth");
				break;
		}
		return result;
	}

	public static double getWorth(Material mat) {
		return options.getConfig().getDouble("Currency.Alt." + mat.name());
	}

	public static ItemStack getMajor() {
		String item = options.getConfig().getString("Currency.Major.item");
		if (usingSpecial) {
			String name = options.getConfig().getString("Currency.Major.name");
			if (Items.getMaterial(item) == null) {
				throw new TypeNotPresentException("Material", new Throwable("Item type by the name of " + item + " not found."));
			}
			ItemStack stack = new ItemStack(Items.getMaterial(item));
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(StringUtils.translate(name));
			stack.setItemMeta(meta);
			return stack;
		}
		if (Items.getMaterial(item) == null) {
			throw new TypeNotPresentException("Material", new Throwable("Item type by the name of " + item + " not found."));
		}
		return new ItemStack(Items.getMaterial(item));
	}

	public static ItemStack getMinor() {
		String item = options.getConfig().getString("Currency.Minor.item");
		if (usingSpecial) {
			String name = options.getConfig().getString("Currency.Minor.name");
			if (Items.getMaterial(item) == null) {
				throw new TypeNotPresentException("Material", new Throwable("Item type by the name of " + item + " not found."));
			}
			ItemStack stack = new ItemStack(Items.getMaterial(item));
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(StringUtils.translate(name));
			stack.setItemMeta(meta);
			return stack;
		}
		if (Items.getMaterial(item) == null) {
			throw new TypeNotPresentException("Material", new Throwable("Item type by the name of " + item + " not found."));
		}
		return new ItemStack(Items.getMaterial(item));
	}

	public static Map<Material, Double> getAltMap() {
		Map<Material, Double> map = new HashMap<>();
		for (String item : options.getConfig().getConfigurationSection("Currency.Alt").getKeys(false)) {
			if (Items.getMaterial(item) != null) {
				map.put(Items.getMaterial(item), options.getConfig().getDouble("Currency.Alt." + item));
			}
		}
		return map;
	}

	public enum CurrencySize {
		MAJOR, MINOR
	}

}

/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.retro.RetroConomy;
import org.bukkit.OfflinePlayer;

public class FormattedMessage {

	protected String context;

	protected FormattedMessage(String context) {
		this.context = context;
	}

	public static FormattedMessage convert(String context) {
		return new FormattedMessage(context);
	}

	public String next() {
		return StringUtils.use(StringUtils.use(StringUtils.use(StringUtils.use(context).replaceIgnoreCase("{MAJOR_SINGULAR}", RetroConomy.getInstance().getManager().getMajorSingular())).replaceIgnoreCase("{MAJOR_PLURAL}", RetroConomy.getInstance().getManager().getMajorPlural())).replaceIgnoreCase("{MINOR_SINGULAR}", RetroConomy.getInstance().getManager().getMinorSingular())).replaceIgnoreCase("{MINOR_PLURAL}", RetroConomy.getInstance().getManager().getMinorPlural());
	}

	public String next(double amount, double amount2) {
		return StringUtils.use(StringUtils.use(next()).replaceIgnoreCase("{AMOUNT_1}", RetroConomy.getInstance().getManager().format(amount))).replaceIgnoreCase("{AMOUNT_2}", RetroConomy.getInstance().getManager().format(amount2));
	}

	public String bought(String item, double amount, double each) {
		return StringUtils.use(StringUtils.use(StringUtils.use(next()).replaceIgnoreCase("{TOTAL}", RetroConomy.getInstance().getManager().format(amount))).replaceIgnoreCase("{PRICE}", RetroConomy.getInstance().getManager().format(each))).replaceIgnoreCase("{ITEM}", item);
	}

	public String next(String amount, String amount2) {
		return StringUtils.use(StringUtils.use(next()).replaceIgnoreCase("{AMOUNT_1}", amount)).replaceIgnoreCase("{AMOUNT_2}", amount2);
	}

	public String from(String currency) {
		return StringUtils.use(next()).replaceIgnoreCase("{CURRENCY}", currency);
	}

	public String from(OfflinePlayer target) {
		return StringUtils.use(next()).replaceIgnoreCase("{PLAYER}", target.getName());
	}

	public String from(String currency, double amount, double amount2) {
		return StringUtils.use(next(amount, amount2)).replaceIgnoreCase("{CURRENCY}", currency);
	}


}

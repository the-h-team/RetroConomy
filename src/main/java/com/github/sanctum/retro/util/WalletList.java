/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.WalletAccount;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WalletList extends UniformedComponents<WalletAccount> {

	@Override
	public List<WalletAccount> list() {
		return RetroConomy.getInstance().getManager().WALLETS;
	}

	@Override
	public List<WalletAccount> sort() {
		List<WalletAccount> list = list();
		list.sort(Comparator.comparingDouble(value -> value.getBalance().doubleValue()));
		return list;
	}

	@Override
	public List<WalletAccount> sort(Comparator<? super WalletAccount> comparable) {
		List<WalletAccount> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<WalletAccount> collect() {
		return RetroConomy.getInstance().getManager().WALLETS;
	}

	@Override
	public WalletAccount[] array() {
		return list().toArray(new WalletAccount[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super WalletAccount, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<WalletAccount> filter(Predicate<? super WalletAccount> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public WalletAccount getFirst() {
		return list().get(0);
	}

	@Override
	public WalletAccount getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public WalletAccount get(int index) {
		return list().get(index);
	}
}

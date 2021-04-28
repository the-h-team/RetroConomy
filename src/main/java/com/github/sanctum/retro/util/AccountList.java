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
import com.github.sanctum.retro.construct.core.BankAccount;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AccountList extends UniformedComponents<BankAccount> {

	@Override
	public List<BankAccount> list() {
		return RetroConomy.getInstance().getManager().ACCOUNTS;
	}

	@Override
	public List<BankAccount> sort() {
		List<BankAccount> list = list();
		list.sort(Comparator.comparingDouble(value -> value.getBalance().doubleValue()));
		return list;
	}

	@Override
	public List<BankAccount> sort(Comparator<? super BankAccount> comparable) {
		List<BankAccount> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<BankAccount> collect() {
		return RetroConomy.getInstance().getManager().ACCOUNTS;
	}

	@Override
	public BankAccount[] array() {
		return list().toArray(new BankAccount[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super BankAccount, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<BankAccount> filter(Predicate<? super BankAccount> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public BankAccount getFirst() {
		return list().get(0);
	}

	@Override
	public BankAccount getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public BankAccount get(int index) {
		return list().get(index);
	}
}

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
import com.github.sanctum.retro.construct.core.RetroAccount;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AccountList extends UniformedComponents<RetroAccount> {

	@Override
	public List<RetroAccount> list() {
		return RetroConomy.getInstance().getManager().ACCOUNTS;
	}

	@Override
	public List<RetroAccount> sort() {
		List<RetroAccount> list = list();
		list.sort(Comparator.comparingDouble(value -> value.getBalance().doubleValue()));
		return list;
	}

	@Override
	public List<RetroAccount> sort(Comparator<? super RetroAccount> comparable) {
		List<RetroAccount> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<RetroAccount> collect() {
		return RetroConomy.getInstance().getManager().ACCOUNTS;
	}

	@Override
	public RetroAccount[] array() {
		return list().toArray(new RetroAccount[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super RetroAccount, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<RetroAccount> filter(Predicate<? super RetroAccount> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public RetroAccount getFirst() {
		return list().get(0);
	}

	@Override
	public RetroAccount getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public RetroAccount get(int index) {
		return list().get(index);
	}
}

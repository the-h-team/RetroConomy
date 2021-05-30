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
import com.github.sanctum.retro.construct.core.Currency;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AcceptableCurrencies extends UniformedComponents<Currency> {

	private static final long serialVersionUID = 1351446222428131126L;

	@Override
	public List<Currency> list() {
		return RetroConomy.getInstance().getManager().CURRENCIES;
	}

	@Override
	public List<Currency> sort() {
		List<Currency> list = list();
		list.sort(Comparator.comparingDouble(Currency::getWorth));
		return list;
	}

	@Override
	public List<Currency> sort(Comparator<? super Currency> comparable) {
		List<Currency> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<Currency> collect() {
		return RetroConomy.getInstance().getManager().CURRENCIES;
	}

	@Override
	public Currency[] array() {
		return list().toArray(new Currency[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super Currency, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<Currency> filter(Predicate<? super Currency> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public Currency getFirst() {
		return list().get(0);
	}

	@Override
	public Currency getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public Currency get(int index) {
		return list().get(index);
	}
}

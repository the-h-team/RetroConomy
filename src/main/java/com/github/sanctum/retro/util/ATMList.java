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
import com.github.sanctum.retro.construct.core.ATM;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ATMList extends UniformedComponents<ATM> {


	private static final long serialVersionUID = -7003313369701219244L;

	@Override
	public List<ATM> list() {
		return RetroConomy.getInstance().getManager().ATMS;
	}

	@Override
	public List<ATM> sort() {
		return list();
	}

	@Override
	public List<ATM> sort(Comparator<? super ATM> comparable) {
		list().sort(comparable);
		return list();
	}

	@Override
	public Collection<ATM> collect() {
		return list();
	}

	@Override
	public ATM[] array() {
		return list().toArray(new ATM[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super ATM, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<ATM> filter(Predicate<? super ATM> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public ATM getFirst() {
		return list().get(0);
	}

	@Override
	public ATM getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public ATM get(int index) {
		return list().get(index);
	}
}

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
import com.github.sanctum.retro.construct.core.Shop;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ATMList extends UniformedComponents<Shop> {


	private static final long serialVersionUID = -7003313369701219244L;

	@Override
	public List<Shop> list() {
		return RetroConomy.getInstance().getManager().SHOPS;
	}

	@Override
	public List<Shop> sort() {
		return list();
	}

	@Override
	public List<Shop> sort(Comparator<? super Shop> comparable) {
		list().sort(comparable);
		return list();
	}

	@Override
	public Collection<Shop> collect() {
		return list();
	}

	@Override
	public Shop[] array() {
		return list().toArray(new Shop[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super Shop, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<Shop> filter(Predicate<? super Shop> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public Shop getFirst() {
		return list().get(0);
	}

	@Override
	public Shop getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public Shop get(int index) {
		return list().get(index);
	}
}

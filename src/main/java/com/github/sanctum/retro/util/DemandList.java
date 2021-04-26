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
import com.github.sanctum.retro.construct.item.ItemDemand;
import com.github.sanctum.retro.construct.item.SellableItem;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DemandList extends UniformedComponents<ItemDemand> {

	@Override
	public List<ItemDemand> list() {
		return RetroConomy.getInstance().getManager().SHOP;
	}

	@Override
	public List<ItemDemand> sort() {
		List<ItemDemand> list = list();
		list.sort(Comparator.comparingDouble(SellableItem::getMultiplier));
		return list;
	}

	@Override
	public List<ItemDemand> sort(Comparator<? super ItemDemand> comparable) {
		List<ItemDemand> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<ItemDemand> collect() {
		return RetroConomy.getInstance().getManager().SHOP;
	}

	@Override
	public ItemDemand[] array() {
		return list().toArray(new ItemDemand[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super ItemDemand, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<ItemDemand> filter(Predicate<? super ItemDemand> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public ItemDemand getFirst() {
		return list().get(0);
	}

	@Override
	public ItemDemand getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public ItemDemand get(int index) {
		return list().get(index);
	}
}

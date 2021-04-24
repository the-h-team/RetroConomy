package com.github.sanctum.retro.util;

import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.construct.core.RetroWallet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WalletList extends UniformedComponents<RetroWallet> {

	@Override
	public List<RetroWallet> list() {
		return RetroConomy.getInstance().getManager().WALLETS;
	}

	@Override
	public List<RetroWallet> sort() {
		List<RetroWallet> list = list();
		list.sort(Comparator.comparingDouble(value -> value.getBalance().doubleValue()));
		return list;
	}

	@Override
	public List<RetroWallet> sort(Comparator<? super RetroWallet> comparable) {
		List<RetroWallet> list = list();
		list.sort(comparable);
		return list;
	}

	@Override
	public Collection<RetroWallet> collect() {
		return RetroConomy.getInstance().getManager().WALLETS;
	}

	@Override
	public RetroWallet[] array() {
		return list().toArray(new RetroWallet[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super RetroWallet, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<RetroWallet> filter(Predicate<? super RetroWallet> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public RetroWallet getFirst() {
		return list().get(0);
	}

	@Override
	public RetroWallet getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public RetroWallet get(int index) {
		return list().get(index);
	}
}

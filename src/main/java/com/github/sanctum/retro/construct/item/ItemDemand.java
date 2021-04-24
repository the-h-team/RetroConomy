package com.github.sanctum.retro.construct.item;

import java.util.Map;

public interface ItemDemand extends Modifiable, SellableItem{

	long getRecentBought();

	long getRecentSold();

	long getSold(String user);

	long getSold();

	long getBought(String user);

	long getBought();

	double getPopularity();

	String getLastBuyer();

	String getLastSeller();

	Map<String, Long> getBuyerMap();

	Map<String, Long> getSellerMap();

}

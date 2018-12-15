package com.builtbroken.helmbucket;

import net.minecraft.item.Item;

public enum BucketTypes {
	
	IRON(net.minecraft.init.Items.IRON_HELMET),
	GOLD(net.minecraft.init.Items.GOLDEN_HELMET),
	DIAMOND(net.minecraft.init.Items.DIAMOND_HELMET);
	
	public final Item baseHelmet;
	
	private BucketTypes(Item item) {
		this.baseHelmet = item;
	}
	
	
}

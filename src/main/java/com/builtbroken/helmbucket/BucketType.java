package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.FluidModule;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BucketType {
	
	public final String name;
	public final Item base;
	public final HelmetBucketMaterial material;
	
	public BucketType(String name, Item baseHelmet) {
		this.name = name;
    	this.base = baseHelmet;
    	this.material = new HelmetBucketMaterial(name);
	}
	
    public ItemStack getBucket() {
        return new ItemStack(FluidModule.bucket, 1, this.material.metaValue);
    }
	
}

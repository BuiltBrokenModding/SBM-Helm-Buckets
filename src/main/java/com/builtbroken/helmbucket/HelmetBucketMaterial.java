package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.FluidModule;
import com.builtbroken.mc.fluids.bucket.BucketMaterial;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HelmetBucketMaterial extends BucketMaterial {
	
	public final String name;
	public final Item base;
	
	public HelmetBucketMaterial(String loadThisName, Item base) {
		super(HelmBucket.PREFIX + "HelmBucket." + loadThisName, new ResourceLocation(HelmBucket.PREFIX + "bucket." + loadThisName));
		this.name = loadThisName;
		this.base = base;
	}
	
    public HelmetBucketMaterial(BucketTypes type)
    {
        super(HelmBucket.PREFIX + "HelmBucket." + type.name().toLowerCase(), new ResourceLocation(HelmBucket.PREFIX + "bucket." + type.name().toLowerCase()));
        this.name = type.name().toLowerCase();
        this.base = type.baseHelmet;
    }
    
    public ItemStack getBucket() {
        return new ItemStack(FluidModule.bucket, 1, this.metaValue);
    }

   
}
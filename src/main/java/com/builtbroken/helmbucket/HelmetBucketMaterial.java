package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.FluidModule;
import com.builtbroken.mc.fluids.bucket.BucketMaterial;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HelmetBucketMaterial extends BucketMaterial {
	
	public HelmetBucketMaterial(String loadThisName) {
		super(HelmBucket.PREFIX + "HelmBucket." + loadThisName, new ResourceLocation(HelmBucket.PREFIX + "helmbucket." + loadThisName));
	}

	@Override
	public BucketMaterial getDamagedBucket(ItemStack stack) {
		return HelmBucket.getHelmet(this.materialName.split("HelmBucket.")[1]).material;
	}
	
	
   
}
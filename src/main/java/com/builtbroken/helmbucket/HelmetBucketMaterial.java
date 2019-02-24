package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.bucket.BucketMaterial;
import com.builtbroken.mc.fluids.bucket.BucketMaterialMimic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HelmetBucketMaterial extends BucketMaterialMimic
{
    public static final ResourceLocation FLUID_ICON = new ResourceLocation(HelmBucket.DOMAIN, "fluid_icon");
    public HelmetBucketMaterial(ItemStack itemStack)
    {
        super(itemStack.getTranslationKey(), itemStack);
        invertBucketRender(); //Render icon upside down by default
        fluidResourceLocation = FLUID_ICON;
    }

    @Override
    public BucketMaterial getDamagedBucket(ItemStack stack)
    {
        return this; //TODO implement a damage mechanic, for now return self to prevent loss of items
    }
}
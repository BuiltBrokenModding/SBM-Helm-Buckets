package com.builtbroken.helmbucket;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
public class ItemHelmBucket extends Item
{
    public ItemHelmBucket()
    {
        setUnlocalizedName("helmbucket:helmBucket");
        setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    public MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean flag)
    {
        return super.getMovingObjectPositionFromPlayer(world, player, flag);
    }

    public static ItemStack getStack(Item item, Material material)
    {
        return null;
    }
}

package com.builtbroken.helmbucket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
public class ItemHelmBucket extends Item
{
    @SideOnly(Side.CLIENT)
    IIcon[] fluidIcons;

    @SideOnly(Side.CLIENT)
    IIcon[] helmIcons;

    public ItemHelmBucket()
    {
        setUnlocalizedName("helmbucket:helmBucket");
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        int meta = stack.getItemDamage();
        int fluidMeta = meta % 1000;

        if (fluidMeta == 1)
        {
            list.add(StatCollector.translateToLocal(Blocks.water.getUnlocalizedName() + ".name"));
        }
        else if (fluidMeta == 2)
        {
            list.add(StatCollector.translateToLocal(Blocks.lava.getUnlocalizedName() + ".name"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int meta, int pass)
    {
        if (pass == 0)
        {
            return fluidIcons[meta % 1000];
        }
        return helmIcons[meta / 1000];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return helmIcons[meta / 1000];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.helmIcons = new IIcon[3];
        this.helmIcons[0] = reg.registerIcon("helmbucket:iron_helmet");
        this.helmIcons[1] = reg.registerIcon("helmbucket:gold_helmet");
        this.helmIcons[2] = reg.registerIcon("helmbucket:diamond_helmet");

        this.fluidIcons = new IIcon[3];
        this.fluidIcons[0] = reg.registerIcon("helmbucket:blank");
        this.fluidIcons[1] = reg.registerIcon("helmbucket:water");
        this.fluidIcons[2] = reg.registerIcon("helmbucket:lava");
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean p_77663_5_)
    {
        //TODO make metal helms burn the player randomly
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getItemDamage();
        if (meta < 1000)
        {
            return getUnlocalizedName() + ".iron";
        }
        else if (meta < 2000)
        {
            return getUnlocalizedName() + ".gold";
        }
        if (meta < 3000)
        {
            return getUnlocalizedName() + ".diamond";
        }
        return getUnlocalizedName();
    }

    @Override
    public MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean flag)
    {
        return super.getMovingObjectPositionFromPlayer(world, player, flag);
    }

    public static ItemStack getStack(Item item, ItemStack itemToSave, Block block)
    {
        int meta = 0;
        if (item == Items.iron_helmet)
        {
            meta = 0;
        }
        else if (item == Items.golden_helmet)
        {
            meta = 1000;
        }
        else if (item == Items.diamond_helmet)
        {
            meta = 2000;
        }

        Material material = block.getMaterial();
        if (material == Material.water)
        {
            meta += 1;
        }
        else if (material == Material.lava)
        {
            meta += 2;
        }
        ItemStack stack = new ItemStack(HelmBucket.itemHelmBucket, 1, meta);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setTag("prevItem", itemToSave.writeToNBT(new NBTTagCompound()));
        return stack;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, false);

        if (movingobjectposition == null)
        {
            return stack;
        }
        else
        {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (!world.canMineBlock(player, i, j, k))
                {
                    return stack;
                }

                if (movingobjectposition.sideHit == 0)
                {
                    --j;
                }

                if (movingobjectposition.sideHit == 1)
                {
                    ++j;
                }

                if (movingobjectposition.sideHit == 2)
                {
                    --k;
                }

                if (movingobjectposition.sideHit == 3)
                {
                    ++k;
                }

                if (movingobjectposition.sideHit == 4)
                {
                    --i;
                }

                if (movingobjectposition.sideHit == 5)
                {
                    ++i;
                }

                if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, stack))
                {
                    return stack;
                }

                if (this.tryPlaceContainedLiquid(world, i, j, k, stack.getItemDamage() % 1000) && !player.capabilities.isCreativeMode)
                {
                    return getPreviousItem(stack);
                }
            }

            return stack;
        }
    }

    public ItemStack getPreviousItem(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            return ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("prevItem"));
        }
        int helm = stack.getItemDamage() / 1000;
        switch (helm)
        {
            case 0:
                return new ItemStack(Items.iron_helmet, 1, 0);
            case 1:
                return new ItemStack(Items.golden_helmet, 1, 0);
            case 2:
                return new ItemStack(Items.diamond_helmet, 1, 0);
        }
        stack.stackSize--;
        return stack;
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public boolean tryPlaceContainedLiquid(World world, int x, int y, int z, int fluidMeta)
    {
        Material material = world.getBlock(x, y, z).getMaterial();
        boolean flag = !material.isSolid();

        if (!world.isAirBlock(x, y, z) && !flag)
        {
            return false;
        }
        else
        {
            if (world.provider.isHellWorld && getFluidForMeta(fluidMeta).getMaterial() == Material.water)
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l)
                {
                    world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + Math.random(), (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                if (!world.isRemote && flag && !material.isLiquid())
                {
                    world.func_147480_a(x, y, z, true);
                }

                Block block = getFluidForMeta(fluidMeta);
                world.setBlock(x, y, z, block, 0, 3);
                block.onNeighborBlockChange(world, x, y, z, block);
            }

            return true;
        }
    }

    /**
     * @param fluidMeta meta % 1000
     * @return
     */
    public Block getFluidForMeta(int fluidMeta)
    {
        if (fluidMeta == 1)
        {
            return Blocks.water;
        }
        else if (fluidMeta == 2)
        {
            return Blocks.lava;
        }
        return Blocks.air;
    }
}

package com.builtbroken.helmbucket;


import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
public class ItemHelmBucket extends Item
{
	/*
    @SideOnly(Side.CLIENT)
    IIcon[] fluidIcons;

    @SideOnly(Side.CLIENT)
    IIcon[] helmIcons;
	 */
	
    public ItemHelmBucket()
    {
    	this.setRegistryName("helmbucket");
        this.setUnlocalizedName("helmbucket:helmBucket");
        this.setMaxStackSize(1);
    }
    
    
    
    @Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        int meta = stack.getItemDamage();
        int fluidMeta = meta % 1000;

        if (fluidMeta == 1)
        {
            list.add(I18n.translateToLocal(Blocks.WATER.getUnlocalizedName() + ".name"));
        }
        else if (fluidMeta == 2)
        {
            list.add(I18n.translateToLocal(Blocks.LAVA.getUnlocalizedName() + ".name"));
        }
	}

    /*
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
    */

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
	protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
		return super.rayTrace(worldIn, playerIn, useLiquids);
	}

    public static ItemStack getStack(Item item, ItemStack itemToSave, Block block)
    {
        int meta = 0;
        if (item == Items.IRON_HELMET)
        {
            meta = 0;
        }
        else if (item == Items.GOLDEN_HELMET)
        {
            meta = 1000;
        }
        else if (item == Items.DIAMOND_HELMET)
        {
            meta = 2000;
        }

        Material material = block.getMaterial(block.getDefaultState());
        if (material == Material.WATER)
        {
            meta += 1;
        }
        else if (material == Material.LAVA)
        {
            meta += 2;
        }
        ItemStack stack = new ItemStack(HelmBucket.itemHelmBucket, 1, meta);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setTag("prevItem", itemToSave.writeToNBT(new NBTTagCompound()));
        return stack;
    }

	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {   
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, raytraceresult);
        if (ret != null) return ret;

        if (raytraceresult == null)
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        }
        else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        }
        else
        {
            BlockPos blockpos = raytraceresult.getBlockPos();

            if (!worldIn.isBlockModifiable(playerIn, blockpos))
            {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
            }
            else
            {
                boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
                BlockPos blockpos1 = flag1 && raytraceresult.sideHit == EnumFacing.UP ? blockpos : blockpos.offset(raytraceresult.sideHit);

                if (!playerIn.canPlayerEdit(blockpos1, raytraceresult.sideHit, itemstack))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                }
                else if (this.tryPlaceContainedLiquid(worldIn, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), itemstack.getMetadata()))
                {
                    if (playerIn instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, blockpos1, itemstack);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return !playerIn.capabilities.isCreativeMode ? new ActionResult(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET)) : new ActionResult(EnumActionResult.SUCCESS, itemstack);
                }
                else
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                }
            }
        }
    }

    public ItemStack getPreviousItem(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            return new ItemStack(stack.getTagCompound().getCompoundTag("prevItem"));
        }
        int helm = stack.getItemDamage() / 1000;
        switch (helm)
        {
            case 0:
                return new ItemStack(Items.IRON_HELMET, 1, 0);
            case 1:
                return new ItemStack(Items.GOLDEN_HELMET, 1, 0);
            case 2:
                return new ItemStack(Items.DIAMOND_HELMET, 1, 0);
        }
        stack.shrink(1);
        return stack;
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public boolean tryPlaceContainedLiquid(World world, int x, int y, int z, int fluidMeta)
    {
        Material material = world.getBlockState(new BlockPos(x, y, z)).getBlock().getMaterial(world.getBlockState(new BlockPos(x, y, z)));
        boolean flag = !material.isSolid();

        if (!world.isAirBlock(new BlockPos(x, y, z)) && !flag)
        {
            return false;
        }
        else
        {
            if (world.provider.isNether() && getFluidForMeta(fluidMeta).getMaterial(getFluidForMeta(fluidMeta).getDefaultState()) == Material.WATER)
            {
                world.playSound(null, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) x + Math.random(), (double) y + Math.random(), (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                if (!world.isRemote && flag && !material.isLiquid())
                {
                    world.destroyBlock(new BlockPos(x, y, z), true);
                }

                Block block = getFluidForMeta(fluidMeta);
                world.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), 1);
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
            return Blocks.WATER;
        }
        else if (fluidMeta == 2)
        {
            return Blocks.LAVA;
        }
        return Blocks.AIR;
    }
}

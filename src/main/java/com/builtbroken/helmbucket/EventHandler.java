package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.FluidModule;
import com.builtbroken.mc.fluids.bucket.BucketMaterial;
import com.builtbroken.mc.fluids.bucket.BucketMaterialHandler;
import com.builtbroken.mc.fluids.mods.BucketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2/24/2019.
 */
@Mod.EventBusSubscriber(modid = HelmBucket.DOMAIN)
public class EventHandler
{
    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event)
    {
        final ItemStack heldItemStack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        //Convert helm to fluid bucket
        if (HelmBucket.ITEM_TO_MATERIAL.keySet().contains(heldItemStack.getItem()))
        {
            final HelmetBucketMaterial bucketMaterial = HelmBucket.ITEM_TO_MATERIAL.get(heldItemStack.getItem());
            final ItemStack bucketStack = bucketMaterial.getNewBucketStack(heldItemStack);

            ActionResult<BlockPos> result = onItemRightClickEmpty(event.getWorld(), player, bucketStack, bucketMaterial);
            if (result.getType() == EnumActionResult.SUCCESS)
            {
                //Attempt to pickup fluid
                ItemStack bucketHelmStack = FluidModule.bucket.pickupFluid(player, bucketStack, bucketMaterial, event.getWorld(), result.getResult());

                //Attempt to insert into inventory
                ItemStack stack2 = addToInventory(heldItemStack, player, bucketHelmStack);
                player.setItemStackToSlot(player.getHeldItem(event.getHand()) == event.getEntityPlayer().getHeldItemMainhand() ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, stack2);

                //Cancel default event aka Don't switch helmet onto head
                event.setCanceled(true);
            }
        }
        //Convert fluid bucket to helm
        else if (event.getItemStack().getItem() == FluidModule.bucket)
        {
            final BucketMaterial bucketMaterial = BucketMaterialHandler.getMaterial(event.getItemStack().getItemDamage());
            if (bucketMaterial instanceof HelmetBucketMaterial)
            {
                if(!event.getWorld().isRemote)
                {
                    ActionResult<ItemStack> result = FluidModule.bucket.onItemRightClick(event.getWorld(), event.getEntityPlayer(), event.getHand());
                    boolean isEmpty = FluidModule.bucket.isEmpty(result.getResult());
                    if (result.getType() == EnumActionResult.SUCCESS)
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            ItemStack originalStack = isEmpty ? ((HelmetBucketMaterial) bucketMaterial).getOriginalStack(heldItemStack) : null;
                            if (originalStack == null)
                            {
                                originalStack = result.getResult();
                            }
                            EntityEquipmentSlot slot = player.getHeldItem(event.getHand()) == event.getEntityPlayer().getHeldItemMainhand() ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
                            player.setItemStackToSlot(slot, originalStack);

                        }
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    event.setCanceled(true); // Don't allow normal replace code to run
                }
            }
        }
    }

    private static ItemStack addToInventory(ItemStack originalStack, EntityPlayer player, ItemStack newStack)
    {
        if (player.capabilities.isCreativeMode)
        {
            return originalStack;
        }
        else
        {
            originalStack.shrink(1);
            if (originalStack.isEmpty())
            {
                return newStack;
            }
            else
            {
                if (!player.inventory.addItemStackToInventory(newStack))
                {
                    player.dropItem(newStack, false);
                }
                return originalStack;
            }
        }
    }

    /* Determines whether liquid can be taken and returns raytrace position */
    public static ActionResult<BlockPos> onItemRightClickEmpty(World world, EntityPlayer player, ItemStack bucketStack, BucketMaterial bucketMaterial)
    {
        //Find a raytrace
        RayTraceResult movingobjectposition = rayTrace(world, player, true);
        if (movingobjectposition != null)
        {
            //Check that it is a block
            if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                //Let fluid tiles handle there own logic
                TileEntity tile = world.getTileEntity(movingobjectposition.getBlockPos());
                if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, movingobjectposition.sideHit))
                {
                    return new ActionResult(EnumActionResult.PASS, null);
                }

                //Do not edit if blocked
                if (!world.isBlockModifiable(player, movingobjectposition.getBlockPos()))
                {
                    return new ActionResult(EnumActionResult.PASS, null);
                }

                if (player.canPlayerEdit(movingobjectposition.getBlockPos(), movingobjectposition.sideHit, bucketStack))
                {
                    return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                }
            }
            //TODO maybe interact with entity?
        }
        return new ActionResult(EnumActionResult.PASS, null);
    }

    /* Determines whether liquid can be placed and returns raytrace position */
    public static ActionResult<BlockPos> onItemRightClickFull(World world, EntityPlayer player, ItemStack bucketStack, BucketMaterial bucketMaterial)
    {
        RayTraceResult movingobjectposition = rayTrace(world, player, true);

        if (movingobjectposition != null)
        {
            if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                //Let fluid tiles handle there own logic
                TileEntity tile = world.getTileEntity(movingobjectposition.getBlockPos());
                if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, movingobjectposition.sideHit))
                {
                    return new ActionResult(EnumActionResult.PASS, null);
                }

                //Do not edit if blocked
                if (!world.isBlockModifiable(player, movingobjectposition.getBlockPos()))
                {
                    return new ActionResult(EnumActionResult.PASS, null);
                }

                final IBlockState state = world.getBlockState(movingobjectposition.getBlockPos());
                final Block block = state.getBlock();
                final Material blockMaterial = state.getMaterial();

                if (player.canPlayerEdit(movingobjectposition.getBlockPos(), movingobjectposition.sideHit, bucketStack))
                {
                    //Material handler
                    if (bucketMaterial.getHandler() != null)
                    {
                        return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                    }
                    //Mod support handling
                    if (BucketHandler.blockToHandler.containsKey(block))
                    {
                        BucketHandler handler = BucketHandler.blockToHandler.get(block);
                        if (handler != null)
                        {
                            return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                        }
                    }

                    if (!blockMaterial.isSolid() && block.isReplaceable(world, movingobjectposition.getBlockPos()))
                    {
                        return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                    }
                }

                //Offset position based on side hit
                BlockPos blockpos1 = movingobjectposition.getBlockPos().offset(movingobjectposition.sideHit);

                if (player.canPlayerEdit(blockpos1, movingobjectposition.sideHit, bucketStack))
                {
                    //Bucket material handling
                    if (bucketMaterial.getHandler() != null)
                    {
                        return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                    }

                    //Mod support handling
                    if (BucketHandler.blockToHandler.containsKey(block))
                    {
                        BucketHandler handler = BucketHandler.blockToHandler.get(block);
                        if (handler != null)
                        {
                            return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                        }
                    }
                    return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
                }

            }
        }
        return new ActionResult(EnumActionResult.PASS, null);
    }

    public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d vec3d1 = vec3d.add((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }
}

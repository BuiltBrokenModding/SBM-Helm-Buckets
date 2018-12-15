package com.builtbroken.helmbucket;

import com.builtbroken.helmbucket.network.PacketBucketAction;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent event)
    {
        if (pickupFluid(event.getWorld(), event.getEntityPlayer(), false))
        {
            HelmBucket.packetHandler.sendToServer(new PacketBucketAction());
            event.setCanceled(true);
        }
    }

    public static boolean pickupFluid(World world, EntityPlayer entityPlayer, boolean doAction)
    {
        ItemStack heldItem = entityPlayer.getHeldItem(entityPlayer.getActiveHand());
        if (heldItem != null && heldItem.getItem() instanceof ItemArmor)
        {
            Item item = heldItem.getItem();
            if (item == Items.IRON_HELMET || item == Items.GOLDEN_HELMET || item == Items.DIAMOND_HELMET)
            {
                RayTraceResult ray = entityPlayer.rayTrace(entityPlayer.REACH_DISTANCE.getDefaultValue(), 0F);
                if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    int i = ray.getBlockPos().getX();
                    int j = ray.getBlockPos().getY();
                    int k = ray.getBlockPos().getZ();

                    if (!world.canMineBlockBody(entityPlayer, ray.getBlockPos()))
                    {
                        return false;
                    }

                    if (!entityPlayer.canPlayerEdit(ray.getBlockPos(), ray.sideHit, heldItem))
                    {
                        return false;
                    }
                    IBlockState state = world.getBlockState(ray.getBlockPos());
                    Block block = state.getBlock();
                    Material material = block.getMaterial(state);
                    int l = block.getMetaFromState(state);

                    if (material == Material.WATER && l == 0)
                    {
                        if (doAction)
                        {
                            world.setBlockToAir(ray.getBlockPos());
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, addToInventory(heldItem, entityPlayer, ItemHelmBucket.getStack(item, heldItem, block)));
                            entityPlayer.inventoryContainer.detectAndSendChanges();
                        }
                        return true;
                    }

                    if (material == Material.LAVA && l == 0 && item == Items.DIAMOND_HELMET)
                    {
                        if (doAction)
                        {
                            world.setBlockToAir(ray.getBlockPos());
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, addToInventory(heldItem, entityPlayer, ItemHelmBucket.getStack(item, heldItem, block)));
                            entityPlayer.inventoryContainer.detectAndSendChanges();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
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
}

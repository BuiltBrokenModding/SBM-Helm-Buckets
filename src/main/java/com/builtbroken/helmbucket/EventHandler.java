package com.builtbroken.helmbucket;

import com.builtbroken.helmbucket.network.PacketBucketAction;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent event)
    {
        if (pickupFluid(event.world, event.entityPlayer, false))
        {
            HelmBucket.packetHandler.sendToServer(new PacketBucketAction());
            event.setCanceled(true);
        }
    }

    public static boolean pickupFluid(World world, EntityPlayer entityPlayer, boolean doAction)
    {
        ItemStack heldItem = entityPlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemArmor)
        {
            Item item = heldItem.getItem();
            if (item == Items.iron_helmet || item == Items.golden_helmet || item == Items.diamond_helmet)
            {
                MovingObjectPosition movingobjectposition = HelmBucket.itemHelmBucket.getMovingObjectPositionFromPlayer(world, entityPlayer, true);
                if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!world.canMineBlock(entityPlayer, i, j, k))
                    {
                        return false;
                    }

                    if (!entityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, heldItem))
                    {
                        return false;
                    }

                    Block block = world.getBlock(i, j, k);
                    Material material = block.getMaterial();
                    int l = world.getBlockMetadata(i, j, k);

                    if (material == Material.water && l == 0)
                    {
                        if (doAction)
                        {
                            world.setBlockToAir(i, j, k);
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, addToInventory(heldItem, entityPlayer, ItemHelmBucket.getStack(item, heldItem, block)));
                            entityPlayer.inventoryContainer.detectAndSendChanges();
                        }
                        return true;
                    }

                    if (material == Material.lava && l == 0 && item == Items.diamond_helmet)
                    {
                        if (doAction)
                        {
                            world.setBlockToAir(i, j, k);
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
        else if (--originalStack.stackSize <= 0)
        {
            return newStack;
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(newStack))
            {
                player.dropPlayerItemWithRandomChoice(newStack, false);
            }
            return originalStack;
        }
    }
}

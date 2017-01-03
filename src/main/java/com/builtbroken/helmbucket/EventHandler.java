package com.builtbroken.helmbucket;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
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
        if (!event.world.isRemote)
        {
            ItemStack heldItem = event.entityPlayer.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof ItemArmor)
            {
                Item item = heldItem.getItem();
                if (item == Items.iron_helmet || item == Items.golden_helmet || item == Items.diamond_helmet)
                {
                    MovingObjectPosition movingobjectposition = HelmBucket.itemHelmBucket.getMovingObjectPositionFromPlayer(event.world, event.entityPlayer, true);
                    if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        int i = movingobjectposition.blockX;
                        int j = movingobjectposition.blockY;
                        int k = movingobjectposition.blockZ;

                        if (!event.world.canMineBlock(event.entityPlayer, i, j, k))
                        {
                            event.useItem = Event.Result.ALLOW;
                            return;
                        }

                        if (!event.entityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, heldItem))
                        {
                            event.useItem = Event.Result.ALLOW;
                            return;
                        }

                        Material material = event.world.getBlock(i, j, k).getMaterial();
                        int l = event.world.getBlockMetadata(i, j, k);

                        if (material == Material.water && l == 0)
                        {
                            event.world.setBlockToAir(i, j, k);
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, this.addToInventory(heldItem, event.entityPlayer, ItemHelmBucket.getStack(item, material)));
                            event.entityPlayer.inventoryContainer.detectAndSendChanges();
                            event.useItem = Event.Result.ALLOW;
                            return;
                        }

                        if (material == Material.lava && l == 0 && item == Items.diamond_helmet)
                        {
                            event.world.setBlockToAir(i, j, k);
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, this.addToInventory(heldItem, event.entityPlayer, ItemHelmBucket.getStack(item, material)));
                            event.entityPlayer.inventoryContainer.detectAndSendChanges();
                            event.useItem = Event.Result.ALLOW;
                            return;
                        }
                    }
                }
            }
        }
    }

    private ItemStack addToInventory(ItemStack originalStack, EntityPlayer player, ItemStack newStack)
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

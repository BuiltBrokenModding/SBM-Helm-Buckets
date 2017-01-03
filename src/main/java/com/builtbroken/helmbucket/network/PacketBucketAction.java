package com.builtbroken.helmbucket.network;

import com.builtbroken.helmbucket.EventHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Notification packet used to trigger bucket action server side from client
 */
public class PacketBucketAction extends Packet
{
    @Override
    public void handleServerSide(EntityPlayer player)
    {
        EventHandler.pickupFluid(player.worldObj, player, true);
    }
}

package com.builtbroken.helmbucket.network;

import com.builtbroken.helmbucket.HelmBucket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author tgame14
 * @since 31/05/14
 */
public class ResonantChannelHandler extends net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec<Packet>
{
    public boolean silenceStackTrace = false; //TODO add command and config

    public ResonantChannelHandler()
    {
        this.addDiscriminator(0, PacketBucketAction.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, Packet packet, ByteBuf target) throws Exception
    {
        try
        {
            packet.write(target);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                HelmBucket.logger.error("Failed to encode packet " + packet, e);
            else
                HelmBucket.logger.error("Failed to encode packet " + packet + " E: " + e.getMessage());
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, Packet packet)
    {
        try
        {
            packet.read(source);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                HelmBucket.logger.error("Failed to decode packet " + packet, e);
            else
                HelmBucket.logger.error("Failed to decode packet " + packet + " E: " + e.getMessage());
        }
    }
}

package com.builtbroken.helmbucket;

import com.builtbroken.helmbucket.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
@Mod(modid = "helmbucket", name = "Helm Bucket", version = HelmBucket.VERSION)
public class HelmBucket
{
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static ItemHelmBucket itemHelmBucket;

    /** Information output thing */
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");

    public static PacketManager packetHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        itemHelmBucket = new ItemHelmBucket();
        GameRegistry.registerItem(itemHelmBucket, "hbHelmBucket");

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        packetHandler = new PacketManager("helmbucket");
    }
}

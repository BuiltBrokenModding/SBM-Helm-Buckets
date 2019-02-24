package com.builtbroken.helmbucket;

import net.minecraft.init.Items;
import net.minecraftforge.common.config.Config;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2/24/2019.
 */
@Config(modid = HelmBucket.DOMAIN, name = HelmBucket.DOMAIN)
@Config.LangKey("config." + HelmBucket.PREFIX + "config.main.title")
public class ConfigHelmBucket
{
    @Config.LangKey("config." + HelmBucket.PREFIX + "items")
    @Config.Comment("List of items to support as helm buckets. Restart is required as the fluid lib only generate materials for buckets on load.")
    @Config.Name("item_list")
    @Config.RequiresMcRestart
    public static String[] ITEMS = new String[]{Items.LEATHER_HELMET.getRegistryName().toString(), Items.CHAINMAIL_HELMET.getRegistryName().toString()};

    @Config.LangKey("config." + HelmBucket.PREFIX + "items.ban.list")
    @Config.Comment("Should the item list be used as a list of items to not support. Restart is required as the fluid lib only generate materials for buckets on load.")
    @Config.Name("disallow_list")
    @Config.RequiresMcRestart
    public static boolean BAN_LIST = true;
}

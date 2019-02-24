package com.builtbroken.helmbucket;

import com.builtbroken.mc.fluids.api.reg.BucketMaterialRegistryEvent;
import com.builtbroken.mc.fluids.bucket.BucketMaterialHandler;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.logging.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
@Mod(modid = HelmBucket.DOMAIN, name = "Helm Bucket", version = HelmBucket.VERSION, dependencies = "after:vefluids")
@Mod.EventBusSubscriber(modid = HelmBucket.DOMAIN)
public class HelmBucket
{
    public static final String DOMAIN = "helmbucket";
    public static final String PREFIX = DOMAIN + ":";

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final HashMap<Item, HelmetBucketMaterial> ITEM_TO_MATERIAL = new HashMap();

    public static final Logger logger = Logger.getLogger(DOMAIN);

    @SubscribeEvent
    public static void registerBucketMaterials(BucketMaterialRegistryEvent.Pre event)
    {
        if (ConfigHelmBucket.BAN_LIST)
        {
            Set<ResourceLocation> bannedIDs = new HashSet();
            Arrays.stream(ConfigHelmBucket.ITEMS).map(str -> new ResourceLocation(str)).forEach(rss -> bannedIDs.add(rss));

            //Loop all items looking for those not banned
            Iterator<Item> items = Item.REGISTRY.iterator();
            while (items.hasNext())
            {
                Item item = items.next();
                if (item != null && !bannedIDs.contains(item.getRegistryName()))
                {
                    addBucketType(item);
                }
            }
        }
        else
        {
            //Loop allowed items only
            for (String itemID : ConfigHelmBucket.ITEMS)
            {
                if (!addBucketType(Item.REGISTRY.getObject(new ResourceLocation(itemID))))
                {
                    logger.warning("Invalid item " + itemID + " either it was not found or is not an ItemArmor of type Helm.");
                }
            }
        }
    }

    private static boolean addBucketType(Item item)
    {
        if (item instanceof ItemArmor && ((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD)
        {
            HelmetBucketMaterial material = new HelmetBucketMaterial(new ItemStack(item)); //TODO check for subtypes and NBT
            final String key = "helm_" + item.getRegistryName().toString().replace(":", "_");
            BucketMaterialHandler.addMaterial(key, material);
            ITEM_TO_MATERIAL.put(item, material);
            return true;
        }
        return false;
    }
}

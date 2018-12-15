package com.builtbroken.helmbucket;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegistry {

	public static final ItemHelmBucket helmbucket = new ItemHelmBucket();

	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		public static final Set<Item> ITEMS = new HashSet<>();

		/**
		 * Register this mod's {@link Item}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			registry.register(helmbucket);
			ITEMS.add(helmbucket);
		}

		@SubscribeEvent
		public static void registerItemBlockModels(final ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(helmbucket, 0, new ModelResourceLocation("helmbucket:helmbucketiron"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 1, new ModelResourceLocation("helmbucket:helmbucketiron"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 2, new ModelResourceLocation("helmbucket:helmbucketiron"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 1000, new ModelResourceLocation("helmbucket:helmbucketgold"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 1001, new ModelResourceLocation("helmbucket:helmbucketgold"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 1002, new ModelResourceLocation("helmbucket:helmbucketgold"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 2000, new ModelResourceLocation("helmbucket:helmbucketdiamond"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 2001, new ModelResourceLocation("helmbucket:helmbucketdiamond"));
			ModelLoader.setCustomModelResourceLocation(helmbucket, 2002, new ModelResourceLocation("helmbucket:helmbucketdiamond"));
		}

	}

}

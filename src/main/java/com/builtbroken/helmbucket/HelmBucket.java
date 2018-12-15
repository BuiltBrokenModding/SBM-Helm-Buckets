package com.builtbroken.helmbucket;

import java.io.File;
import java.util.HashMap;

import com.builtbroken.mc.fluids.FluidModule;
import com.builtbroken.mc.fluids.api.reg.BucketMaterialRegistryEvent;
import com.builtbroken.mc.fluids.bucket.BucketMaterial;
import com.builtbroken.mc.fluids.bucket.BucketMaterialHandler;
import com.builtbroken.mc.fluids.mods.BucketHandler;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2017.
 */
@Mod(modid = HelmBucket.DOMAIN, name = "Helm Bucket", version = HelmBucket.VERSION, dependencies = "after:vefluids")
public class HelmBucket
{
	public static final String DOMAIN = "helmbucket";
	public static final String PREFIX = DOMAIN + ":";

	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVISION_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

	public static Configuration config;

	public static boolean PREVENT_HOT_FLUID_USAGE = false;
	public static boolean DAMAGE_BUCKET_WITH_HOT_FLUID = false;
	public static boolean BURN_ENTITY_WITH_HOT_FLUID = true;
	public static boolean ENABLE_FLUID_LEAKING = false;
	public static boolean ALLOW_LEAK_TO_CAUSE_FIRES = true;

	public static int VISCOSITY_TO_IGNORE_LEAKING = 3000;
	public static int AMOUNT_TO_LEAK = 1;
	public static float CHANCE_TO_LEAK = 0.03f;
	public static float LEAK_FIRE_CHANCE = 0.4f;

	private static HashMap<Item, BucketType> validHelmets = new HashMap<Item, BucketType>();

	private static HashMap<String, BucketType> helmetBuckets = new HashMap<String, BucketType>();

	public HelmBucket()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void addHelmet(BucketType type) {
		helmetBuckets.put(type.name, type);
	}

	public static BucketType getHelmet(String name) {
		return helmetBuckets.get(name);
	}


	/** DO NOT TOUCH, add Type instance to helmetBuckets and use the type name as your key **/
	@SubscribeEvent
	public void registerBucketMaterials(BucketMaterialRegistryEvent.Pre event) {
		BucketType iron = new BucketType("helmiron", Items.IRON_HELMET);
		BucketType gold = new BucketType("helmgold", Items.GOLDEN_HELMET);
		BucketType diamond = new BucketType("helmdiamond", Items.DIAMOND_HELMET);
		this.addHelmet(iron);
		this.addHelmet(gold);
		this.addHelmet(diamond);

		int buckets = 0;
		for(String key : helmetBuckets.keySet()) {
			BucketType type = helmetBuckets.get(key);
			System.out.println(key + ": " + type.name + " " + buckets + " = " + type.base.getUnlocalizedName());
			BucketMaterialHandler.addMaterial(type.name, type.material, buckets++ );
			validHelmets.put(type.base, type);
		}
	}

	@SubscribeEvent
	public void onPlayerRightClick(PlayerInteractEvent event)
	{
		Item item = event.getItemStack().getItem();
		ItemStack stack = event.getItemStack();
		EntityPlayer player = event.getEntityPlayer();
		if (validHelmets.keySet().contains(item))
		{
			BucketType type = validHelmets.get(item);
			ActionResult<BlockPos> result = this.onItemRightClickEmpty(event.getWorld(), player, type);
			if(result.getType() == EnumActionResult.SUCCESS) {
				ItemStack result2 = ((com.builtbroken.mc.fluids.bucket.ItemFluidBucket)type.getBucket().getItem()).pickupFluid(player, type.getBucket(), type.material, event.getWorld(), result.getResult());
				ItemStack stack2 = this.addToInventory(stack, player, result2);
				player.setItemStackToSlot(player.getHeldItem(event.getHand()) == event.getEntityPlayer().getHeldItemMainhand() ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, stack2);
				event.setCanceled(true); // Don't switch helmet onto head (default right click)
			}
		} else if(event.getItemStack().getItem() == FluidModule.bucket) {
			
			// TODO: FIX THIS PART!!!
			
			BucketMaterial mat = BucketMaterialHandler.getMaterial(event.getItemStack().getItemDamage());
			BucketType type = null;
			for(BucketType type2 : helmetBuckets.values()) {
				if(type2.material == mat) {
					type = type2;
				}
			}
			if(type != null) {
				Item base = type.base;
				
				System.out.println("Someone is using a " + type.name);
				
				ActionResult<BlockPos> result = this.onItemRightClickFull(event.getWorld(), player, type);
				if(result.getType() == EnumActionResult.SUCCESS) {
					((com.builtbroken.mc.fluids.bucket.ItemFluidBucket)type.getBucket().getItem()).placeFluid(player, stack, event.getWorld(), result.getResult());
					if(!player.capabilities.isCreativeMode)
						player.setItemStackToSlot(player.getHeldItem(event.getHand()) == event.getEntityPlayer().getHeldItemMainhand() ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(base, 1));
				}

				event.setCanceled(true); // Don't allow normal replace code to run
			}
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Helm_Bucket.cfg"));
		config.load();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PREVENT_HOT_FLUID_USAGE = config.getBoolean("PreventHotFluidUsage", "WoodenBucketUsage", PREVENT_HOT_FLUID_USAGE, "Enables settings that attempt to prevent players from wanting to use the bucket for moving hot fluids");
		DAMAGE_BUCKET_WITH_HOT_FLUID = config.getBoolean("DamageBucketWithHotFluid", "WoodenBucketUsage", DAMAGE_BUCKET_WITH_HOT_FLUID, "Will randomly destroy the bucket if it contains hot fluid, lava in other words");
		BURN_ENTITY_WITH_HOT_FLUID = config.getBoolean("BurnPlayerWithHotFluid", "WoodenBucketUsage", BURN_ENTITY_WITH_HOT_FLUID, "Will light the player on fire if the bucket contains a hot fluid, lava in other words");
		ENABLE_FLUID_LEAKING = config.getBoolean("Enable", "Leaking", ENABLE_FLUID_LEAKING, "Allows fluid to slowly leak out of the bucket as a nerf. Requested by Darkosto");
		VISCOSITY_TO_IGNORE_LEAKING = config.getInt("MaxViscosity", "Leaking", VISCOSITY_TO_IGNORE_LEAKING, -1, 10000, "At which point it the flow rate so slow that the leak is plugged, higher values are slower");
		AMOUNT_TO_LEAK = config.getInt("MaxLeakAmount", "Leaking", AMOUNT_TO_LEAK, 0, 10000, "How much can leak from the bucket each time a leak happens, number is max amount and is randomly ranged between 0 - #");
		CHANCE_TO_LEAK = config.getFloat("LeakChance", "Leaking", CHANCE_TO_LEAK, 0f, 1f, "What is the chance that a leak will happen, calculated each tick with high numbers being more often");
		ALLOW_LEAK_TO_CAUSE_FIRES = config.getBoolean("AllowFires", "Leaking", ALLOW_LEAK_TO_CAUSE_FIRES, "If molten fluid leaks, should there be a chance to cause fires?");
		LEAK_FIRE_CHANCE = config.getFloat("FireChance", "Leaking", LEAK_FIRE_CHANCE, 0f, 1f, "How often to cause fire from molten fluids leaking");

		for(String key : helmetBuckets.keySet()) {
			HelmetBucketMaterial mat = helmetBuckets.get(key).material;
			mat.preventHotFluidUsage = PREVENT_HOT_FLUID_USAGE;
			mat.damageBucketWithHotFluid = DAMAGE_BUCKET_WITH_HOT_FLUID;
			mat.burnEntityWithHotFluid = BURN_ENTITY_WITH_HOT_FLUID;
			mat.enableFluidLeaking = ENABLE_FLUID_LEAKING;
			mat.viscosityToIgnoreLeaking = VISCOSITY_TO_IGNORE_LEAKING;
			mat.amountToLeak = AMOUNT_TO_LEAK;
			mat.chanceToLeak = CHANCE_TO_LEAK;
			mat.allowLeakToCauseFires = ALLOW_LEAK_TO_CAUSE_FIRES;
			mat.leakFireChance = LEAK_FIRE_CHANCE;
		}
	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {

	}


	/*
	 * Bucket methods
	 * 
	 * 
	 */


	private static ItemStack addToInventory(ItemStack originalStack, EntityPlayer player, ItemStack newStack)
	{
		if (player.capabilities.isCreativeMode) {
			return originalStack;
		} else {
			originalStack.shrink(1);
			if (originalStack.isEmpty()) {
				return newStack;
			} else {
				if (!player.inventory.addItemStackToInventory(newStack)) {
					player.dropItem(newStack, false);
				}
				return originalStack;
			}
		}
	}

	/* Determines whether liquid can be taken and returns raytrace position */
	public ActionResult<BlockPos> onItemRightClickEmpty(World world, EntityPlayer player, BucketType type)
	{
		final BucketMaterial bucketMaterial = type.material;
		if (bucketMaterial != null)
		{
			RayTraceResult movingobjectposition = this.rayTrace(world, player, true);

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

					if (player.canPlayerEdit(movingobjectposition.getBlockPos(), movingobjectposition.sideHit, type.getBucket()))
					{
						return new ActionResult(EnumActionResult.SUCCESS, movingobjectposition.getBlockPos());
					}

				}
			}
			return new ActionResult(EnumActionResult.PASS, null);
		}
		return new ActionResult(EnumActionResult.FAIL, null);
	}

	/* Determines whether liquid can be placed and returns raytrace position */
	public ActionResult<BlockPos> onItemRightClickFull(World world, EntityPlayer player, BucketType type)
	{
		final BucketMaterial bucketMaterial = type.material;
		if (bucketMaterial != null)
		{
			RayTraceResult movingobjectposition = this.rayTrace(world, player, true);

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

					if (player.canPlayerEdit(movingobjectposition.getBlockPos(), movingobjectposition.sideHit, type.getBucket()))
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

					if (player.canPlayerEdit(blockpos1, movingobjectposition.sideHit, type.getBucket()))
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
		return new ActionResult(EnumActionResult.FAIL, null);
	}

	public RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
	{
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

}

package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.*;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.loot.CopyCourierLetterDataFunction;
import net.joefoxe.hexerei.data.loot.CopyCourierPackageDataFunction;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.custom.*;
import net.joefoxe.hexerei.item.custom.bottles.*;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.BroomEnderSatchelBrushParticlePacket;
import net.joefoxe.hexerei.util.message.OpenOwlCourierDepotNameEditorPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(BuiltInRegistries.ITEM, Hexerei.MOD_ID);


	public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE.location(), Hexerei.MOD_ID);
	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCourierPackageDataFunction>> COPY_PACKAGE_DATA = LOOT_FUNCTION_TYPES.register("copy_package_data", () -> new LootItemFunctionType<>(CopyCourierPackageDataFunction.CODEC));
	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCourierLetterDataFunction>> COPY_LETTER_DATA = LOOT_FUNCTION_TYPES.register("copy_letter_data", () -> new LootItemFunctionType<>(CopyCourierLetterDataFunction.CODEC));
	public static final DeferredHolder<Item, Item> BOOK_OF_SHADOWS = ITEMS.register("book_of_shadows",
			() -> new HexereiBookItem(new Item.Properties().stacksTo(1)));


	public static final DeferredHolder<Item, Item> MAHOGANY_BROOM = ITEMS.register("mahogany_broom",
			() -> new BroomItem("mahogany", new Item.Properties().stacksTo(1).fireResistant()) {

				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION));
					this.outter_model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/mahogany_broom.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/mahogany_broom.png");
				}
			});

	public static final DeferredHolder<Item, Item> WILLOW_BROOM = ITEMS.register("willow_broom",
			() -> new BroomItem("willow", new Item.Properties().stacksTo(1)) {

				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION));
					this.outter_model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/willow_broom.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/willow_broom.png");
				}
			});

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_BROOM = ITEMS.register("witch_hazel_broom",
			() -> new BroomItem("witch_hazel", new Item.Properties().stacksTo(1)) {

				@Override
				public Vec3 getBrushOffset() {
					return new Vec3(0, 0, 0.025f);
				}

				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new WitchHazelBroomStickModel(context.bakeLayer(WitchHazelBroomStickModel.LAYER_LOCATION));
					this.outter_model = new WitchHazelBroomStickModel(context.bakeLayer(WitchHazelBroomStickModel.POWER_LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/witch_hazel_broom.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/witch_hazel_broom.png");
				}
			});

//    public static final DeferredHolder<Item, Item> FIRE_TABLET = ITEMS.register("fire_tablet",
//            () -> new FireTabletItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> WHISTLE = ITEMS.register("broom_whistle",
			() -> new WhistleItem(new Item.Properties().durability(100)));

	public static final DeferredHolder<Item, Item> WAX_BLEND = ITEMS.register("wax_blend",
			() -> new WaxBlendItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> CLOTH = ITEMS.register("cloth",
			() -> new CleaningClothItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> WAXING_KIT = ITEMS.register("waxing_kit",
			() -> new WaxingKitItem(new Item.Properties().stacksTo(1), false));

	public static final DeferredHolder<Item, Item> CREATIVE_WAXING_KIT = ITEMS.register("creative_waxing_kit",
			() -> new WaxingKitItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), true));

	public static final DeferredHolder<Item, Item> WILLOW_BOAT = ITEMS.register("willow_boat",
			() -> new ModBoatItem(false, ModBoatEntity.Type.WILLOW, new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_BOAT = ITEMS.register("polished_willow_boat",
			() -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_WILLOW, new Item.Properties()));

	public static final DeferredHolder<Item, Item> MAHOGANY_BOAT = ITEMS.register("mahogany_boat",
			() -> new ModBoatItem(false, ModBoatEntity.Type.MAHOGANY, new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_BOAT = ITEMS.register("polished_mahogany_boat",
			() -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_MAHOGANY, new Item.Properties()));

	public static final DeferredHolder<Item, Item> WILLOW_CHEST_BOAT = ITEMS.register("willow_chest_boat",
			() -> new ModChestBoatItem(false, ModChestBoatEntity.Type.WILLOW, new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_CHEST_BOAT = ITEMS.register("polished_willow_chest_boat",
			() -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_WILLOW, new Item.Properties()));

	public static final DeferredHolder<Item, Item> MAHOGANY_CHEST_BOAT = ITEMS.register("mahogany_chest_boat",
			() -> new ModChestBoatItem(false, ModChestBoatEntity.Type.MAHOGANY, new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_CHEST_BOAT = ITEMS.register("polished_mahogany_chest_boat",
			() -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_MAHOGANY, new Item.Properties()));


	public static final DeferredHolder<Item, Item> SMALL_SATCHEL = ITEMS.register("small_satchel",
			() -> new SatchelItem(new Item.Properties()) {

				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomSmallSatchelModel(context.bakeLayer(BroomSmallSatchelModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_small_satchel.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_small_satchel_dye.png");
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.small_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else  {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> MEDIUM_SATCHEL = ITEMS.register("medium_satchel",
			() -> new SatchelItem(new Item.Properties()) {

				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_satchel.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_satchel_dye.png");
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

			});

	public static final DeferredHolder<Item, Item> LARGE_SATCHEL = ITEMS.register("large_satchel",
			() -> new SatchelItem(new Item.Properties()) {


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomLargeSatchelModel(context.bakeLayer(BroomLargeSatchelModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_large_satchel.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_large_satchel_dye.png");
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.large_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}


			});


	public static final DeferredHolder<Item, Item> ENDER_SATCHEL = ITEMS.register("ender_satchel",
			() -> new SatchelItem(new Item.Properties()) {


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_ender_satchel.png");
					this.dye_texture = null;
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.ender_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

			});

	public static final DeferredHolder<Item, Item> REPLACER_SATCHEL = ITEMS.register("replacer_satchel",
			() -> new SatchelItem(new Item.Properties()) {


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_replacer_satchel.png");
					this.dye_texture = null;
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.replacer_satchel_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.replacer_satchel_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

				@Override
				public void onBrushDamage(BroomEntity broom, RandomSource random) {
					if (!(broom.getModule(BroomEntity.BroomSlot.BRUSH).isEmpty())) return;
					int extraBrushSlot = broom.getExtraBrush();
					if (extraBrushSlot != -1) {
						broom.setModule(BroomEntity.BroomSlot.BRUSH, broom.itemHandler.getStackInSlot(extraBrushSlot).copy());
						broom.itemHandler.setStackInSlot(extraBrushSlot, ItemStack.EMPTY);

						broom.level().playSound(null, broom, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
						HexereiPacketHandler.sendToNearbyClient(broom.level(), broom, new BroomEnderSatchelBrushParticlePacket(broom.getId()));

						broom.sync();
					} else {
						broom.level().playSound(null, broom, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
					}
				}
			});


	public static final DeferredHolder<Item, Item> BROOM_SEAT = ITEMS.register("broom_seat",
			() -> new BroomSeatItem(new Item.Properties()) {


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomSeatModel(context.bakeLayer(BroomSeatModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_seat.png");
					this.dye_texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_seat_dye.png");
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_seat_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_seat_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

			});

	public static final DeferredHolder<Item, Item> GOLD_RINGS = ITEMS.register("gold_rings",
			() -> new BroomAttachmentItem(new Item.Properties()) {


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomRingsModel(context.bakeLayer(BroomRingsModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom.png");
					this.dye_texture = null;
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> BROOM_NETHERITE_TIP = ITEMS.register("broom_netherite_tip",
			() -> new BroomAttachmentItem(new Item.Properties().durability(200)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.BROOM_NETHERITE_TIP_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomNetheriteTipModel(context.bakeLayer(BroomNetheriteTipModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_netherite_tip.png");
					this.dye_texture = null;
				}

				@Override
				public boolean shouldRenderParticles(BroomEntity broom, Level world, BroomEntity.Status status) {
					return status == BroomEntity.Status.UNDER_WATER || status == BroomEntity.Status.UNDER_FLOWING_WATER;
				}


				@Override
				public void renderParticles(BroomEntity broom, Level world, BroomEntity.Status status, RandomSource random) {

					if (random.nextInt(2) == 0) {
						float rotOffset = random.nextFloat() * 10 - 5;
						world.addParticle(ParticleTypes.SMALL_FLAME, broom.getX() - Math.sin(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), broom.getY() + broom.floatingOffset + 0.25f * random.nextFloat() - broom.getDeltaMovement().y(), broom.getZ() + Math.cos(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d);
					}
					if (random.nextInt(2) == 0) {
						float rotOffset = random.nextFloat() * 10 - 5;
						world.addParticle(ParticleTypes.SMOKE, broom.getX() - Math.sin(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), broom.getY() + broom.floatingOffset + 0.25f * random.nextFloat() - broom.getDeltaMovement().y(), broom.getZ() + Math.cos(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d);
					}

				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_netherite_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> BROOM_WATERPROOF_TIP = ITEMS.register("broom_waterproof_tip",
			() -> new BroomAttachmentItem(new Item.Properties().durability(800)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.BROOM_WATERPROOF_TIP_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomWaterproofTipModel(context.bakeLayer(BroomWaterproofTipModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom_waterproof_tip.png");
					this.dye_texture = null;
				}

				@Override
				public boolean shouldRenderParticles(BroomEntity broom, Level world, BroomEntity.Status status) {
					return status == BroomEntity.Status.UNDER_WATER || status == BroomEntity.Status.UNDER_FLOWING_WATER;
				}


				@Override
				public void renderParticles(BroomEntity broom, Level world, BroomEntity.Status status, RandomSource random) {
					if (random.nextInt(2) == 0) {
						float rotOffset = random.nextFloat() * 10 - 5;
						world.addParticle(ParticleTypes.BUBBLE, broom.getX() - Math.sin(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), broom.getY() + broom.floatingOffset + 0.25f * random.nextFloat() - broom.getDeltaMovement().y(), broom.getZ() + Math.cos(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d);
					}
					if (random.nextInt(2) == 0) {
						float rotOffset = random.nextFloat() * 10 - 5;
						world.addParticle(ParticleTypes.BUBBLE_POP, broom.getX() - Math.sin(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), broom.getY() + broom.floatingOffset + 0.25f * random.nextFloat() - broom.getDeltaMovement().y(), broom.getZ() + Math.cos(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * Math.PI) * (1.25f + broom.getDeltaMovement().length() / 4), (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d);
					}
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_waterproof_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> BROOM_KEYCHAIN = ITEMS.register("broom_keychain",
			() -> new KeychainItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> BROOM_KEYCHAIN_BASE = ITEMS.register("broom_keychain_base",
			() -> new Item(new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("the base is not for use, see the broom keychain.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WET_BROOM_BRUSH = ITEMS.register("wet_broom_brush",
			() -> new Item(new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> BROOM_BRUSH = ITEMS.register("broom_brush",
			() -> new BroomBrushItem(new Item.Properties().durability(100)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.BROOM_BRUSH_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/broom.png");
					this.dye_texture = null;
					this.list = new ArrayList<>();
					this.list.add(new Tuple<>(ModParticleTypes.BROOM.get(), 5));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_2.get(), 2));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_3.get(), 8));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_4.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_5.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_6.get(), 50));
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> BROOM_THRUSTER_BRUSH = ITEMS.register("broom_thruster_brush",
			() -> new BroomBrushItem(new Item.Properties().durability(400)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.THRUSTER_BRUSH_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomThrusterBrushModel(context.bakeLayer(BroomThrusterBrushModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/thruster_brush.png");
					this.dye_texture = null;
					this.list = new ArrayList<>();
					this.list.add(new Tuple<>(ParticleTypes.SMALL_FLAME, 5));
					this.list.add(new Tuple<>(ParticleTypes.FLAME, 2));
					this.list.add(new Tuple<>(ParticleTypes.SMOKE, 8));
					this.list.add(new Tuple<>(ParticleTypes.LARGE_SMOKE, 50));
				}

				@Override //TODO tester
				public float getSpeedModifier() {
					return 1.5F;
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WET_MOON_DUST_BRUSH = ITEMS.register("wet_moon_dust_brush",
			() -> new Item(new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> MOON_DUST_BRUSH = ITEMS.register("moon_dust_brush",
			() -> new BroomBrushItem(new Item.Properties().durability(200)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.MOON_DUST_BRUSH_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new MoonDustBrushModel(context.bakeLayer(MoonDustBrushModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/moon_dust_brush.png");
					this.dye_texture = null;
					this.list = new ArrayList<>();
					this.list.add(new Tuple<>(ModParticleTypes.STAR_BRUSH.get(), 5));
					this.list.add(new Tuple<>(ParticleTypes.ELECTRIC_SPARK, 2));
					this.list.add(new Tuple<>(ModParticleTypes.MOON_BRUSH_1.get(), 8));
					this.list.add(new Tuple<>(ModParticleTypes.MOON_BRUSH_2.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.MOON_BRUSH_3.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.MOON_BRUSH_4.get(), 50));
				}

				@Override
				public boolean shouldGlow(Level level, ItemStack brushStack) {
					float time = level.getTimeOfDay(0);
					return time > 0.25f && time < 0.75f && level.getMoonPhase() == 0 && !level.dimensionType().hasFixedTime();
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.moon_dust_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

				@Override
				public float getSpeedModifier(BroomEntity broom) {
					float time = broom.level().getTimeOfDay(0);
					if (time > 0.25f && time < 0.75f && broom.level().getMoonPhase() == 0 && !broom.level().dimensionType().hasFixedTime())
						return 1.0F;
					return 0.25f;
				}
			});

	public static final DeferredHolder<Item, Item> WET_HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("wet_herb_enhanced_broom_brush",
			() -> new Item(new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> HERB_ENHANCED_BROOM_BRUSH = ITEMS.register("herb_enhanced_broom_brush",
			() -> new BroomBrushItem(new Item.Properties().durability(200)) {

				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.HERB_ENHANCED_BRUSH_DURABILITY.get();
				}


				@OnlyIn(Dist.CLIENT)
				@Override
				public void bakeModels() {
					EntityModelSet context = Minecraft.getInstance().getEntityModels();
					this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
					this.texture = ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, "textures/entity/herb_enhanced_brush.png");
					this.dye_texture = null;
					this.list = new ArrayList<>();
					this.list.add(new Tuple<>(ModParticleTypes.BROOM.get(), 5));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_2.get(), 2));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_3.get(), 8));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_4.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_5.get(), 50));
					this.list.add(new Tuple<>(ModParticleTypes.BROOM_6.get(), 50));
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WARHAMMER = ITEMS.register("warhammer",
			() -> new SwordItem(Tiers.NETHERITE,
					new Item.Properties().attributes(SwordItem.createAttributes(Tiers.WOOD, 3, -2.4F))));

	public static final DeferredHolder<Item, Item> BLOOD_BUCKET = ITEMS.register("blood_bucket",
			() -> new BucketItem(ModFluids.BLOOD_FLUID.value(), new Item.Properties().stacksTo(1)));

	public static final DeferredHolder<Item, Item> TALLOW_BUCKET = ITEMS.register("tallow_bucket",
			() -> new BucketItem(ModFluids.TALLOW_FLUID.value(), new Item.Properties().stacksTo(1)));

	public static final DeferredHolder<Item, Item> QUICKSILVER_BUCKET = ITEMS.register("quicksilver_bucket",
			() -> new BucketItem(ModFluids.QUICKSILVER_FLUID.value(), new Item.Properties().stacksTo(1)));

	public static final DeferredHolder<Item, Item> QUICKSILVER_BOTTLE = ITEMS.register("quicksilver_bottle",
			() -> new BottleQuicksilverItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> BLOOD_BOTTLE = ITEMS.register("blood_bottle",
			() -> new BottleBloodtem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> TALLOW_BOTTLE = ITEMS.register("tallow_bottle",
			() -> new BottleTallowItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> LAVA_BOTTLE = ITEMS.register("lava_bottle",
			() -> new BottleLavaItem(new Item.Properties().durability(100)));

	public static final DeferredHolder<Item, Item> MILK_BOTTLE = ITEMS.register("milk_bottle",
			() -> new BottleMilkItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> BLOOD_SIGIL = ITEMS.register("blood_sigil",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> ANIMAL_FAT = ITEMS.register("animal_fat",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> TALLOW_IMPURITY = ITEMS.register("tallow_impurity",
			() -> new TallowImpurityItem(new Item.Properties()));


	public static final DeferredHolder<Item, Item> INFUSED_FABRIC = ITEMS.register("infused_fabric",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> SELENITE_SHARD = ITEMS.register("selenite_shard",
			() -> new Item(new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.selenite_shard").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> SAGE = ITEMS.register("sage",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> SAGE_SEED = ITEMS.register("sage_seed",
			() -> new ItemNameBlockItem(ModBlocks.SAGE.get(), new Item.Properties()
					//.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).fastToEat().build())
					) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

					tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_seeds").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> SAGE_BUNDLE = ITEMS.register("sage_bundle",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_SAGE_BUNDLE = ITEMS.register("dried_sage_bundle",
			() -> new Item(new Item.Properties().durability(3600)) {
				@Override
				public int getMaxDamage(ItemStack stack) {
					return HexConfig.SAGE_BUNDLE_DURATION.get();
				}

				@Override
				public boolean isEnchantable(ItemStack p_41456_) {
					return false;
				}

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

						int duration = stack.getMaxDamage() - stack.getDamageValue();
						float percentDamaged = stack.getDamageValue() / (float) stack.getMaxDamage();
						int minutes = duration / 60;
						int seconds = duration % 60;
						ChatFormatting col = ChatFormatting.GREEN;
						MutableComponent component = Component.literal("");

						if (percentDamaged > 0.4f) {
							col = ChatFormatting.DARK_GREEN;
						}
						if (percentDamaged > 0.60f) {
							col = ChatFormatting.YELLOW;
						}
						if (percentDamaged > 0.70f) {
							col = ChatFormatting.GOLD;
						}
						if (percentDamaged > 0.85f) {
							col = ChatFormatting.RED;
						}
						if (percentDamaged > 0.95f) {
							col = ChatFormatting.DARK_RED;
						}
						MutableComponent minutesText = Component.translatable("tooltip.hexerei.minutes").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
						MutableComponent minuteText = Component.translatable("tooltip.hexerei.minute").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
						MutableComponent secondsText = Component.translatable("tooltip.hexerei.seconds").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
						MutableComponent secondText = Component.translatable("tooltip.hexerei.second").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));

						if (minutes > 1) {
							component.append(String.valueOf(minutes)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(minutesText);
							if (seconds >= 1) {
								component.append(" ");
								if (seconds > 1) {
									component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondsText);
								} else {
									component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondText);
								}
							}
						} else if (minutes == 1) {
							component.append(String.valueOf(minutes)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(minuteText);
							if (seconds >= 1) {
								component.append(" ");
								if (seconds > 1) {
									component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondsText);
								} else {
									component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondText);
								}
							}
						}

						MutableComponent itemText = Component.translatable(ModBlocks.SAGE_BURNING_PLATE.get().getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

						tooltipComponents.add(Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_1", component).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_2", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}

					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> LILY_PAD_ITEM = ITEMS.register("flowering_lily_pad",
			() -> new FloweringLilyPadItem(ModBlocks.LILY_PAD_BLOCK.get(), new Item.Properties())); // ModBlocks.LILY_PAD_BLOCK.get(),


	public static final DeferredHolder<Item, FlowerOutputItem> BELLADONNA_FLOWERS = ITEMS.register("belladonna_flowers",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> BELLADONNA_BERRIES = ITEMS.register("belladonna_berries",
			() -> new FlowerOutputItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1f).fast().effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 2), 100f).build())));

	public static final DeferredHolder<Item, FlowerOutputItem> MANDRAKE_FLOWERS = ITEMS.register("mandrake_flowers",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> MANDRAKE_ROOT = ITEMS.register("mandrake_root",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> MUGWORT_FLOWERS = ITEMS.register("mugwort_flowers",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> MUGWORT_LEAVES = ITEMS.register("mugwort_leaves",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> YELLOW_DOCK_FLOWERS = ITEMS.register("yellow_dock_flowers",
			() -> new FlowerOutputItem(new Item.Properties()));

	public static final DeferredHolder<Item, FlowerOutputItem> YELLOW_DOCK_LEAVES = ITEMS.register("yellow_dock_leaves",
			() -> new FlowerOutputItem(new Item.Properties()));


	public static final DeferredHolder<Item, Item> DRIED_SAGE = ITEMS.register("dried_sage",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_BELLADONNA_FLOWERS = ITEMS.register("dried_belladonna_flowers",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_MANDRAKE_FLOWERS = ITEMS.register("dried_mandrake_flowers",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_MUGWORT_FLOWERS = ITEMS.register("dried_mugwort_flowers",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_MUGWORT_LEAVES = ITEMS.register("dried_mugwort_leaves",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_YELLOW_DOCK_FLOWERS = ITEMS.register("dried_yellow_dock_flowers",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DRIED_YELLOW_DOCK_LEAVES = ITEMS.register("dried_yellow_dock_leaves",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, BlendItem> MINDFUL_TRANCE_BLEND = ITEMS.register("mindful_trance_blend",
			() -> new BlendItem(new Item.Properties()));


	public static final DeferredHolder<Item, DowsingRodItem> DOWSING_ROD = ITEMS.register("dowsing_rod",
			() -> new DowsingRodItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> SEED_MIXTURE = ITEMS.register("seed_mixture",
			() -> new SeedMixtureItem(new Item.Properties()));


	public static final DeferredHolder<Item, Item> CROW_FLUTE = ITEMS.register("crow_flute",
			() -> new CrowFluteItem(new Item.Properties().component(ModDataComponents.FLUTE, FluteData.EMPTY)));


	public static final DeferredHolder<Item, DeferredSpawnEggItem> CROW_SPAWN_EGG = ITEMS.register("crow_spawn_egg",
			() -> new DeferredSpawnEggItem(ModEntityTypes.CROW, 0x161616, 0x333333,
					new Item.Properties()));

	public static final DeferredHolder<Item, DeferredSpawnEggItem> OWL_SPAWN_EGG = ITEMS.register("owl_spawn_egg",
			() -> new DeferredSpawnEggItem(ModEntityTypes.OWL, 0x4B3822, 0xCAB18F,
					new Item.Properties()));


	public static final DeferredHolder<Item, Item> CROW_ANKH_AMULET = ITEMS.register("crow_ankh_amulet",
			() -> new Item(new Item.Properties().stacksTo(1)) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET = ITEMS.register("crow_blank_amulet",
			() -> new CrowAmuletItem(new Item.Properties().stacksTo(1)));

	public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET_TRINKET = ITEMS.register("crow_blank_amulet_trinket",
			() -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET_TRINKET_FRAME = ITEMS.register("crow_blank_amulet_trinket_frame",
			() -> new Item(new Item.Properties()));


	public static final DeferredHolder<Item, GlassesItem> READING_GLASSES = ITEMS.register("reading_glasses",
			() -> new GlassesItem(new Item.Properties()));

	public static final DeferredHolder<Item, Item> MOON_DUST = ITEMS.register("moon_dust",
			() -> new Item(new Item.Properties()));


	// EGG ITEMS

//    public static final DeferredHolder<Item, ModSpawnEggItem> CROW_SPAWN_EGG = ITEMS.register("crow_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntityTypes.CROW, 0x161616, 0x333333,
//                    new Item.Properties()));

//    public static final DeferredHolder<Item, ModSpawnEggItem> PIGEON_SPAWN_EGG = ITEMS.register("pigeon_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntityTypes.PIGEON, 0x879995, 0x576ABC,
//                    new Item.Properties()));


	// ARMOR ITEMS
//    public static final DeferredHolder<Item, Item> ORC_HELMET = ITEMS.register("orc_helmet",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.HEAD,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> ORC_CHESTPLATE = ITEMS.register("orc_chestplate",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.CHEST,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> ORC_LEGGINGS = ITEMS.register("orc_leggings",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> ORC_BOOTS = ITEMS.register("orc_boots",
//            () -> new OrcArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.FEET,
//                    new Item.Properties()));

//    public static final DeferredHolder<Item, Item> DRUID_HELMET = ITEMS.register("druid_helmet",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.HEAD,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> DRUID_CHESTPLATE = ITEMS.register("druid_chestplate",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.CHEST,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> DRUID_LEGGINGS = ITEMS.register("druid_leggings",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties()));
//
//    public static final DeferredHolder<Item, Item> DRUID_BOOTS = ITEMS.register("druid_boots",
//            () -> new DruidArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.FEET,
//                    new Item.Properties()));

	public static final DeferredHolder<Item, Item> WITCH_HELMET = ITEMS.register("witch_helmet",
			() -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, ArmorItem.Type.HELMET,
					new Item.Properties()));

	public static final DeferredHolder<Item, Item> WITCH_CHESTPLATE = ITEMS.register("witch_chestplate",
			() -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, ArmorItem.Type.CHESTPLATE,
					new Item.Properties()));

//    public static final DeferredHolder<Item, Item> WITCH_LEGGINGS = ITEMS.register("witch_leggings",
//            () -> new WitchArmorItem(ModArmorMaterial.ARMOR_SCRAP, EquipmentSlot.LEGS,
//                    new Item.Properties()));

	public static final DeferredHolder<Item, Item> WITCH_BOOTS = ITEMS.register("witch_boots",
			() -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, ArmorItem.Type.BOOTS,
					new Item.Properties()));


	public static final DeferredHolder<Item, Item> MUSHROOM_WITCH_HAT = ITEMS.register("mushroom_witch_hat",
			() -> new MushroomWitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, ArmorItem.Type.HELMET,
					new Item.Properties()));



	public static final DeferredHolder<Item, Item> WILLOW_SIGN = ITEMS.register("willow_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.WILLOW_SIGN.get(), ModBlocks.WILLOW_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_SIGN = ITEMS.register("witch_hazel_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.WITCH_HAZEL_SIGN.get(), ModBlocks.WITCH_HAZEL_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> MAHOGANY_SIGN = ITEMS.register("mahogany_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.MAHOGANY_SIGN.get(), ModBlocks.MAHOGANY_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_SIGN = ITEMS.register("polished_willow_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.POLISHED_WILLOW_SIGN.get(), ModBlocks.POLISHED_WILLOW_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_SIGN = ITEMS.register("polished_witch_hazel_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.POLISHED_WITCH_HAZEL_SIGN.get(), ModBlocks.POLISHED_WITCH_HAZEL_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_SIGN = ITEMS.register("polished_mahogany_sign",
			() -> new SignItem(new Item.Properties(), ModBlocks.POLISHED_MAHOGANY_SIGN.get(), ModBlocks.POLISHED_MAHOGANY_WALL_SIGN.get()));

	public static final DeferredHolder<Item, Item> WILLOW_HANGING_SIGN = ITEMS.register("willow_hanging_sign",
			() -> new HangingSignItem(ModBlocks.WILLOW_HANGING_SIGN.get(), ModBlocks.WILLOW_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_HANGING_SIGN = ITEMS.register("witch_hazel_hanging_sign",
			() -> new HangingSignItem(ModBlocks.WITCH_HAZEL_HANGING_SIGN.get(), ModBlocks.WITCH_HAZEL_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

	public static final DeferredHolder<Item, Item> MAHOGANY_HANGING_SIGN = ITEMS.register("mahogany_hanging_sign",
			() -> new HangingSignItem(ModBlocks.MAHOGANY_HANGING_SIGN.get(), ModBlocks.MAHOGANY_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

	public static final DeferredHolder<Item, Item> WILLOW_CHEST = ITEMS.register("willow_chest",
			() -> new ModChestItem(ModBlocks.WILLOW_CHEST.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_CHEST = ITEMS.register("witch_hazel_chest",
			() -> new ModChestItem(ModBlocks.WITCH_HAZEL_CHEST.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> MAHOGANY_CHEST = ITEMS.register("mahogany_chest",
			() -> new ModChestItem(ModBlocks.MAHOGANY_CHEST.get(), new Item.Properties()));



	public static final DeferredHolder<Item, Item> MIXING_CAULDRON = ITEMS.register("mixing_cauldron",
			() -> new MixingCauldronItem(ModBlocks.MIXING_CAULDRON.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> COFFER = ITEMS.register("coffer",
			() -> new CofferItem(ModBlocks.COFFER.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> HERB_JAR = ITEMS.register("herb_jar",
			() -> new HerbJarItem(ModBlocks.HERB_JAR.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> CANDLE = ITEMS.register("candle",
			() -> new CandleItem(ModBlocks.CANDLE.get(), new Item.Properties()));



	public static final DeferredHolder<Item, Item> PACKING_PEANUT = ITEMS.register("packing_peanut",
			() -> new Item(new Item.Properties().food(new FoodProperties.Builder().saturationModifier(1).nutrition(1).alwaysEdible().build())));
	public static final DeferredHolder<Item, Item> COURIER_PACKAGE = ITEMS.register("courier_package",
			() -> new CourierPackageItem(ModBlocks.COURIER_PACKAGE.get(), new Item.Properties()) {

			});
	public static final DeferredHolder<Item, Item> COURIER_LETTER = ITEMS.register("courier_letter",
			() -> new CourierLetterItem(ModBlocks.COURIER_LETTER.get(), new Item.Properties()) {

			});

	public static final DeferredHolder<Item, StandingAndWallBlockItem> WILLOW_COURIER_DEPOT = ITEMS.register("willow_courier_depot",
			() -> new StandingAndWallBlockItem(ModBlocks.WILLOW_COURIER_DEPOT.get(), ModBlocks.WILLOW_COURIER_DEPOT_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}


				@Override
				protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
					boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
					if (!pLevel.isClientSide && !flag && pPlayer != null) {
						BlockEntity blockentity = pLevel.getBlockEntity(pPos);
						if (blockentity instanceof OwlCourierDepotTile) {
							Block block = pLevel.getBlockState(pPos).getBlock();
							if (block instanceof OwlCourierDepot depotBlock && pPlayer instanceof ServerPlayer serverPlayer) {
								HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
							}
						}
					}

					return flag;
				}
			});

	public static final DeferredHolder<Item, StandingAndWallBlockItem> MAHOGANY_COURIER_DEPOT = ITEMS.register("mahogany_courier_depot",
			() -> new StandingAndWallBlockItem(ModBlocks.MAHOGANY_COURIER_DEPOT.get(), ModBlocks.MAHOGANY_COURIER_DEPOT_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}


				@Override
				protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
					boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
					if (!pLevel.isClientSide && !flag && pPlayer != null) {
						BlockEntity blockentity = pLevel.getBlockEntity(pPos);
						if (blockentity instanceof OwlCourierDepotTile) {
							Block block = pLevel.getBlockState(pPos).getBlock();
							if (block instanceof OwlCourierDepot depotBlock && pPlayer instanceof ServerPlayer serverPlayer) {
								HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
							}
						}
					}

					return flag;
				}
			});

	public static final DeferredHolder<Item, StandingAndWallBlockItem> WITCH_HAZEL_COURIER_DEPOT = ITEMS.register("witch_hazel_courier_depot",
			() -> new StandingAndWallBlockItem(ModBlocks.WITCH_HAZEL_COURIER_DEPOT.get(), ModBlocks.WITCH_HAZEL_COURIER_DEPOT_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}


				@Override
				protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
					boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
					if (!pLevel.isClientSide && !flag && pPlayer != null) {
						BlockEntity blockentity = pLevel.getBlockEntity(pPos);
						if (blockentity instanceof OwlCourierDepotTile) {
							Block block = pLevel.getBlockState(pPos).getBlock();
							if (block instanceof OwlCourierDepot depotBlock && pPlayer instanceof ServerPlayer serverPlayer) {
								HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
							}
						}
					}

					return flag;
				}
			});


	public static final DeferredHolder<Item, StandingAndWallBlockItem> MAHOGANY_BROOM_STAND = ITEMS.register("mahogany_broom_stand",
			() -> new StandingAndWallBlockItem(ModBlocks.MAHOGANY_BROOM_STAND.get(), ModBlocks.MAHOGANY_BROOM_STAND_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}
			});


	public static final DeferredHolder<Item, StandingAndWallBlockItem> WILLOW_BROOM_STAND = ITEMS.register("willow_broom_stand",
			() -> new StandingAndWallBlockItem(ModBlocks.WILLOW_BROOM_STAND.get(), ModBlocks.WILLOW_BROOM_STAND_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}
			});


	public static final DeferredHolder<Item, StandingAndWallBlockItem> WITCH_HAZEL_BROOM_STAND = ITEMS.register("witch_hazel_broom_stand",
			() -> new StandingAndWallBlockItem(ModBlocks.WITCH_HAZEL_BROOM_STAND.get(), ModBlocks.WITCH_HAZEL_BROOM_STAND_WALL.get(), (new Item.Properties()), Direction.DOWN) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					if (Screen.hasShiftDown()) {
						tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
						tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					} else {
						tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					}
				}
			});


	public static final DeferredHolder<Item, Item> STONE_WINDOW_PANE = ITEMS.register("stone_window_pane",
			() -> new BlockItem(ModBlocks.STONE_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> STONE_WINDOW = ITEMS.register("stone_window",
			() -> new BlockItem(ModBlocks.STONE_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_STONE_WINDOW_PANE = ITEMS.register("waxed_stone_window_pane",
			() -> new BlockItem(ModBlocks.WAXED_STONE_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_STONE_WINDOW = ITEMS.register("waxed_stone_window",
			() -> new BlockItem(ModBlocks.WAXED_STONE_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> MAHOGANY_WINDOW_PANE = ITEMS.register("mahogany_window_pane",
			() -> new BlockItem(ModBlocks.MAHOGANY_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WILLOW_WINDOW_PANE = ITEMS.register("willow_window_pane",
			() -> new BlockItem(ModBlocks.WILLOW_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_WINDOW_PANE = ITEMS.register("witch_hazel_window_pane",
			() -> new BlockItem(ModBlocks.WITCH_HAZEL_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_WINDOW_PANE = ITEMS.register("waxed_mahogany_window_pane",
			() -> new BlockItem(ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WILLOW_WINDOW_PANE = ITEMS.register("waxed_willow_window_pane",
			() -> new BlockItem(ModBlocks.WAXED_WILLOW_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_WINDOW_PANE = ITEMS.register("waxed_witch_hazel_window_pane",
			() -> new BlockItem(ModBlocks.WAXED_WITCH_HAZEL_WINDOW_PANE.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> MAHOGANY_WINDOW = ITEMS.register("mahogany_window",
			() -> new BlockItem(ModBlocks.MAHOGANY_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WILLOW_WINDOW = ITEMS.register("willow_window",
			() -> new BlockItem(ModBlocks.WILLOW_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WITCH_HAZEL_WINDOW = ITEMS.register("witch_hazel_window",
			() -> new BlockItem(ModBlocks.WITCH_HAZEL_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_WINDOW = ITEMS.register("waxed_mahogany_window",
			() -> new BlockItem(ModBlocks.WAXED_MAHOGANY_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WILLOW_WINDOW = ITEMS.register("waxed_willow_window",
			() -> new BlockItem(ModBlocks.WAXED_WILLOW_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_WINDOW = ITEMS.register("waxed_witch_hazel_window",
			() -> new BlockItem(ModBlocks.WAXED_WITCH_HAZEL_WINDOW.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});



	public static final DeferredHolder<Item, Item> INFUSED_FABRIC_CARPET_ORNATE = ITEMS.register("infused_fabric_carpet_ornate",
			() -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_CARPET_ORNATE = ITEMS.register("waxed_infused_fabric_carpet_ornate",
			() -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> INFUSED_FABRIC_BLOCK_ORNATE = ITEMS.register("infused_fabric_block_ornate",
			() -> new BlockItem(ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_BLOCK_ORNATE = ITEMS.register("waxed_infused_fabric_block_ornate",
			() -> new BlockItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_ORNATE.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> INFUSED_FABRIC_CARPET = ITEMS.register("infused_fabric_carpet",
			() -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_CARPET.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

			});

	public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_CARPET = ITEMS.register("waxed_infused_fabric_carpet",
			() -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get(), new Item.Properties()) {

				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}

			});

	public static final DeferredHolder<Item, Item> INFUSED_FABRIC_BLOCK = ITEMS.register("infused_fabric_block",
			() -> new DyeableCarpetItem(ModBlocks.INFUSED_FABRIC_BLOCK.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_BLOCK = ITEMS.register("waxed_infused_fabric_block",
			() -> new DyeableCarpetItem(ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
//
	public static final DeferredHolder<Item, Item> WILLOW_CONNECTED = ITEMS.register("willow_connected",
			() -> new BlockItem(ModBlocks.WILLOW_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
//
	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_CONNECTED = ITEMS.register("polished_willow_connected",
			() -> new BlockItem(ModBlocks.POLISHED_WILLOW_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
//
//
	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_PILLAR = ITEMS.register("polished_willow_pillar",
			() -> new BlockItem(ModBlocks.POLISHED_WILLOW_PILLAR.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_LAYERED = ITEMS.register("polished_willow_layered",
			() -> new BlockItem(ModBlocks.POLISHED_WILLOW_LAYERED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> WITCH_HAZEL_CONNECTED = ITEMS.register("witch_hazel_connected",
			() -> new BlockItem(ModBlocks.WITCH_HAZEL_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_CONNECTED = ITEMS.register("polished_witch_hazel_connected",
			() -> new BlockItem(ModBlocks.POLISHED_WITCH_HAZEL_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_PILLAR = ITEMS.register("polished_witch_hazel_pillar",
			() -> new BlockItem(ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_LAYERED = ITEMS.register("polished_witch_hazel_layered",
			() -> new BlockItem(ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_CONNECTED = ITEMS.register("waxed_polished_mahogany_connected",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_PILLAR = ITEMS.register("waxed_polished_mahogany_pillar",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_LAYERED = ITEMS.register("waxed_polished_mahogany_layered",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_CONNECTED = ITEMS.register("waxed_mahogany_connected",
			() -> new BlockItem(ModBlocks.WAXED_MAHOGANY_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_CONNECTED = ITEMS.register("waxed_polished_willow_connected",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
//
	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_PILLAR = ITEMS.register("waxed_polished_willow_pillar",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_LAYERED = ITEMS.register("waxed_polished_willow_layered",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WILLOW_CONNECTED = ITEMS.register("waxed_willow_connected",
			() -> new BlockItem(ModBlocks.WAXED_WILLOW_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_CONNECTED = ITEMS.register("waxed_polished_witch_hazel_connected",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WITCH_HAZEL_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_PILLAR = ITEMS.register("waxed_polished_witch_hazel_pillar",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WITCH_HAZEL_PILLAR.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_LAYERED = ITEMS.register("waxed_polished_witch_hazel_layered",
			() -> new BlockItem(ModBlocks.WAXED_POLISHED_WITCH_HAZEL_LAYERED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_CONNECTED = ITEMS.register("waxed_witch_hazel_connected",
			() -> new BlockItem(ModBlocks.WAXED_WITCH_HAZEL_CONNECTED.get(), new Item.Properties()) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					Component cloth = Component.translatable(ModItems.CLOTH.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					Component waxing_kit = Component.translatable(ModItems.WAXING_KIT.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
					tooltipComponents.add(Component.translatable("tooltip.hexerei.waxed_connected_texture", cloth, waxing_kit).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});


	public static final DeferredHolder<Item, Item> MAHOGANY_CONNECTED = ITEMS.register("mahogany_connected",
			() -> new BlockItem(ModBlocks.MAHOGANY_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_CONNECTED = ITEMS.register("polished_mahogany_connected",
			() -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_PILLAR = ITEMS.register("polished_mahogany_pillar",
			() -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});
	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_LAYERED = ITEMS.register("polished_mahogany_layered",
			() -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), new Item.Properties()
					) {
				@Override
				public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
					tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
			});

	public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_TRAPDOOR = ITEMS.register("polished_witch_hazel_trapdoor",
			() -> new BlockItem(ModBlocks.POLISHED_WITCH_HAZEL_TRAPDOOR.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_WILLOW_TRAPDOOR = ITEMS.register("polished_willow_trapdoor",
			() -> new BlockItem(ModBlocks.POLISHED_WILLOW_TRAPDOOR.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_TRAPDOOR = ITEMS.register("polished_mahogany_trapdoor",
			() -> new BlockItem(ModBlocks.POLISHED_MAHOGANY_TRAPDOOR.get(), new Item.Properties()));

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
		LOOT_FUNCTION_TYPES.register(eventBus);

	}

}

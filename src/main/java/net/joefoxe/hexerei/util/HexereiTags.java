package net.joefoxe.hexerei.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class HexereiTags {

	public static class Entity {

		public static final TagKey<EntityType<?>> CAN_RIDE_BROOM = createTag("can_ride_broom");

		private static TagKey<EntityType<?>> createTag(String name) {
			return TagKey.create(Registries.ENTITY_TYPE, HexereiUtil.getResource(name));
		}
	}

	public static class Blocks {

		public static final TagKey<Block> HERB_BLOCK = createTag("herbs");
		public static final TagKey<Block> HEAT_SOURCES = createTag("heat_sources");
		public static final TagKey<Block> CROW_HARVESTABLE = createTag("crow_harvestable");
		public static final TagKey<Block> CROW_BLOCK_HARVESTABLE = createTag("crow_block_harvestable");

		private static TagKey<Block> createTag(String name) {
			return BlockTags.create(HexereiUtil.getResource(name));
		}

		private static TagKey<Block> createForgeTag(String name) {
			return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
		}
	}


	public static class Items {

		public static final TagKey<Item> SIGILS = createTag("sigils");
		public static final TagKey<Item> ALL_SATCHELS = createTag("all_satchels");
		public static final TagKey<Item> SMALL_SATCHELS = createTag("small_satchels");
		public static final TagKey<Item> MEDIUM_SATCHELS = createTag("medium_satchels");
		public static final TagKey<Item> LARGE_SATCHELS = createTag("large_satchels");
		public static final TagKey<Item> BROOM_MISC = createTag("broom_misc");
		public static final TagKey<Item> BROOM_BRUSH = createTag("broom_brush");
		public static final TagKey<Item> HERB_ITEM = createTag("herbs");
		public static final TagKey<Item> TALLOW_MELTABLE = createTag("tallow_meltable");
		public static final TagKey<Item> CANDLES = createTag("candles");
		public static final TagKey<Item> WILLOW_PLANKS = createTag("willow_planks");
		public static final TagKey<Item> MAHOGANY_PLANKS = createTag("mahogany_planks");
		public static final TagKey<Item> WITCH_HAZEL_PLANKS = createTag("witch_hazel_planks");
		public static final TagKey<Item> FLOWER_BIPRODUCT = createTag("flower_biproduct");
		public static final TagKey<Item> INFUSED_FABRIC_CARPET = createTag("infused_fabric_carpet");
		public static final TagKey<Item> INFUSED_FABRIC_BLOCK = createTag("infused_fabric_block");
		public static final TagKey<Item> OWL_TAMING_FOOD = createTag("owl_taming_food");
		public static final TagKey<Item> OWL_BREEDING_FOOD = createTag("owl_breeding_food");


		private static TagKey<Item> createTag(String name) {
			return ItemTags.create(HexereiUtil.getResource(name));
		}

		private static TagKey<Item> createForgeTag(String name) {
			return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
		}
	}
}

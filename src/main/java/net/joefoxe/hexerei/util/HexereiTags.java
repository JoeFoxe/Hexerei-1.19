package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class HexereiTags {

	public static class Blocks {

		public static final TagKey<Block> HERB_BLOCK = createTag("herbs");
		public static final TagKey<Block> HEAT_SOURCES = createTag("heat_sources");
		public static final TagKey<Block> CROW_HARVESTABLE = createTag("crow_harvestable");
		public static final TagKey<Block> CROW_BLOCK_HARVESTABLE = createTag("crow_block_harvestable");

		private static TagKey<Block> createTag(String name) {
			return BlockTags.create(new ResourceLocation(Hexerei.MOD_ID, name));
		}

		private static TagKey<Block> createForgeTag(String name) {
			return BlockTags.create(new ResourceLocation("forge", name));
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
		public static final TagKey<Item> FLOWER_BIPRODUCT = createTag("flower_biproduct");
		public static final TagKey<Item> INFUSED_FABRIC_CARPET = createTag("infused_fabric_carpet");
		public static final TagKey<Item> INFUSED_FABRIC_BLOCK = createTag("infused_fabric_block");

		private static TagKey<Item> createTag(String name) {
			return ItemTags.create(new ResourceLocation(Hexerei.MOD_ID, name));
		}

		private static TagKey<Item> createForgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}
	}
}

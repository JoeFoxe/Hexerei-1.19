package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public class HexereiTags {

    public static class Blocks {

        public static final TagKey<Block> HERB_BLOCK = createTag("herbs");
        public static final TagKey<Block> CROW_HARVESTABLE = createTag("crow_harvestable");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(new ResourceLocation(Hexerei.MOD_ID, name));
        }

        private static TagKey<Block> createForgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }


    public static class Items {

        public static final  TagKey<Item> SIGILS = createTag("sigils");
        public static final TagKey<Item> ALL_SATCHELS = createTag("all_satchels");
        public static final TagKey<Item> SMALL_SATCHELS = createTag("small_satchels");
        public static final  TagKey<Item> MEDIUM_SATCHELS = createTag("medium_satchels");
        public static final  TagKey<Item> LARGE_SATCHELS = createTag("large_satchels");
        public static final  TagKey<Item> BROOM_MISC = createTag("broom_misc");
        public static final  TagKey<Item> BROOM_BRUSH = createTag("broom_brush");
        public static final  TagKey<Item> HERB_ITEM = createTag("herbs");
        public static final  TagKey<Item> CANDLES = createTag("candles");

        private static  TagKey<Item> createTag(String name) {
            return ItemTags.create(new ResourceLocation(Hexerei.MOD_ID, name));
        }

        private static  TagKey<Item> createForgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }
}

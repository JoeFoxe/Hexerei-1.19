package net.joefoxe.hexerei.data.tags;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.biome.ModBiomes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBiomeTagsProvider extends BiomeTagsProvider {

	public ModBiomeTagsProvider(DataGenerator dataGen, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGen, Hexerei.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(Tags.Biomes.IS_SWAMP).add(ModBiomes.WILLOW_SWAMP.get());
	}
}

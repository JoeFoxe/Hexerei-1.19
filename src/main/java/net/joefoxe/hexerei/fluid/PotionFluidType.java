package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class PotionFluidType extends FluidType {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;

    public PotionFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
    }

    public static int getTintColor(FluidStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int color = PotionUtils.getColor(PotionUtils.getAllEffects(tag)) | 0xD0000000;
        return color;
    }

    public static int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return 0xffffffff;
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        ItemLike itemFromBottleType =
                PotionFluidHandler.itemFromBottleType(HexereiUtil.readEnum(tag, "Bottle", PotionFluid.BottleType.class));
        return PotionUtils.getPotion(tag)
                .getName(itemFromBottleType.asItem()
                        .getDescriptionId() + ".effect.");
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return PotionFluidType.getTintColor(stack);
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return PotionFluidType.getTintColor(state, getter, pos);
            }

        });
    }


}

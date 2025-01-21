package net.joefoxe.hexerei.data.loot;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CourierLetterTile;
import net.joefoxe.hexerei.tileentity.CourierPackageTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class CopyCourierLetterDataFunction extends LootItemConditionalFunction {

    public static final MapCodec<CopyCourierLetterDataFunction> CODEC = RecordCodecBuilder.mapCodec(
            p_298065_ -> commonFields(p_298065_)
                    .apply(p_298065_, CopyCourierLetterDataFunction::new)
    );

    protected CopyCourierLetterDataFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CourierLetterTile courierLetterTile) {
            CompoundTag tag = courierLetterTile.saveData(new CompoundTag(), Hexerei.DynamicRegistries.get());
            if (!tag.isEmpty())
                BlockItem.setBlockEntityData(stack, courierLetterTile.getType(), courierLetterTile.save(new CompoundTag(), Hexerei.DynamicRegistries.get()));
            return stack;
        }

        return stack;
    }

    @Override
    public LootItemFunctionType<CopyCourierLetterDataFunction> getType() {
        return ModItems.COPY_LETTER_DATA.get();
    }

    //    @Override
//    public LootItemFunctionType<?> getType() {
//
//    }
}
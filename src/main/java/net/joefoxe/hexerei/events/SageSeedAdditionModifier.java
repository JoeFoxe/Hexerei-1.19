package net.joefoxe.hexerei.events;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class SageSeedAdditionModifier extends LootModifier {
    private final Item addition;
    private final int count;



    public static final MapCodec<SageSeedAdditionModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(
                            instance.group(
                                    Codec.STRING.optionalFieldOf("addition", "").forGetter(d -> BuiltInRegistries.ITEM.getKey(d.addition).toString()),
                                    Codec.INT.optionalFieldOf("count", 1).forGetter(d -> d.count)
                            )
                    )
                    .apply(instance, SageSeedAdditionModifier::new));

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
            NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Hexerei.MOD_ID);
    private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SageSeedAdditionModifier>> GRASS_DROPS = REGISTER.register(
            "sage_seed_drops", () -> CODEC
    );

    public SageSeedAdditionModifier(LootItemCondition[] lootItemConditions, String addition, Integer count) {
        super(lootItemConditions);
        this.addition = BuiltInRegistries.ITEM.get(ResourceLocation.parse(addition));
        this.count = count;
    }

    public static void init(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(addition, count));

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return GRASS_DROPS.get();
    }

}
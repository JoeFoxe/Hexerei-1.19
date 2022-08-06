package net.joefoxe.hexerei.events;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class SageSeedAdditionModifier extends LootModifier {
    private final Item addition;
    private final int count;

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Hexerei.MOD_ID);
    private static final RegistryObject<Codec<SageSeedAdditionModifier>> GRASS_DROPS = REGISTER.register(
            "sage_seeds_from_grass", () -> RecordCodecBuilder.create(instance -> instance.group(
                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                    Codec.STRING.fieldOf("addition").forGetter(d -> String.valueOf(d.addition)),
                    Codec.INT.fieldOf("count").forGetter(d -> d.count)
            ).apply(instance, SageSeedAdditionModifier::new))
    );

    public SageSeedAdditionModifier(LootItemCondition[] lootItemConditions, String addition, Integer count) {
        super(lootItemConditions);
        this.addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation(addition));
        this.count = count;
    }

    public static void init()
    {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(addition, count));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return GRASS_DROPS.get();
    }

}
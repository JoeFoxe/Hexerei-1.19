package net.joefoxe.hexerei.events;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AnimalFatAdditionModifier extends LootModifier {
    private final Item addition;

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Hexerei.MOD_ID);
    private static final RegistryObject<Codec<AnimalFatAdditionModifier>> GRASS_DROPS = REGISTER.register(
            "animal_fat_drops", () -> RecordCodecBuilder.create(instance -> instance.group(
                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                    Codec.STRING.fieldOf("addition").forGetter(d -> String.valueOf(d.addition))
            ).apply(instance, AnimalFatAdditionModifier::new))
    );

    public AnimalFatAdditionModifier(LootItemCondition[] lootItemConditions, String addition) {
        super(lootItemConditions);
        this.addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation(addition));
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(context.getRandom().nextDouble() / (double)Math.min(context.getLootingModifier() + 1, 4) < 0.45D)
            generatedLoot.add(new ItemStack(addition, context.getRandom().nextInt(Math.min(context.getLootingModifier() + 1, 4)) + 1));

        return generatedLoot;
    }

    public static void init()
    {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return GRASS_DROPS.get();
    }

}
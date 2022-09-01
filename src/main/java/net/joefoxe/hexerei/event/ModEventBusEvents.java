package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
//        event.put(ModEntityTypes.BUFF_ZOMBIE.get(), BuffZombieEntity.setCustomAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterDispenserBehaviors(RegisterEvent event) {
//        if (event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES)){
//            //example on how to replace RegistryEvent.Register<T>
//        }
//        ModSpawnEggItem.initSpawnEggs();
    }

//
//    @SubscribeEvent
//    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>>
//                                                           event) {
//        event.getRegistry().registerAll(
//                new AnimalFatAdditionModifier.Serializer().setRegistryName
//                        (new ResourceLocation(Hexerei.MOD_ID,"animal_fat_from_cow")),
//                new AnimalFatAdditionModifier.Serializer().setRegistryName
//                        (new ResourceLocation(Hexerei.MOD_ID,"animal_fat_from_sheep")),
//                new AnimalFatAdditionModifier.Serializer().setRegistryName
//                        (new ResourceLocation(Hexerei.MOD_ID,"animal_fat_from_pig")),
//                new SageSeedAdditionModifier.Serializer().setRegistryName
//                        (new ResourceLocation(Hexerei.MOD_ID,"sage_seeds_from_grass")),
//                new SageSeedAdditionModifier.Serializer().setRegistryName
//                        (new ResourceLocation(Hexerei.MOD_ID,"sage_seeds_from_tall_grass"))
//        );
//    }
}
package net.joefoxe.hexerei.client.renderer.entity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.*;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Bus.MOD)
public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES
            = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Hexerei.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BroomEntity>> BROOM =
            ENTITY_TYPES.register("broom",
                    () -> EntityType.Builder.<BroomEntity>of(BroomEntity::new,
                                    MobCategory.MISC).sized(1.175F, 0.3625F).setShouldReceiveVelocityUpdates(true).setTrackingRange(10).updateInterval(1)
                            .build(HexereiUtil.getResource("broom").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ModBoatEntity>> HEXEREI_BOAT =
            ENTITY_TYPES.register("boat",
                    () -> EntityType.Builder.of(ModBoatEntity::new,
                                    MobCategory.MISC).sized(1.175F, 0.3625F).setTrackingRange(10)
                            .build(HexereiUtil.getResource("boat").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ModChestBoatEntity>> HEXEREI_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat",
                    () -> EntityType.Builder.of(ModChestBoatEntity::new,
                                    MobCategory.MISC).sized(1.175F, 0.3625F).setTrackingRange(10)
                            .build(HexereiUtil.getResource("chest_boat").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CrowEntity>> CROW =
            ENTITY_TYPES.register("crow",
                    () -> EntityType.Builder.of(CrowEntity::new,
                                    MobCategory.CREATURE).sized(0.375F, 0.5F).setTrackingRange(64).setUpdateInterval(1)
                            .build(HexereiUtil.getResource("crow").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<OwlEntity>> OWL =
            ENTITY_TYPES.register("owl",
                    () -> EntityType.Builder.of(OwlEntity::new,
                                    MobCategory.CREATURE).sized(0.5F, 0.65F).setTrackingRange(64).setUpdateInterval(1)
                            .build(HexereiUtil.getResource("owl").toString()));
//
//    public static final EntityType<EntityCrow> CROW = registerEntity(EntityType.Builder.create(EntityCrow::new, MobCategory.CREATURE).size(0.45F, 0.45F), "crow");



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CROW.get(), CrowEntity.createAttributes());
        event.put(OWL.get(), OwlEntity.createAttributes());
    }

//    public static final DeferredHolder<EntityType<?>, SpawnEggItem> CROW_SPAWN_EGG = ModItems.ITEMS.register("crow_spawn_egg", () -> new SpawnEggItem(ModEntityTypes.CROW.get(), 0x161616, 0x333333, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> addEntityWithEgg(String name, int color1, int color2, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        EntityType<T> type = EntityType.Builder.of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Hexerei.MOD_ID + ":" + name);
        ModItems.ITEMS.register(name + "_spawn_egg", () -> new SpawnEggItem(type, color1, color2, new Item.Properties()));
        return ENTITY_TYPES.register(name, () -> type);
    }


//    private static <T extends Entity> EntityType<T> register2(String key, EntityType.Builder<T> builder) {
//        return Registry.register(Registry.ENTITY_TYPE, key, builder.build(key));
//    }
//
//    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
//        ResourceLocation nameLoc = HexereiUtil.getResource(entityName);
//        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
//    }
//
//    public static Predicate<LivingEntity> buildPredicateFromTag(Tag entityTag){
//        if(entityTag == null){
//            return Predicates.alwaysFalse();
//        }else{
//            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().isContained(entityTag);
//        }
//    }
//
//    public static Predicate<LivingEntity> buildPredicateFromTagTameable(Tag entityTag, LivingEntity owner){
//        if(entityTag == null){
//            return Predicates.alwaysFalse();
//        }else{
//            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().isContained(entityTag) && !owner.isOnSameTeam(e);
//        }
//    }

}
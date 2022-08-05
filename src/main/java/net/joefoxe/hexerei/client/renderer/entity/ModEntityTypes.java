package net.joefoxe.hexerei.client.renderer.entity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.item.ModItemGroup;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES
            = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Hexerei.MOD_ID);

//    public static final EntityType<CrowEntity> CROW2 = registerEntity(EntityType.Builder.create(CrowEntity::new, MobCategory.CREATURE).size(0.45F, 0.45F), "crow");

//    public static final RegistryObject<EntityType<BuffZombieEntity>> BUFF_ZOMBIE =
//            ENTITY_TYPES.register("buff_zombie",
//                    () -> EntityType.Builder.of(BuffZombieEntity::new,
//                                    MobCategory.MONSTER).sized(1f, 3f)
//                            .build(new ResourceLocation(Hexerei.MOD_ID, "buff_zombie").toString()));

//    public static final RegistryObject<EntityType<PigeonEntity>> PIGEON =
//            ENTITY_TYPES.register("pigeon",
//                    () -> EntityType.Builder.of(PigeonEntity::new,
//                                    MobCategory.CREATURE).size(0.4f, 0.3f)
//                            .build(new ResourceLocation(Hexerei.MOD_ID, "pigeon").toString()));

    public static final RegistryObject<EntityType<BroomEntity>> BROOM =
            ENTITY_TYPES.register("broom",
                    () -> EntityType.Builder.<BroomEntity>of(BroomEntity::new,
                                    MobCategory.MISC).sized(1.175F, 0.3625F).setTrackingRange(10)
                            .build(new ResourceLocation(Hexerei.MOD_ID, "broom").toString()));

    public static final RegistryObject<EntityType<CrowEntity>> CROW =
            ENTITY_TYPES.register("crow",
                    () -> EntityType.Builder.<CrowEntity>of(CrowEntity::new,
                                    MobCategory.CREATURE).sized(0.375F, 0.5F).setTrackingRange(64).setUpdateInterval(1)
                            .build(new ResourceLocation(Hexerei.MOD_ID, "crow").toString()));
//    public static final RegistryObject<EntityType<CrowEntity>> CROW = addEntityWithEgg("crow", 0x161616, 0x333333, 0.375F, 0.5F, CrowEntity::new, MobCategory.CREATURE);

//    public static final RegistryObject<EntityType<CrowEntity>> CROW =
//            ENTITY_TYPES.register("crow",
//                    () -> EntityType.Builder.<CrowEntity>of(CrowEntity::new,
//                                    MobCategory.MISC).size(0.35F, 0.35F)
//                            .build(new ResourceLocation(Hexerei.MOD_ID, "crow").toString()));
//
//    public static final EntityType<EntityCrow> CROW = registerEntity(EntityType.Builder.create(EntityCrow::new, MobCategory.CREATURE).size(0.45F, 0.45F), "crow");



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CROW.get(), CrowEntity.createAttributes());
    }

//    public static final RegistryObject<SpawnEggItem> CROW_SPAWN_EGG = ModItems.ITEMS.register("crow_spawn_egg", () -> new SpawnEggItem(ModEntityTypes.CROW.get(), 0x161616, 0x333333, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));

    static <T extends Mob> RegistryObject<EntityType<T>> addEntityWithEgg(String name, int color1, int color2, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        EntityType<T> type = EntityType.Builder.<T>of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Hexerei.MOD_ID + ":" + name);
        ModItems.ITEMS.register(name + "_spawn_egg", () -> new SpawnEggItem((EntityType<? extends T>) type, color1, color2, new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP)));
        return ENTITY_TYPES.register(name, () -> type);
    }


//    private static <T extends Entity> EntityType<T> register2(String key, EntityType.Builder<T> builder) {
//        return Registry.register(Registry.ENTITY_TYPE, key, builder.build(key));
//    }
//
//    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
//        ResourceLocation nameLoc = new ResourceLocation(Hexerei.MOD_ID, entityName);
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
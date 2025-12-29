package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainers {

    public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Hexerei.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WoodcutterContainer>> WOODCUTTER_CONTAINER
            = CONTAINERS.register("woodcutter_container",
            () -> new MenuType<>((IContainerFactory<WoodcutterContainer>) (windowId, inv, data) -> new WoodcutterContainer(windowId, inv), FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MixingCauldronContainer>> MIXING_CAULDRON_CONTAINER
            = CONTAINERS.register("mixing_cauldron_container",
            () -> new MenuType<>((IContainerFactory<MixingCauldronContainer>) (windowId, inv, data) -> {

                BlockPos pos = data.readBlockPos();
                Level world = inv.player.level();
                return new MixingCauldronContainer(windowId, world, pos, inv, inv.player);
            }, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<CofferContainer>> COFFER_CONTAINER
            = CONTAINERS.register("coffer_container",
            () -> new MenuType<>((IContainerFactory<CofferContainer>) (windowId, inv, data) -> {
                if(data.readBoolean()){
                    BlockPos pos = data.readBlockPos();
                    Level world = inv.player.level();
                    return new CofferContainer(windowId, world, pos, inv, inv.player);
                } else {

                    if(data.readInt() == 0)
                        return new CofferContainer(windowId, inv.player.getMainHandItem(), inv, inv.player, InteractionHand.MAIN_HAND);
                    else
                        return new CofferContainer(windowId, inv.player.getOffhandItem(), inv, inv.player, InteractionHand.OFF_HAND);
                }
            }, FeatureFlags.DEFAULT_FLAGS));


    public static final DeferredHolder<MenuType<?>, MenuType<PackageContainer>> PACKAGE_CONTAINER = CONTAINERS.register("package_container",
            () -> new MenuType<>((IContainerFactory<PackageContainer>) PackageContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<HerbJarContainer>> HERB_JAR_CONTAINER = CONTAINERS.register("herb_jar_container",
            () -> new MenuType<>((IContainerFactory<HerbJarContainer>) HerbJarContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<DipperContainer>> DIPPER_CONTAINER = CONTAINERS.register("dipper_container",
            () -> new MenuType<>((IContainerFactory<DipperContainer>) DipperContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<DryingRackContainer>> DRYING_RACK_CONTAINER = CONTAINERS.register("drying_rack_container",
            () -> new MenuType<>((IContainerFactory<DryingRackContainer>) DryingRackContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<PestleAndMortarContainer>> PESTLE_AND_MORTAR_CONTAINER = CONTAINERS.register("pestle_and_mortar_container",
            () -> new MenuType<>((IContainerFactory<PestleAndMortarContainer>) PestleAndMortarContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<CrowFluteContainer>> CROW_FLUTE_CONTAINER = CONTAINERS.register("crow_flute_container",
            () -> new MenuType<>((IContainerFactory<CrowFluteContainer>) CrowFluteContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<BroomContainer>> BROOM_CONTAINER = CONTAINERS.register("broom_container",
            () -> new MenuType<>((IContainerFactory<BroomContainer>) (windowId, inv, data) -> {
                Level world = inv.player.level();//new BroomEntity(world, pos.getX(), pos.getY(), pos.getZ())

                if (data == null) {
                    return new BroomContainer(windowId,new BroomEntity(world, 0, 0, 0), inv, inv.player, false);
                }

                boolean isEnder = data.readBoolean();
                int id = data.readInt();
                if(world.getEntity(id) != null)
                    return new BroomContainer(windowId,(BroomEntity)world.getEntity(id), inv, inv.player, isEnder);
                else
                    return new BroomContainer(windowId,new BroomEntity(world, 0, 0, 0), inv, inv.player, isEnder);
            }, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<CrowContainer>> CROW_CONTAINER
            = CONTAINERS.register("crow_container",
            () -> new MenuType<>((IContainerFactory<CrowContainer>) (windowId, inv, data) -> {

                Level world = inv.player.level();
                int id = data.readInt();
                if(world.getEntity(id) != null)
                    return new CrowContainer(windowId,(CrowEntity)world.getEntity(id), inv, inv.player);
                else
                    return new CrowContainer(windowId,new CrowEntity(ModEntityTypes.CROW.get(),world), inv, inv.player);
            }, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<OwlContainer>> OWL_CONTAINER
            = CONTAINERS.register("owl_container",
            () -> new MenuType<>((IContainerFactory<OwlContainer>) (windowId, inv, data) -> {

                Level world = inv.player.level();
                int id = data.readInt();
                if(world.getEntity(id) != null)
                    return new OwlContainer(windowId,(OwlEntity)world.getEntity(id), inv, inv.player);
                else
                    return new OwlContainer(windowId,new OwlEntity(ModEntityTypes.OWL.get(),world), inv, inv.player);
            }, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}

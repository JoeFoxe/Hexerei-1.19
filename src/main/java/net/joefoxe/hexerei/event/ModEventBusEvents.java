package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.CrowWhitelistRenderer;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.List;

@EventBusSubscriber(modid = Hexerei.MOD_ID, bus = Bus.MOD)
public class ModEventBusEvents {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerBelowAll(HexereiUtil.getResource("crow_whitelist"), new CrowWhitelistRenderer());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        var containers = List.of(
                ModTileEntities.COFFER_TILE,
                ModTileEntities.MIXING_CAULDRON_TILE,
                ModTileEntities.DRYING_RACK_TILE,
                ModTileEntities.PESTLE_AND_MORTAR_TILE,
                ModTileEntities.CANDLE_DIPPER_TILE,
                ModTileEntities.CHEST_TILE,
                ModTileEntities.SAGE_BURNING_PLATE_TILE,
                ModTileEntities.COURIER_PACKAGE_TILE
                );
        for (var container : containers) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, container.get(), (c, side) -> new InvWrapper(c));
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.HERB_JAR_TILE.get(), (c, side) -> c.itemHandler);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModTileEntities.MIXING_CAULDRON_TILE.get(), (c, side) -> c);


        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 5), ModItems.COURIER_PACKAGE.get());


        event.registerEntity(Capabilities.ItemHandler.ENTITY, ModEntityTypes.OWL.get(), (owl, v) -> owl.itemHandler);
        event.registerEntity(Capabilities.ItemHandler.ENTITY, ModEntityTypes.CROW.get(), (crow, v) -> crow.itemHandler);
        event.registerEntity(Capabilities.ItemHandler.ENTITY, ModEntityTypes.BROOM.get(), (broom, v) -> broom.itemHandler);

    }
}
package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Hexerei.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HexereiPacketHandler {

    public static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(PROTOCOL_VERSION);
//        event.registrar(PROTOCOL_VERSION)
        reg.playToClient(MessageCountUpdate.TYPE, MessageCountUpdate.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EmitParticlesPacket.TYPE, EmitParticlesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(TESyncPacket.TYPE, TESyncPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BroomSyncPacket.TYPE, BroomSyncPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BroomSyncFloatModeToServer.TYPE, BroomSyncFloatModeToServer.CODEC,  HexereiPacketHandler::handle);;
        reg.playToServer(BroomAskForSyncPacket.TYPE, BroomAskForSyncPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BroomSyncRotationToServer.TYPE, BroomSyncRotationToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BroomActivateToServer.TYPE, BroomActivateToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(DrainCauldronToServer.TYPE, DrainCauldronToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BroomDamageBrushToServer.TYPE, BroomDamageBrushToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BroomSyncRotation.TYPE, BroomSyncRotation.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BroomEnderSatchelBrushParticlePacket.TYPE, BroomEnderSatchelBrushParticlePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BroomDamageMiscToServer.TYPE, BroomDamageMiscToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(DowsingRodUpdatePositionPacket.TYPE, DowsingRodUpdatePositionPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(OwlTeleportParticlePacket.TYPE, OwlTeleportParticlePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(SendOwlCourierPacket.TYPE, SendOwlCourierPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(OpenOwlCourierDepotNameEditorPacket.TYPE, OpenOwlCourierDepotNameEditorPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(UpdateOwlCourierDepotNamePacket.TYPE, UpdateOwlCourierDepotNamePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundOwlCourierDepotDataPacket.TYPE, ClientboundOwlCourierDepotDataPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundOwlCourierDepotDataInventoryPacket.TYPE, ClientboundOwlCourierDepotDataInventoryPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundOpenOwlCourierSendScreenPacket.TYPE, ClientboundOpenOwlCourierSendScreenPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundOpenCourierLetterScreenPacket.TYPE, ClientboundOpenCourierLetterScreenPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CourierLetterUpdatePacket.TYPE, CourierLetterUpdatePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EmotionPacket.TYPE, EmotionPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BrowAnimPacket.TYPE, BrowAnimPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BrowPositioningPacket.TYPE, BrowPositioningPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(OwlHootPacket.TYPE, OwlHootPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(CrowCawPacket.TYPE, CrowCawPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(HeadTiltPacket.TYPE, HeadTiltPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(HeadShakePacket.TYPE, HeadShakePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(TailWagPacket.TYPE, TailWagPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(TailFanPacket.TYPE, TailFanPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(CandleExtinguishPacket.TYPE, CandleExtinguishPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(CandleEffectParticlePacket.TYPE, CandleEffectParticlePacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(StartRidingPacket.TYPE, StartRidingPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CofferSyncCrowButtonToServer.TYPE, CofferSyncCrowButtonToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(HerbJarSyncCrowButtonToServer.TYPE, HerbJarSyncCrowButtonToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(PeckPacket.TYPE, PeckPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EmitExtinguishParticlesPacket.TYPE, EmitExtinguishParticlesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowSyncCommandToServer.TYPE, CrowSyncCommandToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EntitySyncPacket.TYPE, EntitySyncPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(OwlSyncInvPacket.TYPE, OwlSyncInvPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EntitySyncAdditionalDataPacket.TYPE, EntitySyncAdditionalDataPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(AskForSyncPacket.TYPE, AskForSyncPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowWhitelistSyncToServer.TYPE, CrowWhitelistSyncToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowInteractionRangeToServer.TYPE, CrowInteractionRangeToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowCanAttackToServer.TYPE, CrowCanAttackToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(PlayerWhitelistingForCrowSyncToServer.TYPE, PlayerWhitelistingForCrowSyncToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowSyncHelpCommandToServer.TYPE, CrowSyncHelpCommandToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(EatParticlesPacket.TYPE, EatParticlesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowFluteCommandSyncToServer.TYPE, CrowFluteCommandSyncToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowFluteHelpCommandSyncToServer.TYPE, CrowFluteHelpCommandSyncToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowFluteCommandModeSyncToServer.TYPE, CrowFluteCommandModeSyncToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowFluteClearCrowListToServer.TYPE, CrowFluteClearCrowListToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(CrowFluteClearCrowPerchToServer.TYPE, CrowFluteClearCrowPerchToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BookPagesPacket.TYPE, BookPagesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BookEntriesPacket.TYPE, BookEntriesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(AskForEntriesAndPagesPacket.TYPE, AskForEntriesAndPagesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BookTurnPageToServer.TYPE, BookTurnPageToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundBookTurnPage.TYPE, ClientboundBookTurnPage.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundBookDataUpdate.TYPE, ClientboundBookDataUpdate.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BookBookmarkPageToServer.TYPE, BookBookmarkPageToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BookBookmarkSwapToServer.TYPE, BookBookmarkSwapToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(BookBookmarkDeleteToServer.TYPE, BookBookmarkDeleteToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(RecipeToServer.TYPE, RecipeToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(AskForMapDataPacket.TYPE, AskForMapDataPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(MapDataPacket.TYPE, MapDataPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ToggleDynamicLightPacket.TYPE, ToggleDynamicLightPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(WoodcutterRecipesPacket.TYPE, WoodcutterRecipesPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(BookSyncDataPacket.TYPE, BookSyncDataPacket.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(UpdateBookDataToServer.TYPE, UpdateBookDataToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(PaintDataToServer.TYPE, PaintDataToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToClient(ClientboundPaintData.TYPE, ClientboundPaintData.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(AskForPaintDataToServer.TYPE, AskForPaintDataToServer.CODEC,  HexereiPacketHandler::handle);
        reg.playToServer(SetPaintingToServer.TYPE, SetPaintingToServer.CODEC,  HexereiPacketHandler::handle);
    }

    private static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            //separate class to avoid loading client code on server.
            //Using OnlyIn on a method in this class would work too, but is discouraged
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {

        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }

    public static void sendToNearbyClient(Level world, BlockPos pos, CustomPacketPayload toSend) {
        if (world instanceof ServerLevel ws) {
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
                    .forEach(p -> HexereiPacketHandler.sendToPlayerClient(toSend, p));
        }
    }

    public static void sendToNearbyClient(Level world, Entity entity, CustomPacketPayload toSend) {
        sendToNearbyClient(world, entity.blockPosition(), toSend);
    }

    public static void sendToPlayerClient(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static void sendToAllPlayers(CustomPacketPayload msg, MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach((serverPlayer) -> PacketDistributor.sendToPlayer(serverPlayer, msg));
    }

    public static void sendToAllPlayersBut(CustomPacketPayload msg, MinecraftServer server, ServerPlayer playerNotToSendTo) {
        server.getPlayerList().getPlayers().forEach((serverPlayer) -> {
            if (serverPlayer != playerNotToSendTo)
                PacketDistributor.sendToPlayer(serverPlayer, msg);
        });
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }
}
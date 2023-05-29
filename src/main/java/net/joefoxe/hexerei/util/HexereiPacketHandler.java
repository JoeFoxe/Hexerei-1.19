package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class HexereiPacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel instance;
    private static int nextId = 0;
    static int id = 0;

    public static void register()
    {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Hexerei.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        instance.registerMessage(++ id, MessageCountUpdate.class, MessageCountUpdate::encode, MessageCountUpdate::decode, MessageCountUpdate::handle);

        instance.registerMessage(++ id, EmitParticlesPacket.class, EmitParticlesPacket::encode, EmitParticlesPacket::decode, EmitParticlesPacket::handle);

        instance.registerMessage(
                ++ id,
                TESyncPacket.class,
                TESyncPacket::encode,
                TESyncPacket::decode,
                TESyncPacket::consume
        );

        instance.registerMessage(
                ++ id,
                BroomSyncPacket.class,
                BroomSyncPacket::encode,
                BroomSyncPacket::decode,
                BroomSyncPacket::consume
        );

        instance.registerMessage(
                ++ id,
                BroomSyncFloatModeToServer.class,
                BroomSyncFloatModeToServer::encode,
                BroomSyncFloatModeToServer::decode,
                BroomSyncFloatModeToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BroomAskForSyncPacket.class,
                BroomAskForSyncPacket::encode,
                BroomAskForSyncPacket::decode,
                BroomAskForSyncPacket::consume
        );

        instance.registerMessage(
                ++ id,
                BroomSyncRotationToServer.class,
                BroomSyncRotationToServer::encode,
                BroomSyncRotationToServer::decode,
                BroomSyncRotationToServer::consume
        );

        instance.registerMessage(
                ++ id,
                DrainCauldronToServer.class,
                DrainCauldronToServer::encode,
                DrainCauldronToServer::decode,
                DrainCauldronToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BroomSyncRotation.class,
                BroomSyncRotation::encode,
                BroomSyncRotation::decode,
                BroomSyncRotation::consume
        );

        instance.registerMessage(
                ++ id,
                BroomDamageBrushToServer.class,
                BroomDamageBrushToServer::encode,
                BroomDamageBrushToServer::decode,
                BroomDamageBrushToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BroomEnderSatchelBrushParticlePacket.class,
                BroomEnderSatchelBrushParticlePacket::encode,
                BroomEnderSatchelBrushParticlePacket::decode,
                BroomEnderSatchelBrushParticlePacket::consume
        );

        instance.registerMessage(
                ++ id,
                BroomDamageMiscToServer.class,
                BroomDamageMiscToServer::encode,
                BroomDamageMiscToServer::decode,
                BroomDamageMiscToServer::consume
        );

        instance.registerMessage(
                ++ id,
                DowsingRodUpdatePositionPacket.class,
                DowsingRodUpdatePositionPacket::encode,
                DowsingRodUpdatePositionPacket::decode,
                DowsingRodUpdatePositionPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CrowCawPacket.class,
                CrowCawPacket::encode,
                CrowCawPacket::decode,
                CrowCawPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CrowTailWagPacket.class,
                CrowTailWagPacket::encode,
                CrowTailWagPacket::decode,
                CrowTailWagPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CrowTailFanPacket.class,
                CrowTailFanPacket::encode,
                CrowTailFanPacket::decode,
                CrowTailFanPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CandleExtinguishPacket.class,
                CandleExtinguishPacket::encode,
                CandleExtinguishPacket::decode,
                CandleExtinguishPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CandleEffectParticlePacket.class,
                CandleEffectParticlePacket::encode,
                CandleEffectParticlePacket::decode,
                CandleEffectParticlePacket::consume
        );

        instance.registerMessage(
                ++ id,
                CrowStartRidingPacket.class,
                CrowStartRidingPacket::encode,
                CrowStartRidingPacket::decode,
                CrowStartRidingPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CofferSyncCrowButtonToServer.class,
                CofferSyncCrowButtonToServer::encode,
                CofferSyncCrowButtonToServer::decode,
                CofferSyncCrowButtonToServer::consume
        );

        instance.registerMessage(
                ++ id,
                HerbJarSyncCrowButtonToServer.class,
                HerbJarSyncCrowButtonToServer::encode,
                HerbJarSyncCrowButtonToServer::decode,
                HerbJarSyncCrowButtonToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowPeckPacket.class,
                CrowPeckPacket::encode,
                CrowPeckPacket::decode,
                CrowPeckPacket::consume
        );


        instance.registerMessage(
                ++ id,
                EmitExtinguishParticlesPacket.class,
                EmitExtinguishParticlesPacket::encode,
                EmitExtinguishParticlesPacket::decode,
                EmitExtinguishParticlesPacket::handle
        );


        instance.registerMessage(
                ++ id,
                CrowSyncCommandToServer.class,
                CrowSyncCommandToServer::encode,
                CrowSyncCommandToServer::decode,
                CrowSyncCommandToServer::consume
        );


        instance.registerMessage(
                ++ id,
                CrowSyncPacket.class,
                CrowSyncPacket::encode,
                CrowSyncPacket::decode,
                CrowSyncPacket::consume
        );


        instance.registerMessage(
                ++ id,
                CrowSyncAdditonalDataPacket.class,
                CrowSyncAdditonalDataPacket::encode,
                CrowSyncAdditonalDataPacket::decode,
                CrowSyncAdditonalDataPacket::consume
        );


        instance.registerMessage(
                ++ id,
                CrowAskForSyncPacket.class,
                CrowAskForSyncPacket::encode,
                CrowAskForSyncPacket::decode,
                CrowAskForSyncPacket::consume
        );

        instance.registerMessage(
                ++ id,
                CrowWhitelistSyncToServer.class,
                CrowWhitelistSyncToServer::encode,
                CrowWhitelistSyncToServer::decode,
                CrowWhitelistSyncToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowInteractionRangeToServer.class,
                CrowInteractionRangeToServer::encode,
                CrowInteractionRangeToServer::decode,
                CrowInteractionRangeToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowCanAttackToServer.class,
                CrowCanAttackToServer::encode,
                CrowCanAttackToServer::decode,
                CrowCanAttackToServer::consume
        );

        instance.registerMessage(
                ++ id,
                PlayerWhitelistingForCrowSyncToServer.class,
                PlayerWhitelistingForCrowSyncToServer::encode,
                PlayerWhitelistingForCrowSyncToServer::decode,
                PlayerWhitelistingForCrowSyncToServer::consume
        );


        instance.registerMessage(
                ++ id,
                CrowSyncHelpCommandToServer.class,
                CrowSyncHelpCommandToServer::encode,
                CrowSyncHelpCommandToServer::decode,
                CrowSyncHelpCommandToServer::consume
        );


        instance.registerMessage(
                ++ id,
                CrowEatParticlesPacket.class,
                CrowEatParticlesPacket::encode,
                CrowEatParticlesPacket::decode,
                CrowEatParticlesPacket::consume
        );


        instance.registerMessage(
                ++ id,
                CrowFluteCommandSyncToServer.class,
                CrowFluteCommandSyncToServer::encode,
                CrowFluteCommandSyncToServer::decode,
                CrowFluteCommandSyncToServer::consume
        );


        instance.registerMessage(
                ++ id,
                CrowFluteHelpCommandSyncToServer.class,
                CrowFluteHelpCommandSyncToServer::encode,
                CrowFluteHelpCommandSyncToServer::decode,
                CrowFluteHelpCommandSyncToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowFluteCommandModeSyncToServer.class,
                CrowFluteCommandModeSyncToServer::encode,
                CrowFluteCommandModeSyncToServer::decode,
                CrowFluteCommandModeSyncToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowFluteClearCrowListToServer.class,
                CrowFluteClearCrowListToServer::encode,
                CrowFluteClearCrowListToServer::decode,
                CrowFluteClearCrowListToServer::consume
        );

        instance.registerMessage(
                ++ id,
                CrowFluteClearCrowPerchToServer.class,
                CrowFluteClearCrowPerchToServer::encode,
                CrowFluteClearCrowPerchToServer::decode,
                CrowFluteClearCrowPerchToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BookPagesPacket.class,
                BookPagesPacket::encode,
                BookPagesPacket::decode,
                BookPagesPacket::consume
        );

        instance.registerMessage(
                ++ id,
                BookEntriesPacket.class,
                BookEntriesPacket::encode,
                BookEntriesPacket::decode,
                BookEntriesPacket::consume
        );

        instance.registerMessage(
                ++ id,
                AskForEntriesAndPagesPacket.class,
                AskForEntriesAndPagesPacket::encode,
                AskForEntriesAndPagesPacket::decode,
                AskForEntriesAndPagesPacket::consume
        );

        instance.registerMessage(
                ++ id,
                BookTurnPageToServer.class,
                BookTurnPageToServer::encode,
                BookTurnPageToServer::decode,
                BookTurnPageToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BookTurnPageBackToServer.class,
                BookTurnPageBackToServer::encode,
                BookTurnPageBackToServer::decode,
                BookTurnPageBackToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BookBookmarkPageToServer.class,
                BookBookmarkPageToServer::encode,
                BookBookmarkPageToServer::decode,
                BookBookmarkPageToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BookBookmarkSwapToServer.class,
                BookBookmarkSwapToServer::encode,
                BookBookmarkSwapToServer::decode,
                BookBookmarkSwapToServer::consume
        );

        instance.registerMessage(
                ++ id,
                BookBookmarkDeleteToServer.class,
                BookBookmarkDeleteToServer::encode,
                BookBookmarkDeleteToServer::decode,
                BookBookmarkDeleteToServer::consume
        );

        instance.registerMessage(
                ++ id,
                RecipeToServer.class,
                RecipeToServer::encode,
                RecipeToServer::decode,
                RecipeToServer::consume
        );

        instance.registerMessage(
                ++ id,
                AskForMapDataPacket.class,
                AskForMapDataPacket::encode,
                AskForMapDataPacket::decode,
                AskForMapDataPacket::consume
        );

        instance.registerMessage(
                ++ id,
                MapDataPacket.class,
                MapDataPacket::encode,
                MapDataPacket::decode,
                MapDataPacket::consume
        );

        instance.registerMessage(
                ++ id,
                ToggleDynamicLightPacket.class,
                ToggleDynamicLightPacket::encode,
                ToggleDynamicLightPacket::decode,
                ToggleDynamicLightPacket::consume
        );
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }

    public static <MSG> void sendToServer(MSG msg) {
        HexereiPacketHandler.instance.sendToServer(msg);
    }
}
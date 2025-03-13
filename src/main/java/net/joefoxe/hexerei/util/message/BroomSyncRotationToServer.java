package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class BroomSyncRotationToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncRotationToServer> CODEC  = StreamCodec.ofMember(BroomSyncRotationToServer::encode, BroomSyncRotationToServer::new);
    public static final CustomPacketPayload.Type<BroomSyncRotationToServer> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync_rot_server"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    float deltaRotation;
    Vec3 deltaMovement;

    public BroomSyncRotationToServer(BroomEntity broom) {
        this.sourceId = broom.getId();
        this.deltaRotation = broom.deltaRotation;
        this.deltaMovement = broom.getDeltaMovement();
    }
    public BroomSyncRotationToServer(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.deltaRotation = buf.readFloat();
        this.deltaMovement = buf.readVec3();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeFloat(deltaRotation);
        buffer.writeVec3(deltaMovement);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getEntity(sourceId) instanceof BroomEntity broom) {
//            broom.deltaRotation = deltaRotation;
//            broom.setDeltaMovement(deltaMovement);
//            broom.syncDeltaRotation();
            HexereiPacketHandler.sendToNearbyClient(broom.level(), broom, new BroomSyncRotation(sourceId, deltaRotation, deltaMovement));
        }
    }
}
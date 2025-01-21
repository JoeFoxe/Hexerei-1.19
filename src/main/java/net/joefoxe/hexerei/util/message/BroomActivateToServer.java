package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BroomActivateToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomActivateToServer> CODEC  = StreamCodec.ofMember(BroomActivateToServer::encode, BroomActivateToServer::new);
    public static final Type<BroomActivateToServer> TYPE = new Type<>(HexereiUtil.getResource("broom_activate"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public BroomActivateToServer(int id) {
        this.sourceId = id;
    }

    public BroomActivateToServer(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(server.overworld().getEntity(sourceId) instanceof BroomEntity broom) {
            broom.activate();
        }
    }
}
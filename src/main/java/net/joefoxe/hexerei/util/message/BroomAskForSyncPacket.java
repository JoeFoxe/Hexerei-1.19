package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class BroomAskForSyncPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomAskForSyncPacket> CODEC  = StreamCodec.ofMember(BroomAskForSyncPacket::encode, BroomAskForSyncPacket::new);
    public static final CustomPacketPayload.Type<BroomAskForSyncPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_ask_for_sync"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public BroomAskForSyncPacket(int id) {
        this.sourceId = id;
    }

    public BroomAskForSyncPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(server.overworld().getEntity(sourceId) instanceof BroomEntity broom) {
            HexereiPacketHandler.sendToPlayerClient(new BroomSyncPacket(broom.getId(), broom.saveWithoutId(new CompoundTag())), player);
        }
    }
}
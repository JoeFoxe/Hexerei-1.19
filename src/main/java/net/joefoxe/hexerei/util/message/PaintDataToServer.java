package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.PaintData;
import net.joefoxe.hexerei.data.books.PaintSystemSavedData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PaintDataToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, PaintDataToServer> CODEC  = StreamCodec.ofMember(PaintDataToServer::encode, PaintDataToServer::new);
    public static final Type<PaintDataToServer> TYPE = new Type<>(HexereiUtil.getResource("paint_data_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    PaintData paintData;

    public PaintDataToServer(PaintData paintData) {
        this.paintData = paintData;
    }
    public PaintDataToServer(RegistryFriendlyByteBuf buf) {
        this.paintData = PaintData.STREAM_CODEC.decode(buf);

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        PaintData.STREAM_CODEC.encode(buffer, paintData);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        PaintSystemSavedData.get().putPaintData(paintData);
        //send message to update clients other than the player who sent it.
        HexereiPacketHandler.sendToAllPlayersBut(new ClientboundPaintData(paintData), server, player);
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.PaintSystemSavedData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AskForPaintDataToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AskForPaintDataToServer> CODEC  = StreamCodec.ofMember(AskForPaintDataToServer::encode, AskForPaintDataToServer::new);
    public static final Type<AskForPaintDataToServer> TYPE = new Type<>(HexereiUtil.getResource("ask_for_paint_data_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public AskForPaintDataToServer() {
    }
    public AskForPaintDataToServer(RegistryFriendlyByteBuf buf) {

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        PaintSystemSavedData.get().sendToClient(player);
    }
}
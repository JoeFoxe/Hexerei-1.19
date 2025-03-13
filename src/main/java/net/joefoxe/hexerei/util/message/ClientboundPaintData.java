package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ClientboundPaintData extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPaintData> CODEC  = StreamCodec.ofMember(ClientboundPaintData::encode, ClientboundPaintData::new);
    public static final Type<ClientboundPaintData> TYPE = new Type<>(HexereiUtil.getResource("clientbound_paint_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    PaintData paintData;

    public ClientboundPaintData(PaintData paintData) {
        this.paintData = paintData;
    }
    public ClientboundPaintData(RegistryFriendlyByteBuf buf) {
        this.paintData = PaintData.STREAM_CODEC.decode(buf);

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        PaintData.STREAM_CODEC.encode(buffer, paintData);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientPaintDataCache.store(paintData.page, paintData.uuid, paintData);
        ResourceLocation bookLoc = ResourceLocation.parse(paintData.page.toString().split("/")[0]);
        BookPage bookPage = BookManager.getBookPages(bookLoc, paintData.page);
        if (bookPage != null) {
            for(BookPaintElement paintElement : bookPage.paintElements) {
                paintElement.client.getPaintSystem(paintData.uuid).fromPaintData(paintData);
            }
        }
    }
}
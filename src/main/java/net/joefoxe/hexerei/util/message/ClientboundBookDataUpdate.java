package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ClientboundBookDataUpdate extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBookDataUpdate> CODEC  = StreamCodec.ofMember(ClientboundBookDataUpdate::encode, ClientboundBookDataUpdate::new);
    public static final Type<ClientboundBookDataUpdate> TYPE = new Type<>(HexereiUtil.getResource("book_data_update_client"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos bookAltar;
    BookData bookData;

    public ClientboundBookDataUpdate(BookOfShadowsAltarTile bookAltar, BookData bookData) {
        this.bookAltar = bookAltar.getBlockPos();
        this.bookData = bookData;
    }
    public ClientboundBookDataUpdate(RegistryFriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.bookData = BookData.STREAM_CODEC.decode(buf);

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(bookAltar);
        BookData.STREAM_CODEC.encode(buffer, bookData);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player.level().getBlockEntity(bookAltar) instanceof  BookOfShadowsAltarTile altar) {
            altar.currentBook = bookData;
            //update the BookWritableTextBoxes

            for (ResourceLocation book : BookManager.getBookLocations()) {
                BookEntries bookEntries = BookManager.getBookEntries(book);

                if (bookEntries != null) {
                    for (BookChapter bookChapter : bookEntries.chapterList) {
                        for (BookPageEntry bookPageEntry : bookChapter.pages) {

                            if (bookData.pageTexts().containsKey(bookPageEntry.location)) {
                                BookPage page = BookManager.getBookPages(book, ResourceLocation.parse(bookPageEntry.location));
                                if (page != null) {
                                    for (BookWritableTextBox bookWritableTextBox : page.writableTextBoxes) {
                                        bookWritableTextBox.client.clearDisplayCache(bookData.getUUID());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
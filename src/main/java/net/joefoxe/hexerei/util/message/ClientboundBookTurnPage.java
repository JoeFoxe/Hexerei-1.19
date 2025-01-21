package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ClientboundBookTurnPage extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBookTurnPage> CODEC  = StreamCodec.ofMember(ClientboundBookTurnPage::encode, ClientboundBookTurnPage::new);
    public static final Type<ClientboundBookTurnPage> TYPE = new Type<>(HexereiUtil.getResource("book_turn_page_client"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos bookAltar;
    int turnPage;
    int turnToChapter;
    int turnToPage;
    int chapter;
    int page;

    public ClientboundBookTurnPage(BookOfShadowsAltarTile bookAltar, int turnPage, int turnToChapter, int turnToPage, int chapter, int page) {
        this.bookAltar = bookAltar.getBlockPos();
        this.turnPage = turnPage;
        this.turnToChapter = turnToChapter;
        this.turnToPage = turnToPage;
        this.chapter = chapter;
        this.page = page;
    }
    public ClientboundBookTurnPage(RegistryFriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.turnPage = buf.readInt();
        this.turnToChapter = buf.readInt();
        this.turnToPage = buf.readInt();
        this.chapter = buf.readInt();
        this.page = buf.readInt();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(bookAltar);
        buffer.writeInt(turnPage);
        buffer.writeInt(turnToChapter);
        buffer.writeInt(turnToPage);
        buffer.writeInt(chapter);
        buffer.writeInt(page);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player.level().getBlockEntity(bookAltar) instanceof  BookOfShadowsAltarTile book) {
            if (turnPage == -2)
                turnPage += 2;
            book.turnPage = turnPage;
            book.turnToChapter = turnToChapter;
            book.turnToPage = turnToPage;
            book.currentBook = book.currentBook.setChapter(chapter);
            book.currentBook = book.currentBook.setPage(page);

            book.pageOneRotationRender = 0;
            book.pageOneRotation = 0;
            book.pageOneRotationTo = 0;
            book.pageOneRotationLast = book.pageOneRotation;
            book.pageTwoRotationRender = 0;
            book.pageTwoRotation = 0;
            book.pageTwoRotationTo = 0;
            book.pageTwoRotationLast = book.pageTwoRotation;
        }
    }
}
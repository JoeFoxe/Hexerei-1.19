package net.joefoxe.hexerei.data.books;

import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.BookEntriesPacket;
import net.joefoxe.hexerei.util.message.BookPagesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.LinkedHashMap;
import java.util.Map;

public class  BookManager {
    private static final Map<ResourceLocation, BookPage> BOOK_PAGES = new LinkedHashMap<>();

    private static final Map<String, BookHyperlink> BOOK_ITEM_HYPERLINKS = new LinkedHashMap<>();
    private static BookEntries BOOK_ENTRIES = null;

    public static void clearBookPages() {
        BOOK_PAGES.clear();
    }

    public static void clearBookEntries() {
        BOOK_ENTRIES = null;
    }

    public static void addBookPage(ResourceLocation loc, BookPage bookPage) {
        BOOK_PAGES.put(loc, bookPage);
    }

    public static void addBookItemHyperlink(String loc, BookHyperlink hyperlink) {
        BOOK_ITEM_HYPERLINKS.put(loc, hyperlink);
    }

    public static void addBookEntries(BookEntries bookEntry) {
        BOOK_ENTRIES = bookEntry;
    }

    public static void sendBookPagesToClient() {
        if (ServerLifecycleHooks.getCurrentServer() != null)
            HexereiPacketHandler.sendToAllPlayers(new BookPagesPacket(BOOK_PAGES), ServerLifecycleHooks.getCurrentServer());
    }

    public static void sendBookEntriesToClient() {
        if (ServerLifecycleHooks.getCurrentServer() != null)
            HexereiPacketHandler.sendToAllPlayers(new BookEntriesPacket(BOOK_ENTRIES), ServerLifecycleHooks.getCurrentServer());
    }

    public static void sendBookPagesToClient(ServerPlayer player) {
        HexereiPacketHandler.sendToPlayerClient(new BookPagesPacket(BOOK_PAGES), player);
    }

    public static void sendBookEntriesToClient(ServerPlayer player) {
        HexereiPacketHandler.sendToPlayerClient(new BookEntriesPacket(BOOK_ENTRIES), player);
    }

    public static BookPage getBookPages(ResourceLocation loc){

        if(BOOK_PAGES.containsKey(loc)) {
            return BOOK_PAGES.get(loc);
        }

        return null;
    }

    public static BookEntries getBookEntries(){
        return BOOK_ENTRIES;
    }

    public static Map<String, BookHyperlink>  getBookItemHyperlinks(){
        return BOOK_ITEM_HYPERLINKS;
    }

}

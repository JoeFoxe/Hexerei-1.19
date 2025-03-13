package net.joefoxe.hexerei.data.books;

import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.BookEntriesPacket;
import net.joefoxe.hexerei.util.message.BookPagesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public class  BookManager {
    private static final Map<ResourceLocation, Map<ResourceLocation, BookPage>> BOOK_PAGES = new LinkedHashMap<>();

    private static final Map<String, BookHyperlink> BOOK_ITEM_HYPERLINKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BookEntries> BOOK_ENTRIES = new HashMap<>();

    public static List<ResourceLocation> getBookLocations() {
        List<ResourceLocation> list = new ArrayList<>();
        BOOK_ENTRIES.forEach(((resourceLocation, bookEntries) -> list.add(resourceLocation)));
        return list;
    }

    public static void clearBookPages(ResourceLocation locationToClear) {
        if (BOOK_PAGES.containsKey(locationToClear))
            BOOK_PAGES.get(locationToClear).clear();
//        BOOK_PAGES.clear();
    }

    public static void clearBookEntries(ResourceLocation locationToClear) {
        BOOK_ENTRIES.remove(locationToClear);
    }

    public static void addBookPage(ResourceLocation bookLoc, ResourceLocation loc, BookPage bookPage) {
        if (!BOOK_PAGES.containsKey(bookLoc)) {
            BOOK_PAGES.put(bookLoc, new HashMap<>());
            BOOK_PAGES.get(bookLoc).put(loc, bookPage);
        } else {
            BOOK_PAGES.get(bookLoc).put(loc, bookPage);
        }
    }

    public static void addBookItemHyperlink(String loc, BookHyperlink hyperlink) {
        BOOK_ITEM_HYPERLINKS.put(loc, hyperlink);
    }

    public static void addBookEntries(BookEntries bookEntry) {
        BOOK_ENTRIES.put(bookEntry.book, bookEntry);
    }

    public static void sendBookPagesToClient(List<ResourceLocation> books) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {

            Map<ResourceLocation, Map<ResourceLocation, BookPage>> booksToSend = new HashMap<>();
            for(ResourceLocation book : books) {
                if (BOOK_PAGES.containsKey(book))
                    booksToSend.put(book, BOOK_PAGES.get(book));
            }
            HexereiPacketHandler.sendToAllPlayers(new BookPagesPacket(booksToSend), ServerLifecycleHooks.getCurrentServer());
        }
    }

    public static void sendBookEntriesToClient(List<ResourceLocation> books) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            Map<ResourceLocation, BookEntries> booksToSend = new HashMap<>();
            for(ResourceLocation book : books) {
                if (BOOK_ENTRIES.containsKey(book))
                    booksToSend.put(book, BOOK_ENTRIES.get(book));
            }
            HexereiPacketHandler.sendToAllPlayers(new BookEntriesPacket(booksToSend), ServerLifecycleHooks.getCurrentServer());
        }
    }

    public static void sendBookPagesToClient(ServerPlayer player) {
        HexereiPacketHandler.sendToPlayerClient(new BookPagesPacket(BOOK_PAGES), player);
    }

    public static void sendBookEntriesToClient(ServerPlayer player) {
        HexereiPacketHandler.sendToPlayerClient(new BookEntriesPacket(BOOK_ENTRIES), player);
    }

    public static BookPage getBookPages(ResourceLocation bookLoc, ResourceLocation loc){

        if(BOOK_PAGES.containsKey(bookLoc) && BOOK_PAGES.get(bookLoc).containsKey(loc)) {
            return BOOK_PAGES.get(bookLoc).get(loc);
        }

        return null;
    }

    public static BookEntries getBookEntries(ResourceLocation bookLoc){
        if (BOOK_ENTRIES.containsKey(bookLoc))
            return BOOK_ENTRIES.get(bookLoc);
        return null;
    }

    public static Map<String, BookHyperlink>  getBookItemHyperlinks(){
        return BOOK_ITEM_HYPERLINKS;
    }

}

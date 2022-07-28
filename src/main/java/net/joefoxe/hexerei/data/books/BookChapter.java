package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

public class BookChapter {
    public ArrayList<BookPageEntry> pages;
    public String name;
    public int startPage;
    public int endPage;

    BookChapter(ArrayList<BookPageEntry> pages, String name, int startPage, int endPage){
        this.pages = pages;
        this.name = name;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public static BookChapter deserialize(JsonObject object, int numOfPages) {

        JsonArray yourJson = GsonHelper.getAsJsonArray(object,"pages");

        ArrayList<BookPageEntry> yourList = new ArrayList<>();
        for (int i = 0; i < yourJson.size(); i++) {
            JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
            yourList.add(BookPageEntry.deserialize(extraItemObject, numOfPages + i));
        }
        String string = GsonHelper.getAsString(object, "name", "empty");
        return new BookChapter(yourList, string, numOfPages, numOfPages + yourJson.size());
    }
}

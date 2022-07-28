package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class BookPageEntry {
    public String location;
    public int pageNum;

    BookPageEntry(String location, int pageNum){
        this.location = location;
        this.pageNum = pageNum;
    }

    public static BookPageEntry deserialize(JsonObject object, int pageNum) {

        String string = GsonHelper.getAsString(object, "page_location", "empty");
        return new BookPageEntry(string, pageNum);
    }
}

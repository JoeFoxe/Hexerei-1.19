package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

public class BookParagraph {
    public ArrayList<BookParagraphElements> paragraphElements;
    public String passage;
    public String align;

    public MutableComponent translatablePassage;

    BookParagraph(ArrayList<BookParagraphElements> paragraphElements, String passage, String align){
        this.paragraphElements = paragraphElements;
        this.passage = passage;
        this.translatablePassage = Component.translatable(passage);
        this.align = align;
    }

    public static BookParagraph deserialize(JsonObject object) {

        JsonArray yourJson = GsonHelper.getAsJsonArray(object,"paragraph_box");

        ArrayList<BookParagraphElements> yourList = new ArrayList<>();
        for (int i = 0; i < yourJson.size(); i++) {
            JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
            yourList.add(BookParagraphElements.deserialize(extraItemObject));
        }
        String string = GsonHelper.getAsString(object, "passage_text", "empty");
        String align = GsonHelper.getAsString(object, "align", "left");
        return new BookParagraph(yourList, string, align);
    }
}

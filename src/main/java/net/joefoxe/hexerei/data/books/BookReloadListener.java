package net.joefoxe.hexerei.data.books;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Map;

public class BookReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public BookReloadListener() {
        super(GSON, "book");
    }

//    public static BookPage deserialize(JsonObject object) {
////        Ingredient input = object.has("ingredient_list") ? Ingredient.fromJson(object.get("ingredient_list")) : Ingredient.fromJson(object);
//        int count = GsonHelper.getAsString(object, "passage_text");
//        return new BookPage("Hexerei", count);
//    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller profile) {

        BookManager.clearBookPages();

        jsons.forEach((key, input) -> {
            if (input != null) {
                try {
                    String keyString = key.getPath();
                    if(!keyString.equals("book_entries")){
                        addBookPage(key, input);
                    }else{
                        addBookEntries(key, input);
                    }

                } catch (Exception e) {
                    Hexerei.LOGGER.error("Failed to parse JSON object for book page " + key);
                }
            }
        });

        if (EffectiveSide.get().isServer() && ServerLifecycleHooks.getCurrentServer() != null) {
            BookManager.sendBookEntriesToClient();
            BookManager.sendBookPagesToClient();
        }
    }

    private static void addBookPage(ResourceLocation key, JsonElement input) throws CommandSyntaxException {
        JsonObject jsonObject = input.getAsJsonObject();
        String title = GsonHelper.getAsString(jsonObject, "showTitle", "none");

        ArrayList<BookParagraph> paragraphsList = new ArrayList<>();
        if (jsonObject.has("paragraphs")) {
            JsonArray paragraphs = GsonHelper.getAsJsonArray(jsonObject, "paragraphs");
            for (int i = 0; i < paragraphs.size(); i++) {
                JsonObject obj = paragraphs.get(i).getAsJsonObject();
                paragraphsList.add(BookParagraph.deserialize(obj));
            }
        }

        ArrayList<BookItemsAndFluids> itemsInSlotsList = new ArrayList<>();
        if (jsonObject.has("items_and_fluids")) {
            JsonArray itemsAndFluids = GsonHelper.getAsJsonArray(jsonObject, "items_and_fluids");
            for (int i = 0; i < itemsAndFluids.size(); i++) {
                JsonObject obj = itemsAndFluids.get(i).getAsJsonObject();

                itemsInSlotsList.add(BookItemsAndFluids.deserialize(obj));
            }
        }

        ArrayList<BookEntity> entityList = new ArrayList<>();
        if (jsonObject.has("entities")) {
            JsonArray entity = GsonHelper.getAsJsonArray(jsonObject, "entities");
            for (int i = 0; i < entity.size(); i++) {
                JsonObject obj = entity.get(i).getAsJsonObject();

                entityList.add(BookEntity.deserialize(obj));
            }
        }

        ArrayList<BookImage> imagesList = new ArrayList<>();
        if (jsonObject.has("images")) {
            JsonArray images = GsonHelper.getAsJsonArray(jsonObject, "images");
            for (int i = 0; i < images.size(); i++) {
                JsonObject obj = images.get(i).getAsJsonObject();

                imagesList.add(BookImage.deserialize(obj));
            }
        }

        ArrayList<BookNonItemTooltip> nonItemTooltipsList = new ArrayList<>();
        if (jsonObject.has("non_item_tooltips")) {
            JsonArray non_item_tooltips = GsonHelper.getAsJsonArray(jsonObject, "non_item_tooltips");
            for (int i = 0; i < non_item_tooltips.size(); i++) {
                JsonObject obj = non_item_tooltips.get(i).getAsJsonObject();

                nonItemTooltipsList.add(BookNonItemTooltip.deserialize(obj));
            }
        }

        String itemHyperlink = GsonHelper.getAsString(jsonObject, "item_hyperlink", "none");
//        if(!itemHyperlink.equals("none"))
//            BookManager.addBookItemHyperlink(itemHyperlink, )

        BookManager.addBookPage(key, new BookPage(title, paragraphsList, itemsInSlotsList, entityList, imagesList, nonItemTooltipsList, itemHyperlink));
    }

    private static void addBookEntries(ResourceLocation key, JsonElement input) throws CommandSyntaxException {
        JsonObject jsonObject = input.getAsJsonObject();
        int numberOfPages = 0;

        ArrayList<BookChapter> chaptersList = new ArrayList<>();
        if (jsonObject.has("chapters")) {
            JsonArray chapters = GsonHelper.getAsJsonArray(jsonObject, "chapters");
            for (int i = 0; i < chapters.size(); i++) {
                JsonObject obj = chapters.get(i).getAsJsonObject();
                BookChapter bookChapter = BookChapter.deserialize(obj, numberOfPages);
                numberOfPages += bookChapter.pages.size();
                chaptersList.add(bookChapter);
            }
        }

        BookManager.addBookEntries(new BookEntries(chaptersList, numberOfPages));
    }
}

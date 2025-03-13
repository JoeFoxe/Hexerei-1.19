package net.joefoxe.hexerei.data.books;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.AskForEntriesAndPagesPacket;
import net.joefoxe.hexerei.util.message.AskForPaintDataToServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static boolean askForUpdate = false;

    public BookReloadListener() {
        super(GSON, "book");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller profile) {

        List<ResourceLocation> books = new ArrayList<>();

        jsons.forEach((key, input) -> {
            if (input != null) {
                try {
                    ResourceLocation bookLoc = ResourceLocation.parse(key.toString().split("/")[0]);
                    if (!books.contains(bookLoc)) {
                        BookManager.clearBookPages(bookLoc);
                        books.add(bookLoc);
                    }
                    String keyString = key.getPath();
                    if(keyString.equals(bookLoc.getPath() + "/" + bookLoc.getPath())){
                        addBookEntries(bookLoc, input);
                    }else{
                        addBookPage(bookLoc, key, input);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Hexerei.LOGGER.error("Failed to parse JSON object for book page {}", key);
                }
            }
        });

        if (!FMLEnvironment.dist.isClient()) {
            BookManager.sendBookEntriesToClient(books);
            BookManager.sendBookPagesToClient(books);

            PaintSystemSavedData.sendToClients();
        } else {
            askForUpdate = true;
        }
    }

    private static void addBookPage(ResourceLocation bookLoc, ResourceLocation key, JsonElement input) {
        JsonObject jsonObject = input.getAsJsonObject();
        String name = GsonHelper.getAsString(jsonObject, "name", "");

        ArrayList<BookParagraph> paragraphsList = new ArrayList<>();
        if (jsonObject.has("paragraphs")) {
            JsonArray paragraphs = GsonHelper.getAsJsonArray(jsonObject, "paragraphs");
            for (int i = 0; i < paragraphs.size(); i++) {
                JsonObject obj = paragraphs.get(i).getAsJsonObject();
                paragraphsList.add(BookParagraph.CODEC.decode(JsonOps.INSTANCE, obj).getOrThrow().getFirst());

            }
        }

        ArrayList<BookWritableTextBox> writableTextBoxes = new ArrayList<>();
        if (jsonObject.has("writable_text_box")) {
            JsonArray paragraphs = GsonHelper.getAsJsonArray(jsonObject, "writable_text_box");
            for (int i = 0; i < paragraphs.size(); i++) {
                JsonObject obj = paragraphs.get(i).getAsJsonObject();
                BookWritableTextBox box = BookWritableTextBox.CODEC.decode(JsonOps.INSTANCE, obj).getOrThrow().getFirst();
                box.parentLocation = key;
                writableTextBoxes.add(box);

            }
        }

        ArrayList<BookPaintElement> paintElements = new ArrayList<>();
        if (jsonObject.has("paint_element")) {
            JsonArray paragraphs = GsonHelper.getAsJsonArray(jsonObject, "paint_element");
            for (int i = 0; i < paragraphs.size(); i++) {
                JsonObject obj = paragraphs.get(i).getAsJsonObject();
                BookPaintElement paint = BookPaintElement.CODEC.decode(JsonOps.INSTANCE, obj).getOrThrow().getFirst();
                paint.parentLocation = key;
                paintElements.add(paint);

            }
        }

        ArrayList<BookItemsAndFluids> itemsInSlotsList = new ArrayList<>();
        if (jsonObject.has("items_and_fluids")) {
            JsonArray itemsAndFluids = GsonHelper.getAsJsonArray(jsonObject, "items_and_fluids");
            for (int i = 0; i < itemsAndFluids.size(); i++) {
                JsonObject obj = itemsAndFluids.get(i).getAsJsonObject();

                Optional<Pair<BookItemsAndFluids, JsonElement>> optional = BookItemsAndFluids.CODEC.decode(JsonOps.INSTANCE, obj).result();
                optional.ifPresent(bookItemsAndFluidsJsonElementPair -> itemsInSlotsList.add(bookItemsAndFluidsJsonElementPair.getFirst()));
            }
        }

        ArrayList<BookBlocks> blocksList = new ArrayList<>();
        if (jsonObject.has("blocks")) {
            JsonArray blocks = GsonHelper.getAsJsonArray(jsonObject, "blocks");
            for (int i = 0; i < blocks.size(); i++) {
                JsonObject obj = blocks.get(i).getAsJsonObject();

                Optional<Pair<BookBlocks, JsonElement>> optional = BookBlocks.CODEC.decode(JsonOps.INSTANCE, obj).result();
                optional.ifPresent(bookBlocksPair -> blocksList.add(bookBlocksPair.getFirst()));

            }
        }

        ArrayList<BookEntity> entityList = new ArrayList<>();
        if (jsonObject.has("entities")) {
            JsonArray entity = GsonHelper.getAsJsonArray(jsonObject, "entities");
            for (int i = 0; i < entity.size(); i++) {
                JsonObject obj = entity.get(i).getAsJsonObject();

                Optional<Pair<BookEntity, JsonElement>> optional = BookEntity.CODEC.decode(JsonOps.INSTANCE, obj).result();
                optional.ifPresent(bookEntityPair -> entityList.add(bookEntityPair.getFirst()));

            }
        }

        ArrayList<BookImage> imagesList = new ArrayList<>();
        if (jsonObject.has("images")) {
            JsonArray images = GsonHelper.getAsJsonArray(jsonObject, "images");
            for (int i = 0; i < images.size(); i++) {
                JsonObject obj = images.get(i).getAsJsonObject();

                Optional<Pair<BookImage, JsonElement>> optional = BookImage.CODEC.decode(JsonOps.INSTANCE, obj).result();
                optional.ifPresent(bookImagePair -> imagesList.add(bookImagePair.getFirst()));

            }
        }

        ArrayList<BookNonItemTooltip> nonItemTooltipsList = new ArrayList<>();
        if (jsonObject.has("non_item_tooltips")) {
            JsonArray non_item_tooltips = GsonHelper.getAsJsonArray(jsonObject, "non_item_tooltips");
            for (int i = 0; i < non_item_tooltips.size(); i++) {
                JsonObject obj = non_item_tooltips.get(i).getAsJsonObject();

                Optional<Pair<BookNonItemTooltip, JsonElement>> optional = BookNonItemTooltip.CODEC.decode(JsonOps.INSTANCE, obj).result();
                optional.ifPresent(bookNonItemTooltip -> nonItemTooltipsList.add(bookNonItemTooltip.getFirst()));

            }
        }

        String itemHyperlink = GsonHelper.getAsString(jsonObject, "item_hyperlink", "none");

        BookPage page = new BookPage(name, itemHyperlink, paragraphsList, itemsInSlotsList, blocksList, entityList, imagesList, nonItemTooltipsList, writableTextBoxes, paintElements);

        page.location = key;
        BookManager.addBookPage(bookLoc, key, page);
    }



    private static void addBookEntries(ResourceLocation bookLoc, JsonElement input) {
        JsonObject jsonObject = input.getAsJsonObject();
        int numberOfPages = 0;

        ArrayList<BookChapter> chaptersList = new ArrayList<>();
        if (jsonObject.has("chapters")) {
            JsonArray chapters = GsonHelper.getAsJsonArray(jsonObject, "chapters");
            for (int i = 0; i < chapters.size(); i++) {
                JsonObject obj = chapters.get(i).getAsJsonObject();
                BookChapter bookChapter = BookChapter.deserialize(i, obj, numberOfPages);
                numberOfPages += bookChapter.pages.size();
                chaptersList.add(bookChapter);
            }
        }

        BookManager.addBookEntries(new BookEntries(bookLoc, chaptersList, numberOfPages));
    }
}

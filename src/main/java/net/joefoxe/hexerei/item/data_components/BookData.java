package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record BookData(UUID uuid, ResourceLocation book, int chapter, int page, boolean opened, Bookmarks bookmarks, Map<String, PageText> pageTexts) {
    public static final UUID EMPTY_UUID = new UUID(0, 0); // A UUID with all zeros
    public static final BookData EMPTY = new BookData(EMPTY_UUID, HexereiUtil.getResource("book_of_shadows"), 0, 0, false, new Bookmarks(IntStream.range(0, 20) .mapToObj(index -> new Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())), new HashMap<>());
    public static final BookData EMPTY_NOTEBOOK = new BookData(EMPTY_UUID, HexereiUtil.getResource("notebook"), 0, 0, false, new Bookmarks(IntStream.range(0, 20) .mapToObj(index -> new Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())), new HashMap<>());
    public static final Function<ResourceLocation, BookData> EMPTY_AS = (resourceLoc) -> new BookData(EMPTY_UUID, resourceLoc, 0, 0, false, new Bookmarks(IntStream.range(0, 20) .mapToObj(index -> new Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())), new HashMap<>());

    public static class PageText {
        public Map<String, String> pageTexts;

        public Map<String, String> getPageTexts() {
            return pageTexts;
        }

        public static final Codec<PageText> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("text", new HashMap<>()).forGetter(PageText::getPageTexts)
        ).apply(instance, PageText::new));

//        public static final StreamCodec<ByteBuf, PageText> STREAM_CODEC = PageText.Slot.STREAM_CODEC
//                .apply(ByteBufCodecs.list())
//                .map(Bookmarks::new, contents -> contents.slots);

        public PageText(Map<String, String> pageTexts) {
            this.pageTexts = pageTexts;
        }
    }

    // Codec for the inner map: Map<Integer, String>
    private static final Codec<Map<String, String>> INNER_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING);

    // Codec for the outer map: Map<String, Map<Integer, String>>
    public static final Codec<Map<String, Map<String, String>>> MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, INNER_MAP_CODEC);

    public static final Codec<BookData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            UUIDUtil.CODEC.optionalFieldOf("uuid", EMPTY_UUID).forGetter(BookData::uuid),
                            ResourceLocation.CODEC.fieldOf("book").forGetter(BookData::getBook),
                            Codec.INT.fieldOf("chapter").forGetter(BookData::getChapter),
                            Codec.INT.fieldOf("page").forGetter(BookData::getPage),
                            Codec.BOOL.fieldOf("opened").forGetter(BookData::isOpened),
                            Bookmarks.CODEC.fieldOf("bookmarks").forGetter(BookData::getBookmarks),
                            Codec.unboundedMap(Codec.STRING, PageText.CODEC).optionalFieldOf("pageTexts", new HashMap<>()).forGetter(BookData::pageTexts)
                    ).apply(instance, BookData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BookData> STREAM_CODEC = StreamCodec.of(
            BookData::toNetwork, BookData::fromNetwork
    );

    private static BookData fromNetwork(RegistryFriendlyByteBuf buffer) {
        UUID uuid = UUIDUtil.STREAM_CODEC.decode(buffer);
        ResourceLocation book = ResourceLocation.STREAM_CODEC.decode(buffer);
        int chapter = ByteBufCodecs.INT.decode(buffer);
        int page = ByteBufCodecs.INT.decode(buffer);
        boolean opened = ByteBufCodecs.BOOL.decode(buffer);
        Bookmarks bookmarks = Bookmarks.STREAM_CODEC.decode(buffer);

        // Deserialize pageTexts
        Map<String, PageText> pageTexts = new HashMap<>();
        int outerSize = buffer.readInt();
        for (int i = 0; i < outerSize; i++) {
            String outerKey = buffer.readUtf();
            int innerSize = buffer.readInt();

            Map<String, String> innerMap = new HashMap<>();
            for (int j = 0; j < innerSize; j++) {
                String innerKey = buffer.readUtf();
//                int innerKey = Integer.parseInt();
                String innerValue = buffer.readUtf();
                innerMap.put(innerKey, innerValue);
            }
            pageTexts.put(outerKey, new PageText(innerMap));
        }

        return new BookData(uuid, book, chapter, page, opened, bookmarks, pageTexts);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, BookData bookData) {


        UUIDUtil.STREAM_CODEC.encode(buffer, bookData.uuid);
        ResourceLocation.STREAM_CODEC.encode(buffer, bookData.book);
        ByteBufCodecs.INT.encode(buffer, bookData.chapter);
        ByteBufCodecs.INT.encode(buffer, bookData.page);
        ByteBufCodecs.BOOL.encode(buffer, bookData.opened);
        Bookmarks.STREAM_CODEC.encode(buffer, bookData.bookmarks);

//        Map<String, Map<String, String>> pageTexts = new HashMap<>();
//        Map<String, String> innerMap = new HashMap<>();
//        innerMap.put("1", "Page 1 Text");
//        innerMap.put("2", "Page 2 Text");
//        pageTexts.put("Chapter1", innerMap);
//
//        buffer.writeInt(pageTexts.size());
//        pageTexts.forEach(((s1, map) -> {
//            buffer.writeUtf(s1);
//            buffer.writeInt(map.size());
//            map.forEach(((integer, s) -> {
//                buffer.writeUtf(integer);
//                buffer.writeUtf(s);
//            }));
//        }));


        buffer.writeInt(bookData.pageTexts.size());
        bookData.pageTexts.forEach(((s1, map) -> {
            buffer.writeUtf(s1);
            buffer.writeInt(map.pageTexts.size());
            map.pageTexts.forEach(((ss, s) -> {
                buffer.writeUtf(ss);
                buffer.writeUtf(s);
            }));
        }));

    }

    // StreamCodec for network synchronization
//    public static final StreamCodec<ByteBuf, BookData> STREAM_CODEC = StreamCodec.composite(
//            UUIDUtil.STREAM_CODEC, BookData::uuid,
//            ResourceLocation.STREAM_CODEC, BookData::book,
//            ByteBufCodecs.INT, BookData::chapter,
//            ByteBufCodecs.INT, BookData::page,
//            ByteBufCodecs.BOOL, BookData::opened,
//            Bookmarks.STREAM_CODEC, BookData::bookmarks,
//            ByteBufCodecs.map(
//                    HashMap::new, // Map factory
//                    ByteBufCodecs.STRING_UTF8, // Key codec (pageId)
//                    ByteBufCodecs.map(
//                            HashMap::new, // Inner map factory
//                            ByteBufCodecs.INT, // Key codec (textBoxIndex)
//                            ByteBufCodecs.STRING_UTF8 // Value codec (text)
//                    )
//            ), BookData::pageTexts,
//            BookData::new
//    );

    // Helper method to update text for a specific text box on a page
    public BookData updateTextBoxText(String pageId, int textBoxIndex, String text) {
        Map<String, PageText> newPageTexts = new HashMap<>(this.pageTexts);

        // Get or create the inner map for the page
        PageText textBoxTexts;
        if (newPageTexts.containsKey(pageId))
            textBoxTexts = new PageText(new HashMap<>(newPageTexts.get(pageId).pageTexts));
        else
            textBoxTexts = new PageText(new HashMap<>());

//        = newPageTexts.computeIfAbsent(pageId, k -> new PageText(new HashMap<>()));

        if (textBoxTexts.pageTexts == null)
            textBoxTexts.pageTexts = new HashMap<>();
        // Update the text for the specific text box
        textBoxTexts.pageTexts.put(String.valueOf(textBoxIndex), text);

        newPageTexts.put(pageId, textBoxTexts);

        return new BookData(this.uuid, this.book, this.chapter, this.page, this.opened, this.bookmarks, newPageTexts);
    }

    // Helper method to get text for a specific text box on a page
    public String getTextBoxText(String pageId, int textBoxIndex) {
        PageText textBoxTexts = this.pageTexts.get(pageId);
        if (textBoxTexts != null) {
            if (textBoxTexts.pageTexts.containsKey(String.valueOf(textBoxIndex)))
                return textBoxTexts.pageTexts.get(String.valueOf(textBoxIndex));
        }
        return ""; // Return empty string if no text is found
    }

    public ResourceLocation getBook() {
        return book;
    }

    public int getChapter() {
        return chapter;
    }

    public int getPage() {
        return page;
    }

    public boolean isOpened() {
        return opened;
    }

    public Bookmarks getBookmarks() {
        return bookmarks;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BookData setUUID(UUID uuid) {
        return new BookData(uuid, this.book, this.chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
    }

    public BookData setBookmarks(Bookmarks bookmarks) {
        return new BookData(this.uuid, this.book, this.chapter, this.page, this.opened, bookmarks, this.pageTexts);
    }

    public BookData setChapter(int chapter) {
        return new BookData(this.uuid, this.book, chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
    }

    public BookData setPage(int page) {
        return new BookData(this.uuid, this.book, this.chapter, page, this.opened, this.bookmarks, this.pageTexts);
    }

    public BookData setOpened(boolean opened) {
        return new BookData(this.uuid, this.book, this.chapter, this.page, opened, this.bookmarks, this.pageTexts);
    }

    public BookData setBook(ResourceLocation book) {
        return new BookData(this.uuid, book, this.chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
    }

    public static class Bookmarks {

        List<Slot> slots;

        public static final Codec<Bookmarks> CODEC = Slot.CODEC.listOf().xmap(Bookmarks::new, contents -> contents.slots);

        public static final StreamCodec<ByteBuf, Bookmarks> STREAM_CODEC = Slot.STREAM_CODEC
                .apply(ByteBufCodecs.list())
                .map(Bookmarks::new, contents -> contents.slots);

        public Bookmarks(List<Slot> slots) {
            this.slots = slots;
        }

        public List<Slot> getSlots() {
            return slots;
        }

        public Slot getSlot(int index) {
            return this.slots.get(index);
        }

        public void setSlots(List<Slot> slots) {
            this.slots = slots;
        }

        public void setSlot(int index, Slot slot) {
            this.slots.set(index, slot);
        }


        public static class Slot {

            String id;
            DyeColor color;
            int index;

            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("id").forGetter(Slot::getId),
                            DyeColor.CODEC.fieldOf("color").forGetter(Slot::getColor),
                            Codec.INT.fieldOf("index").forGetter(Slot::getIndex)
                    ).apply(instance, Slot::new)
            );

            public Slot(String id, DyeColor color, int index) {
                this.id = id;
                this.color = color;
                this.index = index;
            }

            public String getId() {
                return id;
            }

            public DyeColor getColor() {
                return color;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setColor(DyeColor color) {
                this.color = color;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public static StreamCodec<ByteBuf, Slot> STREAM_CODEC = ByteBufCodecs.fromCodec(Slot.CODEC);

            public Slot copy(){
                return new Slot(this.id, this.color, this.index);
            }

            public Slot copyWithIndex(int index){
                return new Slot(this.id, this.color, index);
            }

        }

    }
}
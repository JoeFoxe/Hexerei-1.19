package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BookHyperlink {
    public int chapter;
    public int page;
    public String id;
    public String url;

    public static BookHyperlink EMPTY = new BookHyperlink(-1, -1);

    public BookHyperlink(int chapter, int page){
        this.chapter = chapter;
        this.page = page;
        this.id = "";
        this.url = "";
    }
    public BookHyperlink(int chapter, int page, String id, String url) {
        this.chapter = chapter;
        this.page = page;
        this.id = id;
        this.url = url;
    }
    public static final Codec<BookHyperlink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("chapter", 0).forGetter(e -> e.chapter),
            Codec.INT.optionalFieldOf("page", 0).forGetter(e -> e.page),
            Codec.STRING.optionalFieldOf("id", "").forGetter(e -> e.id),
            Codec.STRING.optionalFieldOf("url", "").forGetter(e -> e.url)
    ).apply(instance, BookHyperlink::new));

}

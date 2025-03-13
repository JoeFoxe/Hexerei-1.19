package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.List;

public class BookParagraph {
    public List<BookParagraphElements> paragraphElements;
    public String passage;
    public String align;

    public MutableComponent translatablePassage;

    BookParagraph(List<BookParagraphElements> paragraphElements, String passage, String align){
        this.paragraphElements = paragraphElements;
        this.passage = passage;
        this.translatablePassage = Component.translatable(passage);
        this.align = align;
    }

    public static final Codec<BookParagraph> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BookParagraphElements.CODEC.listOf().fieldOf("paragraph_box").forGetter(p -> p.paragraphElements),
                    Codec.STRING.optionalFieldOf("passage_text", "empty").forGetter(p -> p.passage),
                    Codec.STRING.optionalFieldOf("align", "left").forGetter(p -> p.align)
                ).apply(instance, BookParagraph::new));
}

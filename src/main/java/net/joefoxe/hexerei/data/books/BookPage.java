package net.joefoxe.hexerei.data.books;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class BookPage {

    public ResourceLocation location = null;

    public String name;
    public String itemHyperlink;
    public ArrayList<BookParagraph> paragraph;
    public ArrayList<BookItemsAndFluids> itemList;
    public ArrayList<BookBlocks> blockList;
    public ArrayList<BookEntity> entityList;
    public ArrayList<BookImage> imageList;
    public ArrayList<BookNonItemTooltip> nonItemTooltipList;
    public ArrayList<BookWritableTextBox> writableTextBoxes;
    public ArrayList<BookPaintElement> paintElements;

    public BookPage(String name, String itemHyperlink, List<BookParagraph> paragraph, List<BookItemsAndFluids> itemList,
                    List<BookBlocks> blockList, List<BookEntity> entityList, List<BookImage> imageList, List<BookNonItemTooltip> nonItemTooltipList,
                    List<BookWritableTextBox> writableTextBoxes, List<BookPaintElement> paintElements) {
        this.name = name;
        this.itemHyperlink = itemHyperlink;
        this.paragraph = paragraph != null ? new ArrayList<>(paragraph) : new ArrayList<>();
        this.itemList = itemList != null ? new ArrayList<>(itemList) : new ArrayList<>();
        this.blockList = blockList != null ? new ArrayList<>(blockList) : new ArrayList<>();
        this.entityList = entityList != null ? new ArrayList<>(entityList) : new ArrayList<>();
        this.imageList = imageList != null ? new ArrayList<>(imageList) : new ArrayList<>();
        this.nonItemTooltipList = nonItemTooltipList != null ? new ArrayList<>(nonItemTooltipList) : new ArrayList<>();
        this.writableTextBoxes = writableTextBoxes != null ? new ArrayList<>(writableTextBoxes) : new ArrayList<>();
        this.paintElements = paintElements != null ? new ArrayList<>(paintElements) : new ArrayList<>();
    }

    public static final Codec<BookPage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(e -> e.name),
            Codec.STRING.fieldOf("itemHyperlink").forGetter(e -> e.itemHyperlink),
            BookParagraph.CODEC.listOf().fieldOf("paragraph").forGetter(e -> e.paragraph),
            BookItemsAndFluids.CODEC.listOf().fieldOf("itemList").forGetter(e -> e.itemList),
            BookBlocks.CODEC.listOf().fieldOf("blockList").forGetter(e -> e.blockList),
            BookEntity.CODEC.listOf().fieldOf("entityList").forGetter(e -> e.entityList),
            BookImage.CODEC.listOf().fieldOf("imageList").forGetter(e -> e.imageList),
            BookNonItemTooltip.CODEC.listOf().fieldOf("nonItemTooltipList").forGetter(e -> e.nonItemTooltipList),
            BookWritableTextBox.CODEC.listOf().fieldOf("writableTextBoxes").forGetter(e -> e.writableTextBoxes),
            BookPaintElement.CODEC.listOf().fieldOf("paintElements").forGetter(e -> e.paintElements)
    ).apply(instance, BookPage::new));

    public static CompoundTag saveToTag(BookPage bookPage) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, bookPage)
                .resultOrPartial(System.err::println)
                .orElse(new CompoundTag());
    }

    public static BookPage loadFromTag(CompoundTag tag) {
        return CODEC.decode(NbtOps.INSTANCE, tag)
                .resultOrPartial(System.err::println)
                .map(Pair::getFirst)
                .orElse(new BookPage("", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
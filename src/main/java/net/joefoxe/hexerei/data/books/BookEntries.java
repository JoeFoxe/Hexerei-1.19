package net.joefoxe.hexerei.data.books;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class BookEntries {
    public ResourceLocation book;
    public ArrayList<BookChapter> chapterList;
    public int numberOfPages;



    public BookEntries(ResourceLocation book, ArrayList<BookChapter> chapterList, int numberOfPages) {
        this.chapterList = chapterList;
        this.numberOfPages = numberOfPages;
        this.book = book;
    }

    public static CompoundTag saveToTag(BookEntries bookEntries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("bookId", bookEntries.book.toString());
        tag.putInt("numberOfChapters", bookEntries.chapterList.size());
        for(int i = 0; i < bookEntries.chapterList.size(); i++) {
            BookChapter bookChapter = bookEntries.chapterList.get(i);
            CompoundTag compound = new CompoundTag();
            compound.putString("name", bookChapter.name);
            int num = bookChapter.pages.size();
            compound.putInt("num", num);
            for(int k = 0; k < num; k++){

                BookPageEntry bookPageEntry = ((BookPageEntry) bookChapter.pages.toArray()[k]);
                CompoundTag compoundBoxes = new CompoundTag();
                compoundBoxes.putString("location" + k, bookPageEntry.location);
                compoundBoxes.putInt("pageNum" + k, bookPageEntry.pageNum);
                compound.put("page" + k, compoundBoxes);

            }
            tag.put("chapter" + i, compound);
        }
        return tag;
    }

    public static BookEntries loadFromTag(CompoundTag tag) {
        String book = tag.getString("bookId");
        int size = tag.getInt("numberOfChapters");
        int numOfPages = 0;
        ArrayList<BookChapter> list = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            CompoundTag paragraph = tag.getCompound("chapter" + i);
            String string = paragraph.getString("name");

            int num = paragraph.getInt("num");
            int numLast = numOfPages;
            numOfPages += num;
            ArrayList<BookPageEntry> pageEntryList = new ArrayList<>();
            for(int k = 0; k < num; k++){

                CompoundTag pageEntries = paragraph.getCompound("page" + k);
                String location = pageEntries.getString("location" + k);
                int pageNum = pageEntries.getInt("pageNum" + k);

                BookPageEntry bookChapter = new BookPageEntry(location, pageNum, i, k);
                pageEntryList.add(bookChapter);
            }

            BookChapter bookChapter = new BookChapter(pageEntryList, string, numLast + 1, numOfPages, i);
            list.add(bookChapter);
        }

        return new BookEntries(ResourceLocation.parse(book), list, numOfPages);
    }
}

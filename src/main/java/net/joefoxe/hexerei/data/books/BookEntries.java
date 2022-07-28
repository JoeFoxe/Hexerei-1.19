package net.joefoxe.hexerei.data.books;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public class BookEntries {
    public ArrayList<BookChapter> chapterList;
    public int numberOfPages;



    public BookEntries(ArrayList<BookChapter> chapterList, int numberOfPages) {
        this.chapterList = chapterList;
        this.numberOfPages = numberOfPages;
    }

    public static CompoundTag saveToTag(BookEntries bookEntries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("numberOfChapters", bookEntries.chapterList.size());
        for(int i = 0; i < bookEntries.chapterList.size(); i++) {
            BookChapter bookChapter = bookEntries.chapterList.get(i);
            CompoundTag compound = new CompoundTag();
            compound.putString("name" + i, bookChapter.name);
            int num = bookChapter.pages.size();
            compound.putInt("numberOfPages" + i, num);
            for(int k = 0; k < num; k++){

                BookPageEntry bookPageEntry = ((BookPageEntry) bookChapter.pages.toArray()[k]);
                CompoundTag compoundBoxes = new CompoundTag();
                compoundBoxes.putString("location" + k, bookChapter.name);
                compound.put("page" + k, compoundBoxes);

            }
            tag.put("chapter" + i, compound);
        }
        return tag;
    }

    public static BookEntries loadFromTag(CompoundTag tag) {
        int size = tag.getInt("numberOfChapters");
        int numOfPages = 0;
        ArrayList<BookChapter> list = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            CompoundTag paragraph = tag.getCompound("chapter" + i);
            String string = paragraph.getString("name" + i);

            int num = paragraph.getInt("numberOfPages" + i);
            numOfPages += num;
            ArrayList<BookPageEntry> pageEntryList = new ArrayList<>();
            for(int k = 0; k < num; k++){

                CompoundTag pageEntries = paragraph.getCompound("page" + k);
                String location = pageEntries.getString("location" + k);

                BookPageEntry bookChapter = new BookPageEntry(location, 0);
                pageEntryList.add(bookChapter);
            }

            BookChapter bookChapter = new BookChapter(pageEntryList, string, numOfPages, numOfPages);
            list.add(bookChapter);
        }

        return new BookEntries(list, numOfPages);
    }
}

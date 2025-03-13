package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookOfShadowsDyeRecipe extends CustomRecipe {
    protected BookOfShadowsDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {

        Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
        Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack slotStack = inv.getItem(slot);
            int column = slot % inv.width();
            int row = slot / inv.width();
            if (slotStack.isEmpty()) {
                continue;
            }
            if (slotStack.getItem() instanceof HexereiBookItem) {
                if (posBook != null) {
                    return false;
                }
                posBook = new Tuple<>(new Tuple<>(column, row), slotStack);
            } else if (slotStack.is(Tags.Items.DYES)) {
                DyeColor dyeColor = DyeColor.getColor(slotStack);
                if (dyeColor == null) {
                    return false;
                }
                posDyes.computeIfAbsent(new Tuple<>(column, row), c -> new ArrayList<>()).add(dyeColor);
            } else {
                return false;
            }
        }

        if (posBook == null || posDyes.isEmpty())
            return false;

        boolean mainDye = false;
        boolean trimDye = false;

        for (Map.Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
            int dyeCol = entry.getKey().getA();
            int dyeRow = entry.getKey().getB();
            int bookCol = posBook.getA().getA();
            int bookRow = posBook.getA().getB();
            if (dyeCol == bookCol + 1 && dyeRow == bookRow) {
                if (!mainDye)
                    mainDye = true;
                else
                    return false;
            }
            else if (dyeCol == bookCol - 1 && dyeRow == bookRow) {
                if (!mainDye)
                    mainDye = true;
                else
                    return false;
            }
            else if (dyeRow == bookRow + 1 && dyeCol == bookCol) {
                if (!trimDye)
                    trimDye = true;
                else
                    return false;
            }
            else if (dyeRow == bookRow - 1 && dyeCol == bookCol) {
                if (!trimDye)
                    trimDye = true;
                else
                    return false;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
        Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack slotStack = inv.getItem(slot);
            if (slotStack.isEmpty()) {
                continue;
            }
            int column = slot % inv.width();
            int row = slot / inv.width();
            if (slotStack.getItem() instanceof HexereiBookItem) {
                if (posBook != null) {
                    return ItemStack.EMPTY;
                }

                posBook = new Tuple<>(new Tuple<>(column, row), slotStack);
            } else if (slotStack.is(Tags.Items.DYES)) {
                DyeColor dyeColor = DyeColor.getColor(slotStack);
                if (dyeColor == null) {
                    return ItemStack.EMPTY;
                }
                posDyes.computeIfAbsent(new Tuple<>(column, row), c -> new ArrayList<>()).add(dyeColor);
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (posBook == null) {
            return ItemStack.EMPTY;
        }

        ItemStack book = posBook.getB().copy();
        book.setCount(1);

        applyColors(posDyes, book, posBook.getA());

        return book;
    }

    private void applyColors(Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes, ItemStack book, Tuple<Integer, Integer> posBook) {
        List<DyeColor> mainDyes = new ArrayList<>();
        List<DyeColor> trimDyes = new ArrayList<>();

        for (Map.Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
            if (entry.getKey().getA() == posBook.getA() + 1 && entry.getKey().getB().equals(posBook.getB())) {
                mainDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getA() == posBook.getA() - 1 && entry.getKey().getB().equals(posBook.getB())) {
                mainDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getB() == posBook.getB() + 1 && entry.getKey().getA().equals(posBook.getA())) {
                trimDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getB() == posBook.getB() - 1 && entry.getKey().getA().equals(posBook.getA())) {
                trimDyes.addAll(entry.getValue());
            }
        }
        BookColorData bookColorData = book.get(ModDataComponents.BOOK_COLORS);
        if (bookColorData != null){
            int dye1 = bookColorData.color1();
            int dye2 = bookColorData.color2();
            if (!mainDyes.isEmpty())
                dye1 = mainDyes.get(0).getTextureDiffuseColor();
            if (!trimDyes.isEmpty())
                dye2 = trimDyes.get(0).getTextureDiffuseColor();
            bookColorData = new BookColorData(dye1, dye2);
        }
        book.set(ModDataComponents.BOOK_COLORS, bookColorData);
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    public ItemStack getOutput() {
        return getResultItem(null);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOOK_OF_SHADOWS_DYE_SERIALIZER.get();
    }
}
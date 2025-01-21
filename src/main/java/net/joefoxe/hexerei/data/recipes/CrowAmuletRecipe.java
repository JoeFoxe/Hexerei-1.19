package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.custom.CrowAmuletItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class CrowAmuletRecipe extends CustomRecipe {
//    public static final SimpleCraftingRecipeSerializer<CrowAmuletRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(new CrowAmuletRecipe());

    public CrowAmuletRecipe(CraftingBookCategory cBc) {
        super(cBc);


    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level world) {
        int amulet = 0;
        int other = 0;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof CrowAmuletItem) {
                    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

                    if(!tag.contains("Items")){
                        ++amulet;
                    }
                } else {
                    ++other;
                }

                if (other > 1 || amulet > 1) {
                    return false;
                }
            }
        }

        return amulet == 1 && other == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
        ItemStack amulet = ItemStack.EMPTY;
        ItemStack other = ItemStack.EMPTY;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof CrowAmuletItem) {
                    amulet = stack.copy();
                    amulet.setCount(1);
                } else {
                    other = stack.copy();
                    other.setCount(1);
                }
            }
        }
        if (amulet.getItem() instanceof CrowAmuletItem && !other.isEmpty()) {
            CompoundTag tag = amulet.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            ListTag listtag = new ListTag();

            if (!other.isEmpty()) {
                listtag.add(other.save(registryAccess));

            }

            tag.put("Items", listtag);

            amulet.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        return amulet;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 1;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CROW_AMULET_APPLY_SERIALIZER.get();
    }
}
package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CrowAmuletItem;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;


public class CrowAmuletRecipe extends CustomRecipe {
    public static final SimpleRecipeSerializer<CrowAmuletRecipe> SERIALIZER = new SimpleRecipeSerializer<>(CrowAmuletRecipe::new);

    public CrowAmuletRecipe(ResourceLocation registryName) {
        super(registryName);


    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        int amulet = 0;
        int other = 0;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof CrowAmuletItem) {
                    CompoundTag tag = stack.getOrCreateTag();

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
    public ItemStack assemble(CraftingContainer inventory) {
        ItemStack amulet = ItemStack.EMPTY;
        ItemStack other = ItemStack.EMPTY;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
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
            CompoundTag tag = new CompoundTag();
            if(amulet.hasTag())
                tag = amulet.getTag();

            ListTag listtag = new ListTag();

            if (!other.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)0);
                other.save(compoundtag);
                listtag.add(compoundtag);

            }

            tag.put("Items", listtag);

            amulet.setTag(tag);
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
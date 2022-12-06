package net.joefoxe.hexerei.container;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class WoodcutterContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    /** The index of the selected recipe in the GUI. */
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<WoodcutterRecipe> recipes = Lists.newArrayList();
    private WoodcutterRecipe lastUsedRecipe = null;
    private int recipesSize = 0;
    /** The {@plainlink ItemStack} set in the input slot by the player. */
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack lastNonAirInput = ItemStack.EMPTY;
    private ItemStack lastInput = ItemStack.EMPTY;
    /**
     * Stores the game time of the last time the player took items from the the crafting result slot. This is used to
     * prevent the sound from being played multiple times on the same tick.
     */
    long lastSoundTime;
    final Slot inputSlot;
    /** The inventory slot that stores the output of the crafting recipe. */
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {
    };
    public final Container container = new SimpleContainer(1) {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void setChanged() {
            super.setChanged();
            WoodcutterContainer.this.slotsChanged(this);
            WoodcutterContainer.this.slotUpdateListener.run();
        }
    };
    /** The inventory that stores the output of the crafting recipe. */
    public final ResultContainer resultContainer = new ResultContainer();

    public WoodcutterContainer(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public WoodcutterContainer(int pContainerId, Inventory pPlayerInventory, final ContainerLevelAccess pAccess) {
        super(ModContainers.WOODCUTTER_CONTAINER.get(), pContainerId);
        this.access = pAccess;
        this.level = pPlayerInventory.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 26, 44));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 142, 56) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack p_40362_) {
                return false;
            }

            @Override
            public boolean mayPickup(Player pPlayer) {
                if(WoodcutterContainer.this.selectedRecipeIndex.get() == -1 || recipes.isEmpty() || WoodcutterContainer.this.container.getItem(0).getCount() < recipes.get(WoodcutterContainer.this.selectedRecipeIndex.get()).ingredientCount)
                    return false;

                return super.mayPickup(pPlayer);
            }

            public void onTake(Player p_150672_, ItemStack p_150673_) {
                p_150673_.onCraftedBy(p_150672_.level, p_150672_, p_150673_.getCount());
                WoodcutterContainer.this.resultContainer.awardUsedRecipes(p_150672_);
                ItemStack itemstack = WoodcutterContainer.this.inputSlot.remove(recipes.get(WoodcutterContainer.this.selectedRecipeIndex.get()).ingredientCount);
                if (!itemstack.isEmpty()) {
                    WoodcutterContainer.this.setupResultSlot();
                }

                pAccess.execute((p_40364_, p_40365_) -> {
                    long l = p_40364_.getGameTime();
                    if (WoodcutterContainer.this.lastSoundTime != l) {
                        p_40364_.playSound((Player)null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        WoodcutterContainer.this.lastSoundTime = l;
                    }

                });
                WoodcutterContainer.this.slotsChanged(WoodcutterContainer.this.container);
                super.onTake(p_150672_, p_150673_);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18 + 4, 84 + i * 18 + 31));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18 + 4, 142 + 31));
        }

        this.addDataSlot(this.selectedRecipeIndex);
        this.selectedRecipeIndex.set(-1);
    }

    /**
     * Returns the index of the selected recipe.
     */
    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<WoodcutterRecipe> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, ModBlocks.WILLOW_WOODCUTTER.get()) || stillValid(this.access, pPlayer, ModBlocks.MAHOGANY_WOODCUTTER.get());
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (this.isValidRecipeIndex(pId)) {
            this.selectedRecipeIndex.set(pId);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int pRecipeIndex) {
        return pRecipeIndex >= 0 && pRecipeIndex < this.recipes.size();
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void slotsChanged(Container pInventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        this.setupRecipeList(pInventory, itemstack);
        this.input = itemstack.copy();

    }

    private void setupRecipeList(Container pContainer, ItemStack pStack) {
        this.recipes = new ArrayList<>();
        if(!this.input.sameItem(pStack)) {
            if(!this.input.is(Items.AIR) && !this.input.is(lastInput.getItem()) && !this.lastInput.is(Items.AIR))
                this.selectedRecipeIndex.set(-1);
            if(!this.input.is(lastInput.getItem()))
                this.lastInput = this.input;
        }
        if (!pStack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(WoodcutterRecipe.Type.INSTANCE, pContainer, this.level);
            this.recipes = this.recipes.stream().filter((craftingRecipe) -> {
                return container.getItem(0).getCount() >= craftingRecipe.ingredientCount;
            }).toList();
        }
        if(this.recipesSize != this.recipes.size() && this.selectedRecipeIndex.get() != -1)
        {
            for(int i = 0; i < this.recipes.size(); i++){
                if(this.recipes.get(i) == this.lastUsedRecipe) {
                    this.selectedRecipeIndex.set(i);
                    break;
                }
            }
        }
        this.recipesSize = this.recipes.size();

        if(!this.input.sameItem(pStack) || pStack.getCount() != this.input.getCount()) {
            this.resultSlot.set(ItemStack.EMPTY);
            if(this.lastInput.is(Items.AIR))
                setupResultSlot();
        }

    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            WoodcutterRecipe woodcutterrecipe = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(woodcutterrecipe);
            this.lastUsedRecipe = woodcutterrecipe;
            this.resultSlot.set(woodcutterrecipe.assemble(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.lastNonAirInput = this.input;

        this.broadcastChanges();
    }

    public MenuType<?> getType() {
        return ModContainers.WOODCUTTER_CONTAINER.get();
    }

    public void registerUpdateListener(Runnable pListener) {
        this.slotUpdateListener = pListener;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
     * null for the initial slot that was double-clicked.
     */
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 1) {
                item.onCraftedBy(itemstack1, pPlayer.level, pPlayer);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(WoodcutterRecipe.Type.INSTANCE, new SimpleContainer(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 2 && pIndex < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 29 && pIndex < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.selectedRecipeIndex.set(-1);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_40313_, p_40314_) -> {
            this.clearContainer(pPlayer, this.container);
        });
    }
}

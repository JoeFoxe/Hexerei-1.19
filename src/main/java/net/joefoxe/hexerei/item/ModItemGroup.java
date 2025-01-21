package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModItemGroup {

	public static final DeferredRegister<CreativeModeTab> ITEM_GROUP = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Hexerei.MOD_ID);

	private static final List<DeferredHolder<Item, Item>> BLACKLIST = List.of(ModItems.BROOM_KEYCHAIN_BASE, ModItems.CROW_BLANK_AMULET_TRINKET, ModItems.CROW_BLANK_AMULET_TRINKET_FRAME);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEXEREI_GROUP = ITEM_GROUP.register("hexerei_tab", () -> CreativeModeTab.builder()
			.icon(() -> ModItems.MIXING_CAULDRON.get().getDefaultInstance())
			.title(Component.translatable("itemGroup.hexereiModTab"))
			.displayItems(new CreativeModeTab.DisplayItemsGenerator() {
				@Override
				public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
					ModItems.ITEMS.getEntries().forEach(entry -> {
						ItemStack stack = entry.get().getDefaultInstance();

						if (!isBlacklist(entry.get()))
							output.accept(stack);

						if (stack.is(ModItems.INFUSED_FABRIC_BLOCK.get())) {
							for (DyeColor color : DyeColor.values()) {
								if (color.getName().equals("white"))
									continue;
								ItemStack newStack = ModItems.INFUSED_FABRIC_BLOCK.get().getDefaultInstance();
								CompoundTag tag = newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
								tag.putString("color", color.getName());
								newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
								output.accept(newStack);
							}
						}
						else if (stack.is(ModItems.INFUSED_FABRIC_CARPET.get())) {
							for (DyeColor color : DyeColor.values()) {
								if (color.getName().equals("white"))
									continue;
								ItemStack newStack = ModItems.INFUSED_FABRIC_CARPET.get().getDefaultInstance();
								CompoundTag tag = newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
								tag.putString("color", color.getName());
								newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
								output.accept(newStack);
							}
						}
					});
				}
			})
			.build());

	private static boolean isBlacklist(Item item){

		AtomicBoolean blacklisted = new AtomicBoolean(false);
		BLACKLIST.forEach((registryObject) -> {
			if (item == registryObject.get())
				blacklisted.set(true);

		});
		return blacklisted.get();
	}
}

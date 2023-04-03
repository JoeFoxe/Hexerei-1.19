package net.joefoxe.hexerei.data.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }


    public static String getItemName(ItemLike pItemLike) {
        return Registry.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    public static String getAddCandleRecipeName(ItemLike pResult) {
        return "add_to_candle/" + getItemName(pResult) + "_add_to_candle";
    }

    public static String getWoodcuttingRecipeName(String type, Item result, Item input) {
        return "woodcutting" + "/" + type + "/" + ForgeRegistries.ITEMS.getKey(result).getPath() + "_from_" + ForgeRegistries.ITEMS.getKey(input).getPath() + "_woodcutting";
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {

        File add_to_candle_file = new File("./recipe-builder/add_to_candle.json");
        File woodcutting_file = new File("./recipe-builder/woodcutting.json");

        JsonArray recipesAddToCandle;
        JsonArray recipesWoodcutting;

        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(add_to_candle_file.getAbsolutePath()));
            recipesAddToCandle = GsonHelper.getAsJsonArray(jsonElement.getAsJsonObject(), "values");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(woodcutting_file.getAbsolutePath()));
            recipesWoodcutting = GsonHelper.getAsJsonArray(jsonElement.getAsJsonObject(), "values");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        JsonArray finalRecipesToAdd = recipesAddToCandle;
        ForgeRegistries.BLOCKS.forEach((block) -> {
            finalRecipesToAdd.forEach((recipeBlock) -> {
                if (ForgeRegistries.BLOCKS.getKey(block).toString().equals(recipeBlock.getAsString())) {
                    ItemStack stack = new ItemStack(ModItems.CANDLE.get());
                    CandleItem.setBaseLayerFromBlock(stack, Registry.BLOCK.getKey(block).toString());
                    new AddToCandleRecipeBuilder(block.asItem(), stack.getItem(), 1, stack.getOrCreateTag())
                            .unlockedBy("has_candle", inventoryTrigger(ItemPredicate.Builder.item()
                                    .of(ModItems.CANDLE.get()).build())).save(pFinishedRecipeConsumer, getAddCandleRecipeName(block));
                }
            });
        });

        JsonArray finalrecipesWoodcutting = recipesWoodcutting;
        ForgeRegistries.BLOCKS.forEach((block) -> {
            finalrecipesWoodcutting.forEach((recipeBlock) -> {

                Item reg = Item.byBlock(block);
                //planks as list for all types of planks so I can data gen my stuff too
                boolean isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "planks");
                List<String> planks_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray plankArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "planks", new JsonArray());
                    plankArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String planks_loc = GsonHelper.getAsString(object, "planks", "");
                        if (!planks_loc.equals("")) {
                            planks_loc_list.add(planks_loc);
                        }
                    });
                } else
                    planks_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "planks", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "slab");
                List<String> slab_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray slabArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "slab", new JsonArray());
                    slabArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String slab_loc = GsonHelper.getAsString(object, "slab", "");
                        if (!slab_loc.equals("")) {
                            slab_loc_list.add(slab_loc);
                        }
                    });
                } else
                    slab_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "slab", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "stairs");
                List<String> stairs_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray stairsArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "stairs", new JsonArray());
                    stairsArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String stairs_loc = GsonHelper.getAsString(object, "stairs", "");
                        if (!stairs_loc.equals("")) {
                            stairs_loc_list.add(stairs_loc);
                        }
                    });
                } else
                    stairs_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "stairs", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "button");
                List<String> button_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray buttonArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "button", new JsonArray());
                    buttonArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String button_loc = GsonHelper.getAsString(object, "button", "");
                        if (!button_loc.equals("")) {
                            button_loc_list.add(button_loc);
                        }
                    });
                } else
                    button_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "button", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "pressure_plate");
                List<String> pressure_plate_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray pressure_plateArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "pressure_plate", new JsonArray());
                    pressure_plateArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String pressure_plate_loc = GsonHelper.getAsString(object, "pressure_plate", "");
                        if (!pressure_plate_loc.equals("")) {
                            pressure_plate_loc_list.add(pressure_plate_loc);
                        }
                    });
                } else
                    pressure_plate_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "pressure_plate", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "fence");
                List<String> fence_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray fenceArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "fence", new JsonArray());
                    fenceArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String fence_loc = GsonHelper.getAsString(object, "fence", "");
                        if (!fence_loc.equals("")) {
                            fence_loc_list.add(fence_loc);
                        }
                    });
                } else
                    fence_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "fence", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "fence_gate");
                List<String> fence_gate_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray fence_gateArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "fence_gate", new JsonArray());
                    fence_gateArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String fence_gate_loc = GsonHelper.getAsString(object, "fence_gate", "");
                        if (!fence_gate_loc.equals("")) {
                            fence_gate_loc_list.add(fence_gate_loc);
                        }
                    });
                } else
                    fence_gate_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "fence_gate", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "door");
                List<String> door_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray doorArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "door", new JsonArray());
                    doorArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String door_loc = GsonHelper.getAsString(object, "door", "");
                        if (!door_loc.equals("")) {
                            door_loc_list.add(door_loc);
                        }
                    });
                } else
                    door_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "door", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "trapdoor");
                List<String> trapdoor_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray trapdoorArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "trapdoor", new JsonArray());
                    trapdoorArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String trapdoor_loc = GsonHelper.getAsString(object, "trapdoor", "");
                        if (!trapdoor_loc.equals("")) {
                            trapdoor_loc_list.add(trapdoor_loc);
                        }
                    });
                } else
                    trapdoor_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "trapdoor", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "boat");
                List<String> boat_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray boatArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "boat", new JsonArray());
                    boatArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String boat_loc = GsonHelper.getAsString(object, "boat", "");
                        if (!boat_loc.equals("")) {
                            boat_loc_list.add(boat_loc);
                        }
                    });
                } else
                    boat_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "boat", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "chest_boat");
                List<String> chest_boat_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray chest_boatArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "chest_boat", new JsonArray());
                    chest_boatArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String chest_boat_loc = GsonHelper.getAsString(object, "chest_boat", "");
                        if (!chest_boat_loc.equals("")) {
                            chest_boat_loc_list.add(chest_boat_loc);
                        }
                    });
                } else
                    chest_boat_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "chest_boat", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "chest");
                List<String> chest_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray chestArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "chest", new JsonArray());
                    chestArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String chest_loc = GsonHelper.getAsString(object, "chest", "");
                        if (!chest_loc.equals("")) {
                            chest_loc_list.add(chest_loc);
                        }
                    });
                } else
                    chest_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "chest", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "crafting_table");
                List<String> crafting_table_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray crafting_tableArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "crafting_table", new JsonArray());
                    crafting_tableArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String crafting_table_loc = GsonHelper.getAsString(object, "crafting_table", "");
                        if (!crafting_table_loc.equals("")) {
                            crafting_table_loc_list.add(crafting_table_loc);
                        }
                    });
                } else
                    crafting_table_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "crafting_table", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "barrel");
                List<String> barrel_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray barrelArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "barrel", new JsonArray());
                    barrelArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String barrel_loc = GsonHelper.getAsString(object, "barrel", "");
                        if (!barrel_loc.equals("")) {
                            barrel_loc_list.add(barrel_loc);
                        }
                    });
                } else
                    barrel_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "barrel", ""));

                isArray = GsonHelper.isArrayNode(recipeBlock.getAsJsonObject(), "sign");
                List<String> sign_loc_list = new ArrayList<>();
                if (isArray) {
                    JsonArray signArray = GsonHelper.getAsJsonArray(recipeBlock.getAsJsonObject(), "sign", new JsonArray());
                    signArray.forEach((plankElement) -> {
                        JsonObject object = plankElement.getAsJsonObject();
                        String sign_loc = GsonHelper.getAsString(object, "sign", "");
                        if (!sign_loc.equals("")) {
                            sign_loc_list.add(sign_loc);
                        }
                    });
                } else
                    sign_loc_list.add(GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "sign", ""));


                String stripped_wood_loc = GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "stripped_wood", "");
                String wood_loc = GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "wood", "");
                String stripped_log_loc = GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "stripped_log", "");
                String log_loc = GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "log", "");

                String type = GsonHelper.getAsString(recipeBlock.getAsJsonObject(), "type");

                List<Item> planks_list = new ArrayList<>();
                for (String s : planks_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    planks_list.add(item);
                }
                List<Item> slab_list = new ArrayList<>();
                for (String s : slab_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    slab_list.add(item);
                }
                List<Item> stairs_list = new ArrayList<>();
                for (String s : stairs_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    stairs_list.add(item);
                }
                List<Item> button_list = new ArrayList<>();
                for (String s : button_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    button_list.add(item);
                }
                List<Item> pressure_plate_list = new ArrayList<>();
                for (String s : pressure_plate_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    pressure_plate_list.add(item);
                }
                List<Item> fence_list = new ArrayList<>();
                for (String s : fence_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    fence_list.add(item);
                }
                List<Item> fence_gate_list = new ArrayList<>();
                for (String s : fence_gate_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    fence_gate_list.add(item);
                }
                List<Item> door_list = new ArrayList<>();
                for (String s : door_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    door_list.add(item);
                }
                List<Item> trapdoor_list = new ArrayList<>();
                for (String s : trapdoor_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    trapdoor_list.add(item);
                }
                List<Item> boat_list = new ArrayList<>();
                for (String s : boat_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    boat_list.add(item);
                }
                List<Item> chest_boat_list = new ArrayList<>();
                for (String s : chest_boat_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    chest_boat_list.add(item);
                }
                List<Item> chest_list = new ArrayList<>();
                for (String s : chest_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    chest_list.add(item);
                }
                List<Item> crafting_table_list = new ArrayList<>();
                for (String s : crafting_table_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    crafting_table_list.add(item);
                }
                List<Item> barrel_list = new ArrayList<>();
                for (String s : barrel_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    barrel_list.add(item);
                }
                List<Item> sign_list = new ArrayList<>();
                for (String s : sign_loc_list) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                    sign_list.add(item);
                }
//                Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(planks_loc));
                Item stripped_wood = ForgeRegistries.ITEMS.getValue(new ResourceLocation(stripped_wood_loc));
                Item wood = ForgeRegistries.ITEMS.getValue(new ResourceLocation(wood_loc));
                Item stripped_log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(stripped_log_loc));
                Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(log_loc));


                if (reg.equals(stripped_wood) || reg.equals(wood) || reg.equals(log) || reg.equals(stripped_log)) {
                    for (Item planks : planks_list) {
                        if (planks != null && planks != Items.AIR)
                            new WoodcutterRecipeBuilder(block.asItem(), planks, 5, 1, type)
                                    .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, planks, reg));
                    }
                }

                for (Item planks : planks_list) {
                    if (reg.equals(planks)) {

                        for (Item planks2 : planks_list)
                            if (planks2 != null && planks2 != Items.AIR && planks != planks2)
                                new WoodcutterRecipeBuilder(block.asItem(), planks2, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, planks2, reg));

//                        if (slab != null && slab != Items.AIR)
//                            new WoodcutterRecipeBuilder(block.asItem(), slab, 2, 1, type)
//                                    .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, slab, reg));

                        for (Item slab : slab_list) {
                            if (slab != null && slab != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), slab, 2, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, slab, reg));
                        }

                        for (Item stairs : stairs_list)
                            if (stairs != null && stairs != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), stairs, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, stairs, reg));

                        for (Item pressure_plate : pressure_plate_list)
                            if (pressure_plate != null && pressure_plate != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), pressure_plate, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, pressure_plate, reg));

                        for (Item button : button_list)
                            if (button != null && button != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), button, 2, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, button, reg));

                        for (Item fence : fence_list)
                            if (fence != null && fence != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), fence, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, fence, reg));

                        for (Item fence_gate : fence_gate_list)
                            if (fence_gate != null && fence_gate != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), fence_gate, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, fence_gate, reg));

                        for (Item door : door_list)
                            if (door != null && door != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), door, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, door, reg));

                        for (Item trapdoor : trapdoor_list)
                            if (trapdoor != null && trapdoor != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), trapdoor, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, trapdoor, reg));

                        for (Item boat : boat_list)
                            if (boat != null && boat != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), boat, 1, 4, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, boat, reg));

                        for (Item chest_boat : chest_boat_list)
                            if (chest_boat != null && chest_boat != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), chest_boat, 1, 9, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, chest_boat, reg));

                        for (Item chest : chest_list)
                            if (chest != null && chest != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), chest, 1, 6, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, chest, reg));

                        for (Item crafting_table : crafting_table_list)
                            if (crafting_table != null && crafting_table != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), crafting_table, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, crafting_table, reg));

                        for (Item barrel : barrel_list)
                            if (barrel != null && barrel != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), barrel, 1, 3, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, barrel, reg));

                        for (Item sign : sign_list)
                            if (sign != null && sign != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), sign, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, sign, reg));
                    }
                }

                for (Item slab : slab_list)
                    if (reg.equals(slab)) {

                        for (Item planks : planks_list) {
                            if (planks != null && planks != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), planks, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, planks, reg));
                        }

                        for (Item slab2 : slab_list)
                            if (slab2 != null && slab2 != Items.AIR && slab != slab2)
                                new WoodcutterRecipeBuilder(block.asItem(), slab2, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, slab2, reg));
                        for (Item stairs : stairs_list)
                            if (stairs != null && stairs != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), stairs, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, stairs, reg));
                        for (Item pressure_plate : pressure_plate_list)
                            if (pressure_plate != null && pressure_plate != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), pressure_plate, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, pressure_plate, reg));
                        for (Item button : button_list)
                            if (button != null && button != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), button, 1, 1, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, button, reg));
                        for (Item fence : fence_list)
                            if (fence != null && fence != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), fence, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, fence, reg));
                        for (Item fence_gate : fence_gate_list)
                            if (fence_gate != null && fence_gate != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), fence_gate, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, fence_gate, reg));
                        for (Item door : door_list)
                            if (door != null && door != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), door, 1, 4, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, door, reg));
                        for (Item trapdoor : trapdoor_list)
                            if (trapdoor != null && trapdoor != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), trapdoor, 1, 4, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, trapdoor, reg));
                        for (Item boat : boat_list)
                            if (boat != null && boat != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), boat, 1, 8, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, boat, reg));
                        for (Item chest_boat : chest_boat_list)
                            if (chest_boat != null && chest_boat != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), chest_boat, 1, 18, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, chest_boat, reg));
                        for (Item chest : chest_list)
                            if (chest != null && chest != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), chest, 1, 10, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, chest, reg));
                        for (Item crafting_table : crafting_table_list)
                            if (crafting_table != null && crafting_table != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), crafting_table, 1, 4, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, crafting_table, reg));
                        for (Item barrel : barrel_list)
                            if (barrel != null && barrel != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), barrel, 1, 6, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, barrel, reg));
                        for (Item sign : sign_list)
                            if (sign != null && sign != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), sign, 1, 2, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, sign, reg));

                    }
                for (Item stairs : stairs_list)
                    if (reg.equals(stairs)) {

                        for (Item planks : planks_list) {
                            if (planks != null && planks != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), planks, 3, 4, type)
                                        .save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, planks, reg));
                        }


                        for (Item slab : slab_list)
                            if (slab != null && slab != Items.AIR)
                                new WoodcutterRecipeBuilder(block.asItem(), slab, 3, 2, type)
                                        .unlockedBy("has_block", inventoryTrigger(ItemPredicate.Builder.item()
                                                .of(reg).build())).save(pFinishedRecipeConsumer, getWoodcuttingRecipeName(type, slab, reg) + "_combine");

                    }
            });
        });

//
//        List<Item> allowedSupportedContainers = new ArrayList<>();
//        List<ItemStack> supportedContainerStacks = new ArrayList<>();
//        for (Item container : FluidMixingRecipeBuilder.POTION_CONTAINERS) {
//            ItemStack stack = new ItemStack(container);
//            supportedContainerStacks.add(stack);
////            if (PotionBrewing.ALLOWED_CONTAINER.test(stack)) {
////                allowedSupportedContainers.add(container);
////            }
//        }
//
//
//        for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
//            if (recipe instanceof BrewingRecipe recipeImpl) {
//                ItemStack output = recipeImpl.getOutput();
//                if (!FluidMixingRecipeBuilder.POTION_CONTAINERS.contains(output.getItem())) {
//                    continue;
//                }
//
//                Ingredient input = recipeImpl.getInput();
//                Ingredient ingredient = recipeImpl.getIngredient();
//                FluidStack outputFluid = null;
//                for (ItemStack stack : supportedContainerStacks) {
//                    if (input.test(stack)) {
//                        FluidStack inputFluid = PotionFluidHandler.getFluidFromPotionItem(stack);
//                        if (outputFluid == null) {
//                            outputFluid = PotionFluidHandler.getFluidFromPotionItem(output);
//                        }
////                        mixingRecipes.add(createRecipe("potion_mixing_modded_" + recipeIndex++, ingredient, inputFluid, outputFluid));
//
//
//                        new FluidMixingRecipeBuilder(new ArrayList<>(List.of(ingredient)), inputFluid, outputFluid)
//                                .unlockedBy("has_blaze_powder", inventoryTrigger(ItemPredicate.Builder.item()
//                                        .of(Items.BLAZE_POWDER).build())).save(pFinishedRecipeConsumer, new ResourceLocation(Hexerei.MOD_ID,
//                                        Registry.POTION.getKey(PotionFluidHandler.getPotionFromFluidStack(outputFluid)).getPath() + "_from_fluid_mixing"));
//
//                    }
//                }
//            }
//        }

//        ForgeRegistries.POTIONS.forEach((potion) -> {
//
//            if(PotionBrewing.isBrewablePotion(potion)) {
//                //                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Registry.BLOCK.getKey(potion));
//
//                ArrayList<Ingredient> ingredients = new ArrayList<>();
//                for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
//                    if (recipe instanceof BrewingRecipe brewingRecipe) {
//                        for (ItemStack s : brewingRecipe.getIngredient().getItems()) {
//                            ingredients.add(Ingredient.of(s));
//                        }
//
//                    }
//                }
////
////            BrewingRecipeRegistry.getRecipes().forEach(iBrewingRecipe->{
////                iBrewingRecipe.
////            });
//                //                ResourceLocation location = sprite.getName();
//                ItemStack stack = new ItemStack(ModItems.CANDLE.get());
//                new FluidMixingRecipeBuilder(ingredients, PotionFluidHandler.getFluidFromPotion(potion, PotionFluid.BottleType.REGULAR, 2000), PotionFluidHandler.getFluidFromPotion(potion, PotionFluid.BottleType.REGULAR, 2000))
//                        .unlockedBy("has_blaze_powder", inventoryTrigger(ItemPredicate.Builder.item()
//                                .of(Items.BLAZE_POWDER).build())).save(pFinishedRecipeConsumer, new ResourceLocation(Hexerei.MOD_ID,
//                                Registry.POTION.getKey(potion).getPath() + "_from_fluid_mixing"));
//            }
//        });

// from kaupenjoes github
//        ShapedRecipeBuilder.shaped(ModBlocks.EBONY_DOOR.get())
//                .define('E', ModBlocks.EBONY_PLANKS.get())
//                .pattern("EE")
//                .pattern("EE")
//                .pattern("EE")
//                .unlockedBy("has_ebony_planks", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModBlocks.EBONY_PLANKS.get()).build()))
//                .save(pFinishedRecipeConsumer);
//
//        ShapelessRecipeBuilder.shapeless(ModItems.CITRINE.get())
//                .requires(ModBlocks.CITRINE_BLOCK.get())
//                .unlockedBy("has_citrine_block", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModBlocks.CITRINE_BLOCK.get()).build()))
//                .save(pFinishedRecipeConsumer);
//
//        ShapedRecipeBuilder.shaped(ModBlocks.CITRINE_BLOCK.get())
//                .define('C', ModItems.CITRINE.get())
//                .pattern("CCC")
//                .pattern("CCC")
//                .pattern("CCC")
//                .unlockedBy("has_citrine", inventoryTrigger(ItemPredicate.Builder.item()
//                        .of(ModItems.CITRINE.get()).build()))
//                .save(pFinishedRecipeConsumer);

    }
}

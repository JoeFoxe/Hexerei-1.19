package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CandleItem extends BlockItem implements DyeableLeatherItem {

    public CandleItem(Block block, Properties properties) {
        super(block, properties);
        DispenserBlock.registerBehavior(this, Candle.DISPENSE_ITEM_BEHAVIOR);

//        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
//            /**
//             * Dispense the specified stack, play the dispense sound and spawn particles.
//             */
//            protected ItemStack execute(BlockSource p_123412_, ItemStack p_123413_) {
//                Level level = p_123412_.getLevel();
//                this.setSuccess(true);
//                Direction direction = p_123412_.getBlockState().getValue(DispenserBlock.FACING);
//                BlockPos blockpos = p_123412_.getPos().relative(direction);
//                BlockState blockstate = level.getBlockState(blockpos);
//                if (Candle.canBeLit(blockstate, blockpos, level)) {
//
//                    CandleTile tile = ((CandleTile) level.getBlockEntity(blockpos));
//                    boolean flag = false;
//
//                    if(blockstate.getBlock() instanceof Candle && tile != null){
//                        if (tile.candles.get(0).hasCandle && !tile.candles.get(0).lit)
//                            tile.candles.get(0).lit = true;
//                        else if (tile.candles.get(1).hasCandle && !tile.candles.get(1).lit)
//                            tile.candles.get(1).lit = true;
//                        else if (tile.candles.get(2).hasCandle && !tile.candles.get(2).lit)
//                            tile.candles.get(2).lit = true;
//                        else if (tile.candles.get(3).hasCandle && !tile.candles.get(3).lit)
//                            tile.candles.get(3).lit = true;
//                        else {
//                            flag = true;
//                        }
//                    }
//
//                    if(!flag){
//                        level.playSound((Player) null, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RandomSource.create().nextFloat() * 0.4F + 1.0F);
////                    p_123413_.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(pContext.getHand()));
//
//                        if(blockstate.hasProperty(BlockStateProperties.LIT))
//                            level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, true));
//                        level.gameEvent((Entity) null, GameEvent.BLOCK_CHANGE, blockpos);
//                    }
//                }
//
//                if (this.isSuccess() && p_123413_.hurt(1, level.random, (ServerPlayer)null)) {
//                    p_123413_.setCount(0);
//                }
//
//                return p_123413_;
//            }
//        });


    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pStack.getItem() instanceof BlockItem blockItem && blockItem != ModItems.CANDLE.get()){
            if(blockItem.getBlock() == ModBlocks.CANDLE_BLUE.get()
                || blockItem.getBlock() == (ModBlocks.CANDLE_BLACK.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_LIME.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_ORANGE.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_PINK.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_PURPLE.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_RED.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_CYAN.get())
                    || blockItem.getBlock() == (ModBlocks.CANDLE_YELLOW.get())){
                CompoundTag tag = new CompoundTag();
                if(pStack.hasTag()){
                    tag = pStack.getTag();
                }

                ItemStack stack = new ItemStack(ModItems.CANDLE.get());

                if(tag != null && !tag.isEmpty())
                    stack.setTag(tag);

                if(blockItem.getBlock() == (ModBlocks.CANDLE_BLUE.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.BLUE));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_BLACK.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.BLACK));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_LIME.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.LIME));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_ORANGE.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.ORANGE));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_PINK.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.PINK));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_PURPLE.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.PURPLE));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_RED.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.RED));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_CYAN.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.CYAN));
                if(blockItem.getBlock() == (ModBlocks.CANDLE_YELLOW.get()))
                    CandleItem.setColorStatic(stack, HexereiUtil.getColorValue(DyeColor.YELLOW));

                pEntity.getSlot(pSlotId).set(stack);

            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        CustomItemRenderer renderer = createItemRenderer();
        if (renderer != null) {
            consumer.accept(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer.getRenderer();
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public CustomItemRenderer createItemRenderer() {
        return new CandleItemRenderer();
    }


    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerCandleColors(RegisterColorHandlersEvent.Item event)
        {
            CandleItem.ItemHandlerConsumer items = event.getItemColors()::register;
            items.register((s, t) -> t == 1 ? getColorValue(CandleItem.getDyeColorNamed(s), s) : -1, ModItems.CANDLE.get());

        }
    }

    @Override
    public void setColor(ItemStack p_41116_, int p_41117_) {
        DyeableLeatherItem.super.setColor(p_41116_, p_41117_);
    }

    public static void setColorStatic(ItemStack stack, int color) {
        stack.getOrCreateTagElement("display").putInt("color", color);
    }


    public static void setHeight(ItemStack p_41116_, int p_41117_) {
        p_41116_.getOrCreateTagElement("display").putInt("height", p_41117_);
    }

    public static int getHeight(ItemStack stack) {
        if(stack.hasTag()){
            CompoundTag compoundtag = stack.getTagElement("display");
            return compoundtag != null && compoundtag.contains("height", 99) ? compoundtag.getInt("height") : 7;
        }
        return 7;
    }

    public static void setCooldown(ItemStack p_41116_, int p_41117_) {
        p_41116_.getOrCreateTag().putInt("cooldown", p_41117_);
    }

    public static int getCooldown(ItemStack stack) {
        if(stack.hasTag()){
            CompoundTag compoundtag = stack.getTag();
            return compoundtag != null && compoundtag.contains("cooldown", 99) ? compoundtag.getInt("cooldown") : 0;
        }
        return 0;
    }

    public static void setHerbLayer(ItemStack stack, String herbLayer) {
        CompoundTag tag = stack.getOrCreateTagElement("herb");
        if(herbLayer != null)
            tag.putString("layer", herbLayer);
    }

    public static void setBaseLayer(ItemStack stack, String baseLayer) {
        CompoundTag tag = stack.getOrCreateTagElement("base");
        if(baseLayer != null)
            tag.putString("layer", baseLayer);
    }

    public static void setBaseLayerFromBlock(ItemStack stack, String baseLayer) {
        CompoundTag tag = stack.getOrCreateTagElement("base");
        if(baseLayer != null) {
            tag.putString("layer", baseLayer);
            tag.putBoolean("layerFromBlockLocation", true);
        }
    }

    public static void setGlowLayer(ItemStack stack, String glowLayer) {
        CompoundTag tag = stack.getOrCreateTagElement("glow");
        if(glowLayer != null)
            tag.putString("layer", glowLayer);
    }

    public static void setSwirlLayer(ItemStack stack, String swirlLayer) {
        CompoundTag tag = stack.getOrCreateTagElement("swirl");
        if(swirlLayer != null)
            tag.putString("layer", swirlLayer);
    }

    public static void setEffectLocation(ItemStack stack, String effect) {
        CompoundTag tag = stack.getOrCreateTag();
        if(effect != null)
            tag.putString("effect", effect);
        else{
            tag.remove("effect");
        }
    }

    public static void setEffectParticle(ItemStack stack, List<ResourceLocation> effectParticle) {
        CompoundTag tag = stack.getOrCreateTagElement("effectParticle");
        for(int i = 0; i < effectParticle.size(); i++){
            if (effectParticle.get(i) != null)
                tag.putString("particle" + i, effectParticle.get(i).toString());
            else {
                tag.remove("particle");
            }
        }
    }

    public static String getHerbLayer(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("herb");
        if(tag == null) return null;
        if(tag.contains("layerFromBlockLocation") && tag.getBoolean("layerFromBlockLocation")) {
            if(tag.contains("layer")) {
                BlockState blockState = Registry.BLOCK.get(new ResourceLocation(tag.getString("layer"))).defaultBlockState();
                List<BakedQuad> list = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getQuads(blockState, Direction.NORTH, RandomSource.create());
                if(list.size() > 0);
                return list.get(0).getSprite().getName().getNamespace() + ":textures/" + list.get(0).getSprite().getName().getPath() + ".png";
            }
            return null;
        }
        return tag.contains("layer") ? tag.getString("layer") : null;
    }

    public static String getBaseLayer(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("base");
        if(tag == null) return null;
        if(tag.contains("layerFromBlockLocation") && tag.getBoolean("layerFromBlockLocation")) {
            if(tag.contains("layer")) {
                BlockState blockState = Registry.BLOCK.get(new ResourceLocation(tag.getString("layer"))).defaultBlockState();
                List<BakedQuad> list = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getQuads(blockState, Direction.NORTH, RandomSource.create());
                if(!list.isEmpty())
                    return list.get(0).getSprite().getName().getNamespace() + ":textures/" + list.get(0).getSprite().getName().getPath() + ".png";
            }
            return null;
        }
        return tag.contains("layer") ? tag.getString("layer") : null;
    }

    public static String getGlowLayer(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("glow");
        if(tag == null) return null;
        if(tag.contains("layerFromBlockLocation") && tag.getBoolean("layerFromBlockLocation")) {
            if(tag.contains("layer")) {
                BlockState blockState = Registry.BLOCK.get(new ResourceLocation(tag.getString("layer"))).defaultBlockState();
                List<BakedQuad> list = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getQuads(blockState, Direction.NORTH, RandomSource.create());
                if(list.size() > 0);
                return list.get(0).getSprite().getName().getNamespace() + ":textures/" + list.get(0).getSprite().getName().getPath() + ".png";
            }
            return null;
        }
        return tag.contains("layer") ? tag.getString("layer") : null;
    }

    public static String getSwirlLayer(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("swirl");
        if(tag == null) return null;
        if(tag.contains("layerFromBlockLocation") && tag.getBoolean("layerFromBlockLocation")) {
            if(tag.contains("layer")) {
                BlockState blockState = Registry.BLOCK.get(new ResourceLocation(tag.getString("layer"))).defaultBlockState();
                List<BakedQuad> list = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getQuads(blockState, Direction.NORTH, RandomSource.create());
                if(list.size() > 0);
                return list.get(0).getSprite().getName().getNamespace() + ":textures/" + list.get(0).getSprite().getName().getPath() + ".png";
            }
            return null;
        }
        return tag.contains("layer") ? tag.getString("layer") : null;
    }

    public static String getEffectLocation(ItemStack stack) {
        if(stack.hasTag()){
            return stack.getOrCreateTag().getString("effect");
        }
        return null;
    }

    public static List<ResourceLocation> getEffectParticle(ItemStack stack) {
        if(stack.hasTag()){
            List<ResourceLocation> list = new ArrayList<>();
            CompoundTag tag = stack.getOrCreateTagElement("effectParticle");

            for(int i = 0; i < tag.size(); i++){
                list.add(new ResourceLocation(tag.getString("particle" + i)));
            }

            return list;
        }
        return null;
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = getColorStatic(stack);
        if(color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : Candle.BASE_COLOR;
    }

    public static int getDyeColorNamed(String name) {

        if(HexereiUtil.getDyeColorNamed(name)!= null){
            float f3 = (((Hexerei.getClientTicks()) / 10f * 4) % 16) / (float) 16;

            DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
            DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);

            float[] afloat1 = Sheep.getColorArray(col1);
            float[] afloat2 = Sheep.getColorArray(col2);
            float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            return HexereiUtil.getColorValue(f, f1, f2);
        }
        return 0;

    }

    public static DyeColor getDyeColorNamed(ItemStack stack) {

        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }

}
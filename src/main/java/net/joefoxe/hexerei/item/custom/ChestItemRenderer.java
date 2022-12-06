package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.block.custom.ModChest;
import net.joefoxe.hexerei.tileentity.ModChestBlockEntity;
import net.joefoxe.hexerei.tileentity.renderer.ModChestRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestItemRenderer extends CustomItemRenderer {

    private ModChestRenderer renderer;

    private static final Minecraft minecraft = Minecraft.getInstance();

    @OnlyIn(Dist.CLIENT)
    public static ModChestBlockEntity loadBlockEntityFromItem(CompoundTag tag, ItemStack item) {
        if (item.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof ModChest modChest) {
                ModChestBlockEntity te = (ModChestBlockEntity)modChest.newBlockEntity(BlockPos.ZERO, block.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
                if(te != null)
                    if(item.hasCustomHoverName())
                        te.setCustomName(item.getHoverName());
                return te;
            }
        }
        return null;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        if (renderer == null) {
            renderer = new ModChestRenderer(new BlockEntityRendererProvider.Context(minecraft.getBlockEntityRenderDispatcher(), minecraft.getBlockRenderer(), minecraft.getItemRenderer(), minecraft.getEntityRenderDispatcher(), minecraft.getEntityModels(), minecraft.font));
        }

        CompoundTag tag = new CompoundTag();
        if(itemStack.hasTag())
            tag = itemStack.getOrCreateTag();
        ModChestBlockEntity tileEntityIn = loadBlockEntityFromItem(tag, itemStack);

        stack.pushPose();
        stack.translate(0.2, -0.1, -0.10);
        if(tileEntityIn != null)
            renderer.render(tileEntityIn, minecraft.getPartialTick(), stack, source, light, overlay);
        stack.popPose();

    }

}

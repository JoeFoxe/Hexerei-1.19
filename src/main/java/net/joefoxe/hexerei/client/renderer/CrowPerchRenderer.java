package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CrowPerchRenderer {
    private static final float BOX_SIZE = 0.5f;
    private static final float BOX_START = (1f - BOX_SIZE) / 2f;

    private static ItemStack lastStackMain = ItemStack.EMPTY;
    private static ItemStack lastStackOff = ItemStack.EMPTY;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && Hexerei.proxy.getPlayer() != null) {
            ItemStack curItemMain = Hexerei.proxy.getPlayer().getMainHandItem();
            ItemStack curItemOff = Hexerei.proxy.getPlayer().getOffhandItem();
            if (!ItemStack.matches(curItemMain, lastStackMain)) {
                lastStackMain = curItemMain.copy();
            }
            if (!ItemStack.matches(curItemOff, lastStackOff)) {
                lastStackOff = curItemOff.copy();
            }
        }
    }

    @SubscribeEvent
    public static void renderWorldLastEvent(RenderLevelStageEvent event) {

        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS){
            if (CrowWhitelistEvent.whiteListingCrow != null) { // Select
                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                PoseStack matrixStack = event.getPoseStack();

                matrixStack.pushPose();

                Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                renderWhitelisting(buffer, matrixStack, CrowWhitelistEvent.whiteListingCrow);

                matrixStack.popPose();
            }

            if (lastStackMain.getItem() instanceof CrowFluteItem) {
                int command = lastStackMain.getOrCreateTag().getInt("commandMode");
                if (command == 2) { // Perch
                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    PoseStack matrixStack = event.getPoseStack();

                    matrixStack.pushPose();

                    Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                    renderPerch(buffer, matrixStack, lastStackMain);

                    matrixStack.popPose();
                }
                if (command == 1 || command == 2) { // Select
                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    PoseStack matrixStack = event.getPoseStack();

                    matrixStack.pushPose();

                    Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                    renderSelect(buffer, matrixStack, lastStackMain);

                    matrixStack.popPose();
                }
            }
            if (lastStackOff.getItem() instanceof CrowFluteItem) {
                int command = lastStackOff.getOrCreateTag().getInt("commandMode");
                if (command == 2) { // Perch
                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    PoseStack matrixStack = event.getPoseStack();

                    matrixStack.pushPose();

                    Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                    renderPerch(buffer, matrixStack, lastStackOff);

                    matrixStack.popPose();
                }
                if (command == 1 || command == 2) { // Select
                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    PoseStack matrixStack = event.getPoseStack();

                    matrixStack.pushPose();

                    Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                    renderSelect(buffer, matrixStack, lastStackOff);

                    matrixStack.popPose();
                }
            }
        }
    }

    private static void renderPillar(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset){
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
//        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;

        faceBuilder.vertex(posMat,xOffset, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).color(r, g, b, alpha).endVertex();

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);
    }

    private static void renderHorizontalPillar(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset){
//        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;

        faceBuilder.vertex(posMat,xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).color(r, g, b, alpha).endVertex();

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);
    }

    private static void renderHorizontalPillarTurned(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset){
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;

        faceBuilder.vertex(posMat,xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();

        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();
        faceBuilder.vertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).color(r, g, b, alpha).endVertex();

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);
    }

    private static void renderPerch(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, ItemStack stack) {

        ListTag id = stack.getOrCreateTag().getList("crowList", CompoundTag.TAG_COMPOUND);
        BlockPos pos;
        Map<BlockPos, Integer> map = new HashMap<>();

        for(int i = 0; i < id.size(); i++){
            CompoundTag tag = id.getCompound(i);

            int crowId = tag.getInt("ID");

            if ((Hexerei.proxy.getPlayer().level).getEntity(crowId) instanceof CrowEntity crow && ((CrowEntity) (Hexerei.proxy.getPlayer().level).getEntity(crowId)).getPerchPos() != null) {

                pos = crow.getPerchPos();
                double topOffset = Hexerei.proxy.getPlayer().level.getBlockState(pos).getBlock().getOcclusionShape(Hexerei.proxy.getPlayer().level.getBlockState(pos), Hexerei.proxy.getPlayer().level, pos).max(Direction.Axis.Y);
                int amount;
                if (!map.containsKey(pos)) {
                    amount = 1;
                } else {
                    if (map.get(pos) >= 3)
                        continue;
                    amount = map.get(pos) + 1;
                }

                map.put(pos, amount);
//                if (topOffset > 0.5f && Hexerei.proxy.getPlayer().level.getBlockState(pos).getOcclusionShape(Hexerei.proxy.getPlayer().level, pos).max(Direction.Axis.Y)  > 0.5f) {
//                    pos = ((CrowEntity) (Hexerei.proxy.getPlayer().level).getEntity(crowId)).getPerchPos().above();
//                }

                Vec3 vec3 = new Vec3(pos.getX(), pos.getY() + topOffset, pos.getZ());



                matrixStack.pushPose();
                matrixStack.translate(vec3.x, vec3.y, vec3.z);
                Matrix4f posMat = matrixStack.last().pose();
                int color = 0x3B143D;
                if(crow.getDyeColorId() != -1)
                    color = crow.getDyeColor().getMaterialColor().col;

                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0xFF00) >> 8;
                int b = color & 0xFF;
                int alpha = 40;

//                matrixStack.translate(0.5f, Mth.sin(Hexerei.getClientTicks() / 10f) / 10f, 0.5f);
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees(Hexerei.getClientTicks()));
//                matrixStack.translate(-0.5f, 0, -0.5f);
                matrixStack.translate(BOX_START, BOX_START, BOX_START);

                renderPillar(buffer, matrixStack, BOX_SIZE * -0.1f, 0, BOX_SIZE * -0.1f);
                renderPillar(buffer, matrixStack, BOX_SIZE, 0, BOX_SIZE * -0.1f);
                renderPillar(buffer, matrixStack, BOX_SIZE * -0.1f, 0, BOX_SIZE);
                renderPillar(buffer, matrixStack, BOX_SIZE, 0, BOX_SIZE);

                renderHorizontalPillar(buffer, matrixStack, BOX_SIZE * 0.1f,  -BOX_SIZE * 0.1f, 0);
                renderHorizontalPillar(buffer, matrixStack, -BOX_SIZE,  -BOX_SIZE * 0.1f, 0);
                renderHorizontalPillarTurned(buffer, matrixStack, 0,  -BOX_SIZE * 0.1f, BOX_SIZE * 0.1f);
                renderHorizontalPillarTurned(buffer, matrixStack, 0,  -BOX_SIZE * 0.1f, -BOX_SIZE);

                renderHorizontalPillar(buffer, matrixStack, BOX_SIZE * 0.1f,  BOX_SIZE, 0);
                renderHorizontalPillar(buffer, matrixStack, -BOX_SIZE,  BOX_SIZE, 0);
                renderHorizontalPillarTurned(buffer, matrixStack, 0,  BOX_SIZE, BOX_SIZE * 0.1f);
                renderHorizontalPillarTurned(buffer, matrixStack, 0,  BOX_SIZE, -BOX_SIZE);

                VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

                RenderSystem.disableDepthTest();
                buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);

                matrixStack.popPose();

            }
//            tag.putInt("ID", (Hexerei.proxy.getPlayer().level).getEntity(crowId).getId());
//            crows.add((CrowEntity) ((ServerLevel) player.level).getEntity(crowId));
        }

    }

    private static void renderSelect(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, ItemStack stack) {

        ListTag id = stack.getOrCreateTag().getList("crowList", CompoundTag.TAG_COMPOUND);
        Vec3 pos;

        for(int i = 0; i < id.size(); i++){
            CompoundTag tag = id.getCompound(i);

            int crowId = tag.getInt("ID");

            if ((Hexerei.proxy.getPlayer().level).getEntity(crowId) instanceof CrowEntity crow) {

                pos = crow.position();
//                double topOffset = Hexerei.proxy.getPlayer().level.getBlockState(pos).getBlock().getOcclusionShape(Hexerei.proxy.getPlayer().level.getBlockState(pos), Hexerei.proxy.getPlayer().level, pos).max(Direction.Axis.Y);
//                int amount;
//                if(!map.containsKey(pos)) {
//                    amount = 1;
//                }
//                else {
//                    if(map.get(pos) >= 3)
//                        continue;
//                    amount = map.get(pos) + 1;
//                }
//
//                map.put(pos, amount);
//                if (topOffset > 0.5f && Hexerei.proxy.getPlayer().level.getBlockState(pos).getOcclusionShape(Hexerei.proxy.getPlayer().level, pos).max(Direction.Axis.Y)  > 0.5f) {
//                    pos = ((CrowEntity) (Hexerei.proxy.getPlayer().level).getEntity(crowId)).getPerchPos().above();
//                }

//                Vec3 vec3 = new Vec3(pos.getX(), pos.getY() + topOffset, pos.getZ());

                matrixStack.pushPose();
                matrixStack.translate(pos.x, pos.y + 0.45f, pos.z);
                Matrix4f posMat = matrixStack.last().pose();
                int color = 0x3B143D;

                if(crow.getCommand() == 0)
                    color = 0x00969E;
                if(crow.getCommand() == 1)
                    color = 0x771100;
                if(crow.getCommand() == 2)
                    color = 0x009602;


                if(crow.getCommand() == 3) {
                    if(crow.getHelpCommand() == 0)
                        color = 0x3B143D;
                    if(crow.getHelpCommand() == 1)
                        color = 0xADA100;
                    if(crow.getHelpCommand() == 2)
                        color = 0x684005;
                }

                if(crow.getDyeColorId() != -1)
                    color = crow.getDyeColor().getMaterialColor().col;

                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0xFF00) >> 8;
                int b = color & 0xFF;
                int alpha = 80;

                matrixStack.translate(0, Mth.sin((Hexerei.getClientTicks() + (crowId * 20)) / 10f) / 10f, 0);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(Hexerei.getClientTicks() + (crowId * 20)));
                matrixStack.translate(-0.5f, 0, -0.5f);
                matrixStack.translate(BOX_START, BOX_START, BOX_START);
                matrixStack.scale(0.35f, 0.35f, 0.35f);
                matrixStack.translate(0.5f, 0, 0.5f);

                VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

                lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
                lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

                RenderSystem.disableDepthTest();
                buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);

                matrixStack.popPose();

            }
        }

    }
    private static void renderWhitelisting(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, CrowEntity crow) {

        Vec3 pos = crow.position();

        matrixStack.pushPose();
        matrixStack.translate(pos.x, pos.y + 0.45f, pos.z);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0xE2E2E2;

        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 80;

        matrixStack.translate(0, Mth.sin((Hexerei.getClientTicks()) / 10f) / 10f, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(Hexerei.getClientTicks()));
        matrixStack.translate(-0.5f, 0, -0.5f);
        matrixStack.translate(BOX_START, BOX_START, BOX_START);
        matrixStack.scale(0.35f, 0.35f, 0.35f);
        matrixStack.translate(0.5f, 0, 0.5f);

        VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);

        lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

        lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

        lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();

        lineBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).endVertex();

        lineBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();
        lineBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).endVertex();

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HILIGHT_FACE);

        matrixStack.popPose();

    }

}
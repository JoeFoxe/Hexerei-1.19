package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class CrowPerchRenderer {
    private static final float BOX_SIZE = 0.5f;
    private static final float BOX_START = (1f - BOX_SIZE) / 2f;

    private static ItemStack lastStackMain = ItemStack.EMPTY;
    private static ItemStack lastStackOff = ItemStack.EMPTY;
    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (Hexerei.proxy.getPlayer() != null) {
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
                FluteData fluteData = lastStackMain.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
                int command = fluteData.commandMode();
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
                FluteData fluteData = lastStackOff.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
                int command = fluteData.commandMode();
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
//        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
//        VertexConsumer faceBuilder = buffer.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true));
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;
// Front face
        faceBuilder.addVertex(posMat,xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

// Back face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

// Left face
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

// Right face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

// Bottom face
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

// Top face
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset + BOX_SIZE * .1f).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * .1f, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
    }

    private static void renderHorizontalPillar(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset){
//        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
//        VertexConsumer faceBuilder = buffer.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true));
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;
// Front face
        faceBuilder.addVertex(posMat,xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

// Back face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

// Left face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

// Right face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

// Bottom face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

// Top face
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0, 1).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1, 1).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(1, 0).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE * 0.9f, yOffset + BOX_SIZE * 0.1f, zOffset).setColor(r, g, b, alpha).setUv(0, 0).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
    }

    private static void renderHorizontalPillarTurned(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset){
        VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
        Matrix4f posMat = matrixStack.last().pose();
        int color = 0x110511;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int alpha = 40;


        faceBuilder.addVertex(posMat,xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset + BOX_SIZE, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        faceBuilder.addVertex(posMat, xOffset, yOffset + BOX_SIZE * 0.1f, zOffset + BOX_SIZE * 0.9f).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
    }

    private static void renderPerch(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, ItemStack stack) {

        FluteData data = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
        BlockPos pos;
        Map<BlockPos, Integer> map = new HashMap<>();

        for(int i = 0; i < data.crowList().size(); i++){
            int crowId = data.crowList().get(i).id();
            Level level = Hexerei.proxy.getPlayer().level();
            if ((level).getEntity(crowId) instanceof CrowEntity crow && ((CrowEntity) (level).getEntity(crowId)).getPerchPos() != null) {

                pos = crow.getPerchPos();
                double topOffset = level.getBlockState(pos).getOcclusionShape(level, pos).max(Direction.Axis.Y);
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
                    color = crow.getDyeColor().getMapColor().col;

                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0xFF00) >> 8;
                int b = color & 0xFF;
                int alpha = 40;

                matrixStack.translate(0.5f, Mth.sin(ClientEvents.getClientTicks() / 25f) / 25f, 0.5f);
                matrixStack.mulPose(Axis.YP.rotationDegrees(ClientEvents.getClientTicks() * 0.5f));
                matrixStack.translate(-0.5f, 0, -0.5f);
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

                VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
//                VertexConsumer lineBuilder = buffer.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true));

                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

// Back face
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

// Left face
                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

// Right face
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

// Bottom face
                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

// Top face
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

                RenderSystem.disableDepthTest();
                buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);

                matrixStack.popPose();

            }
//            tag.putInt("ID", (Hexerei.proxy.getPlayer().level).getEntity(crowId).getId());
//            crows.add((CrowEntity) ((ServerLevel) player.level).getEntity(crowId));
        }

    }

    private static void renderSelect(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, ItemStack stack) {

        FluteData data = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
        Vec3 pos;

        for(int i = 0; i < data.crowList().size(); i++){
            int crowId = data.crowList().get(i).id();

            if ((Hexerei.proxy.getPlayer().level()).getEntity(crowId) instanceof CrowEntity crow) {

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
                    color = crow.getDyeColor().getMapColor().col;

                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0xFF00) >> 8;
                int b = color & 0xFF;
                int alpha = 80;

                matrixStack.translate(0, Mth.sin((ClientEvents.getClientTicks() + (crowId * 20)) / 10f) / 10f, 0);
                matrixStack.mulPose(Axis.YP.rotationDegrees(ClientEvents.getClientTicks() + (crowId * 20)));
                matrixStack.translate(-0.5f, 0, -0.5f);
                matrixStack.translate(BOX_START, BOX_START, BOX_START);
                matrixStack.scale(0.35f, 0.35f, 0.35f);
                matrixStack.translate(0.5f, 0, 0.5f);

                VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);

                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

// Back face
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

// Left face
                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

// Right face
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

// Bottom face
                lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

// Top face
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
                lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

                RenderSystem.disableDepthTest();
                buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);

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

        matrixStack.translate(0, Mth.sin((ClientEvents.getClientTicks()) / 10f) / 10f, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(ClientEvents.getClientTicks()));
        matrixStack.translate(-0.5f, 0, -0.5f);
        matrixStack.translate(BOX_START, BOX_START, BOX_START);
        matrixStack.scale(0.35f, 0.35f, 0.35f);
        matrixStack.translate(0.5f, 0, 0.5f);

        VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);

        lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);

// Back face
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
        lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);

// Left face
        lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);

// Right face
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);

// Bottom face
        lineBuilder.addVertex(posMat, 0, 0, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
        lineBuilder.addVertex(posMat, 0, 0, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);

// Top face
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        lineBuilder.addVertex(posMat, BOX_SIZE, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
        lineBuilder.addVertex(posMat, 0, BOX_SIZE, 0).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);

        RenderSystem.disableDepthTest();
        buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);

        matrixStack.popPose();

    }

}
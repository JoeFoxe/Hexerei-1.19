package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.OptionalInt;

public class BookCanvasItemRenderer extends CustomItemRenderer {

	public static ModelResourceLocation CANVAS = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "item/book_canvas_back"));

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		matrixStackIn.pushPose();
		matrixStackIn.translate(0.7, 0.4, 0.4);

		BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(CANVAS);

		if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {


			Minecraft.getInstance().getItemRenderer().render(
					stack,
					itemDisplayContext,
					itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
					matrixStackIn,
					bufferIn,
					combinedLightIn,
					combinedOverlayIn,
					baseModel
			);

			baseModel.applyTransform(itemDisplayContext, matrixStackIn, itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);



			if (!stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().isEmpty()) {
				CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

				if (tag.contains("painting_location")) {

					float size = 0.5f / 16 * 12;
					float offset = -1f / 16;

					float f19 = 1;
					float f20 = 0;
					float f21 = 1;
					float f22 = 0;
					VertexConsumer consumer2 = bufferIn.getBuffer(ModRenderTypes.entityCutout(ResourceLocation.parse("hexerei:textures/book/canvas.png")));
					this.vertex(matrixStackIn.last(), consumer2, size, -size + offset, f19, f21, 0.03125F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer2, -size, -size + offset, f20, f21, 0.03125F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer2, -size, size + offset, f20, f22, 0.03125F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer2, size, size + offset, f19, f22, 0.03125F, 0, 0, 1, combinedLightIn);
					if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
						bufferSource.endBatch();

					f19 = tag.contains("U1") ? tag.getFloat("U1") : 1;
					f20 = tag.contains("U0") ? tag.getFloat("U0") : 0;
					f21 = tag.contains("V1") ? tag.getFloat("V1") : 1;
					f22 = tag.contains("V0") ? tag.getFloat("V0") : 0;
					VertexConsumer consumer1 = bufferIn.getBuffer(ModRenderTypes.entityTranslucent(ResourceLocation.parse(tag.getString("painting_location"))));
					this.vertex(matrixStackIn.last(), consumer1, size, -size + offset, f19, f21, 0.03165F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer1, -size, -size + offset, f20, f21, 0.03165F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer1, -size, size + offset, f20, f22, 0.03165F, 0, 0, 1, combinedLightIn);
					this.vertex(matrixStackIn.last(), consumer1, size, size + offset, f19, f22, 0.03165F, 0, 0, 1, combinedLightIn);
					if (bufferIn instanceof MultiBufferSource.BufferSource bufferSource)
						bufferSource.endBatch();

				}
			}
		}



		matrixStackIn.popPose();
	}

	private void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int packedLight) {
		consumer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
	}


	private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
	                        int combinedLightIn) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn,
				OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
	}

	private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
	                        int combinedLightIn, ItemDisplayContext itemDisplayContext) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, itemDisplayContext, combinedLightIn,
				OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, Minecraft.getInstance().level, 1);
	}

//            this.render(itemStack, diplayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, bakedmodel);
	private void renderItem(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn,
	                        int combinedLightIn, ItemDisplayContext itemDisplayContext, BakedModel model) {
		Minecraft.getInstance().getItemRenderer().render(stack, itemDisplayContext, false, matrixStackIn, bufferIn, combinedLightIn,
				OverlayTexture.NO_OVERLAY, model);
	}

	private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
	}

	public OptionalInt getFramedMapId(ItemStack stack) {
		if (stack.is(Items.FILLED_MAP)) {
			MapId mapId = stack.get(DataComponents.MAP_ID);
			if (mapId != null) {
				return OptionalInt.of(mapId.id());
			}
		}

		return OptionalInt.empty();
	}

	private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
		renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

	}

	public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
		RenderShape rendershape = p_110913_.getRenderShape();
		if (rendershape != RenderShape.INVISIBLE) {
			switch (rendershape) {
				case MODEL -> {
					BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
					BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
					int i = color;
					float f = (float) (i >> 16 & 255) / 255.0F;
					float f1 = (float) (i >> 8 & 255) / 255.0F;
					float f2 = (float) (i & 255) / 255.0F;

//                    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_) {
					dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
				}
				case ENTITYBLOCK_ANIMATED -> {
					ItemStack stack = new ItemStack(p_110913_.getBlock());
					IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
				}
			}

		}
	}

}
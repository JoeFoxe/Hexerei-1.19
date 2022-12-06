package net.joefoxe.hexerei.block.connected;


import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelData.Builder;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class CTModel extends BakedModelWrapperWithData {

    private static final ModelProperty<CTData> CT_PROPERTY = new ModelProperty<>();

    private final ConnectedTextureBehaviour behaviour;

    public CTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour) {
        super(originalModel);
        this.behaviour = behaviour;
    }

    @Override
    protected Builder gatherModelData(Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state) {
        return builder.with(CT_PROPERTY, createCTData(world, pos, state));
    }

    protected CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        CTData data = new CTData();
        MutableBlockPos mutablePos = new MutableBlockPos();
        for (Direction face : Direction.values()) {
            if (!behaviour.buildContextForOccludedDirections()
                    && !Block.shouldRenderFace(state, world, pos, face, mutablePos.setWithOffset(pos, face)))
                continue;
            CTType dataType = behaviour.getDataType(state, face);
            if (dataType == null)
                continue;
            ConnectedTextureBehaviour.CTContext context = behaviour.buildContext(world, pos, state, face, dataType.getContextRequirement());


            int textureIndex = dataType.getTextureIndex(context);


            if(dataType.getExtraFaceVariations() > 0){
                if (textureIndex == 54) {
                    Random random = new Random((long) (Math.abs(pos.getX()) + 1) * (Math.abs(pos.getY()) + 1) * (Math.abs(pos.getZ()) + 1));

                    int rand = random.nextInt((int)(dataType.getExtraFaceVariations() / dataType.getPercent()));
                    if (rand != 0 && rand < dataType.getExtraFaceVariations())
                        textureIndex = (rand * 8) - 1;
                }
            }
            data.put(face, textureIndex);
        }
        return data;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (!extraData.has(CT_PROPERTY))
            return quads;

        CTData data = extraData.get(CT_PROPERTY);
        quads = new ArrayList<>(quads);

        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);

            int index = data.get(quad.getDirection());
            if (index == -1)
                continue;

            CTSpriteShiftEntry spriteShift = behaviour.getShift(state, quad.getDirection(), quad.getSprite());
            if (spriteShift == null)
                continue;
            ResourceLocation loc1 = quad.getSprite().getName();
            ResourceLocation loc2 = spriteShift.getOriginal().getName();

            if (loc1 != loc2)
                continue;

            BakedQuad newQuad = QuadHelper.clone(quad);

            int[] vertexData = newQuad.getVertices();

            for (int vertex = 0; vertex < 4; vertex++) {
                float u = QuadHelper.getU(vertexData, vertex);
                float v = QuadHelper.getV(vertexData, vertex);

                QuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u, index));
                QuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
            }

            quads.set(i, newQuad);
        }

        return quads;
    }

    private static class CTData {
        private final int[] indices;

        public CTData() {
            indices = new int[6];
            Arrays.fill(indices, -1);
        }

        public void put(Direction face, int texture) {
            indices[face.get3DDataValue()] = texture;
        }

        public int get(Direction face) {
            return indices[face.get3DDataValue()];
        }
    }

}
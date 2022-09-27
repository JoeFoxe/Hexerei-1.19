package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.ModChest;
import net.joefoxe.hexerei.tileentity.ModChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class ModChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private boolean xmasTextures;

    public ModChestRenderer(BlockEntityRendererProvider.Context pContext) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.xmasTextures = true;
        }

        ModelPart modelpart = pContext.bakeLayer(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany"), "main"));
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
        ModelPart modelpart1 = pContext.bakeLayer(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany_left"), "main"));
        this.doubleLeftBottom = modelpart1.getChild("bottom");
        this.doubleLeftLid = modelpart1.getChild("lid");
        this.doubleLeftLock = modelpart1.getChild("lock");
        ModelPart modelpart2 = pContext.bakeLayer(new ModelLayerLocation(new ResourceLocation("hexerei", "chest/mahogany_right"), "main"));
        this.doubleRightBottom = modelpart2.getChild("bottom");
        this.doubleRightLid = modelpart2.getChild("lid");
        this.doubleRightLock = modelpart2.getChild("lock");
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyRightLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? pBlockEntity.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock<?> abstractchestblock) {
            boolean flag1 = chesttype != ChestType.SINGLE;
            pPoseStack.pushPose();
            float f = blockstate.getValue(ChestBlock.FACING).toYRot();
            pPoseStack.translate(0.5D, 0.5D, 0.5D);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(-f));
            pPoseStack.translate(-0.5D, -0.5D, -0.5D);
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> neighborcombineresult;
            if (flag) {
                neighborcombineresult = abstractchestblock.combine(blockstate, level, pBlockEntity.getBlockPos(), true);
            } else {
                neighborcombineresult = DoubleBlockCombiner.Combiner::acceptNone;
            }

            float f1 = neighborcombineresult.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(pBlockEntity)).get(pPartialTick);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            int i = neighborcombineresult.<Int2IntFunction>apply(new BrightnessCombiner<>()).applyAsInt(pPackedLight);
            VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutout(getLoc(chesttype, pBlockEntity)));
            if (flag1) {
                if (chesttype == ChestType.LEFT) {
                    this.render(pPoseStack, vertexConsumer, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, pPackedOverlay);
                } else {
                    this.render(pPoseStack, vertexConsumer, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, pPackedOverlay);
                }
            } else {
                this.render(pPoseStack, vertexConsumer, this.lid, this.lock, this.bottom, f1, i, pPackedOverlay);
            }

            pPoseStack.popPose();
        }
    }

    private void render(PoseStack pPoseStack, VertexConsumer pConsumer, ModelPart pLidPart, ModelPart pLockPart, ModelPart pBottomPart, float pLidAngle, int pPackedLight, int pPackedOverlay) {
        pLidPart.xRot = -(pLidAngle * ((float)Math.PI / 2F));
        pLockPart.xRot = pLidPart.xRot;
        pLidPart.render(pPoseStack, pConsumer, pPackedLight, pPackedOverlay);
        pLockPart.render(pPoseStack, pConsumer, pPackedLight, pPackedOverlay);
        pBottomPart.render(pPoseStack, pConsumer, pPackedLight, pPackedOverlay);
    }

    public static final ResourceLocation WILLOW_CHEST_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/willow.png");
    public static final ResourceLocation MAHOGANY_CHEST_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/mahogany.png");
    public static final ResourceLocation HOOTY_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/hooty.png");
    public static final ResourceLocation HOOTLE_LOCATION = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/hootle.png");
    public static final ResourceLocation WILLOW_CHEST_LOCATION_LEFT = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/willow_left.png");
    public static final ResourceLocation WILLOW_CHEST_LOCATION_RIGHT = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/willow_right.png");
    public static final ResourceLocation MAHOGANY_CHEST_LOCATION_LEFT = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/mahogany_left.png");
    public static final ResourceLocation MAHOGANY_CHEST_LOCATION_RIGHT = new ResourceLocation(Hexerei.MOD_ID,"textures/entity/chest/mahogany_right.png");
    protected ResourceLocation getLoc(ChestType chestType, BlockEntity block) {
        if(block instanceof ModChestBlockEntity blockEntity)
        {

            if (blockEntity.hasCustomName() && blockEntity.getCustomName().getString().equals("Hootle") && chestType == ChestType.SINGLE){
                return HOOTLE_LOCATION;
            }
            if (blockEntity.hasCustomName() && blockEntity.getCustomName().getString().equals("Hooty") && chestType == ChestType.SINGLE){
                return HOOTY_LOCATION;
            }
        }
        ModChest.WoodType style = ModChest.WoodType.WILLOW;
        if(block.getBlockState().hasProperty(ModChest.WOOD_TYPE)){
            style = block.getBlockState().getValue(ModChest.WOOD_TYPE);
        }
        ResourceLocation left = getLeft(style);
        ResourceLocation right = getRight(style);
        ResourceLocation single = getSingle(style);

        return chooseLoc(chestType, single, left, right);
    }
    public ResourceLocation getLeft(ModChest.WoodType style) {
        return switch (style) {
            case MAHOGANY ->
                MAHOGANY_CHEST_LOCATION_LEFT;
            case POLISHED_MAHOGANY ->
                MAHOGANY_CHEST_LOCATION_LEFT;
            case POLISHED_WILLOW ->
                WILLOW_CHEST_LOCATION_LEFT;
            case WILLOW ->
                WILLOW_CHEST_LOCATION_LEFT;
        };
    }
    public ResourceLocation getRight(ModChest.WoodType style) {
        return switch (style) {
            case MAHOGANY ->
                MAHOGANY_CHEST_LOCATION_RIGHT;
            case POLISHED_MAHOGANY ->
                MAHOGANY_CHEST_LOCATION_RIGHT;
            case POLISHED_WILLOW ->
                WILLOW_CHEST_LOCATION_RIGHT;
            case WILLOW ->
                WILLOW_CHEST_LOCATION_RIGHT;
        };
    }
    public ResourceLocation getSingle(ModChest.WoodType style) {
        return switch (style) {
            case MAHOGANY ->
                MAHOGANY_CHEST_LOCATION;
            case POLISHED_MAHOGANY ->
                MAHOGANY_CHEST_LOCATION;
            case POLISHED_WILLOW ->
                WILLOW_CHEST_LOCATION;
            case WILLOW ->
                WILLOW_CHEST_LOCATION;
        };
    }

    public static ResourceLocation chooseLoc(ChestType pChestType, ResourceLocation pDoubleMaterial, ResourceLocation pLeftMaterial, ResourceLocation pRightMaterial) {
        switch (pChestType) {
            case LEFT:
                return pLeftMaterial;
            case RIGHT:
                return pRightMaterial;
            case SINGLE:
            default:
                return pDoubleMaterial;
        }
    }
}

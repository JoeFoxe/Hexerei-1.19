package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class CauldronParticle extends TextureSheetParticle {

    private final ResourceLocation TEXTURE = new ResourceLocation(Hexerei.MOD_ID,
            "textures/particle/cauldron_boil_particle.png");

    // thanks to understanding simibubi's code from the Create mod for rendering particles I was able to render my own :D
    public static final Vec3[] CUBE = {
            // top render
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, -0.5),

            // bottom render
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(-0.5, 0.1, -0.5),

            // front render
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),

            // back render
            new Vec3(0.5, -0.1, -0.5),
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, -0.1, -0.5),

            // left render
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(-0.5, -0.1, 0.5),

            // right render
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(0.5, -0.1, -0.5),



            // top render
            new Vec3(0.5, -0.1, -0.5),
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(-0.5, -0.1, -0.5),

            // bottom render
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, -0.5),

            // front render
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),

            // back render
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(0.5, -0.1, -0.5),
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, 0.1, -0.5),

            // left render
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(-0.5, 0.1, 0.5),

            // right render
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, -0.5),
            new Vec3(0.5, 0.1, -0.5),
    };

    public static final Vec3[] CUBE_NORMALS = {
            // modified normals for the sides
            new Vec3(0, -0.1, 0),
            new Vec3(0, 0.25, 0),
            new Vec3(0, 0, 0.5),
            new Vec3(0, 0, -0.5),
            new Vec3(-0.5, 0, 0),
            new Vec3(0.5, 0, 0),
    };

    public final static ResourceLocation TEXTURE_BLANK =
            new ResourceLocation(Hexerei.MOD_ID, "textures/block/blank.png");
    private static final ParticleRenderType renderType = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TEXTURE_BLANK);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
//                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
    };

    protected float scale;
    protected float rotationDirection;
    protected float rotation;
    private IClientFluidTypeExtensions clientFluid;
    private boolean canPop;
    int pixelCol = -1;

    public CauldronParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.rotation = 0;
        Random random = new Random();
        setScale(0.2F);  setRotationDirection(random.nextFloat() - 0.5f);
        this.canPop = random.nextInt(3) == 0 && this.lifetime > 10;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public void setRotationDirection(float rotationDirection) {
        this.rotationDirection = rotationDirection;
    }

    @Override
    public void tick() {
        this.rotation = (this.rotationDirection * 0.1f) + this.rotation;
        super.tick();
    }

    public float ease(float x) {
        return (float) (1 - Math.pow(1 - x, 5));
    }
    public static float[] rgbaIntToFloatArray(int rgbInt) {
        int a = rgbInt >> 24 & 255;
        int r = rgbInt >> 16 & 255;
        int g = rgbInt >> 8 & 255;
        int b = rgbInt >> 0 & 255;

        return new float[]{r / 255F, g / 255F, b / 255F, a / 255F};
    }

    @Override
    public void render(VertexConsumer builder, Camera renderInfo, float partial) {
        Vec3 projectedView = renderInfo.getPosition();
        float lerpX = (float) (Mth.lerp(partial, this.xo, this.x) - projectedView.x());
        float lerpY = (float) (Mth.lerp(partial, this.yo, this.y) - projectedView.y());
        float lerpZ = (float) (Mth.lerp(partial, this.zo, this.z) - projectedView.z());

        int light = 15728880;
        double ageMultiplier = 1 - Math.pow(Mth.clamp(age + partial, 0, lifetime), 3) / Math.pow(lifetime, 3);

        RenderSystem._setShaderTexture(0, TEXTURE);

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 4; j++) {
                float alpha = this.alpha * 0.75f;
                Vec3 vec = CUBE[i * 4 + j];
                float popScale = Mth.clamp(age + partial - (lifetime - 5), 0, 5) / 5f;
                vec = vec
                        .yRot(this.rotation)
                        .scale(scale * ageMultiplier * ((canPop && !(i % 6 == 0 || i % 6 == 1)) ? (1 + ease(popScale)) : 1))
                        .add(lerpX, lerpY, lerpZ);

                Vec3 normal = CUBE_NORMALS[i % 6];

                if (canPop){
                    if (popScale > 0){
                        vec = vec.add(normal.yRot(this.rotation).scale(ease(popScale) / 4));
                        alpha = alpha * Mth.clamp(ease(Mth.clamp(1f - popScale, 0, 1)), 0, 1);
                    }
                }

                float[] cols = rgbaIntToFloatArray(pixelCol);

                if (i / 6 == 1) {
                    normal = normal.multiply(-1, -1, -1);
                }

                if(i % 6 == 1) {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 1.35f * cols[2], 0, 1.0f), Mth.clamp(gCol * 1.35f * cols[1], 0, 1.0f), Mth.clamp(bCol * 1.35f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }else if(i % 6 == 0) {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 0.95f * cols[2], 0, 1.0f), Mth.clamp(gCol * 0.95f * cols[1], 0, 1.0f), Mth.clamp(bCol * 0.95f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }else if(i % 6 == 2) {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 1.15f * cols[2], 0, 1.0f), Mth.clamp(gCol * 1.15f * cols[1], 0, 1.0f), Mth.clamp(bCol * 1.15f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }else if(i % 6 == 3) {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 1.2f * cols[2], 0, 1.0f), Mth.clamp(gCol * 1.2f * cols[1], 0, 1.0f), Mth.clamp(bCol * 1.2f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }else if(i % 6 == 4) {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 1.25f * cols[2], 0, 1.0f), Mth.clamp(gCol * 1.25f * cols[1], 0, 1.0f), Mth.clamp(bCol * 1.25f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }else {
                    builder.vertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .uv(0, 0)
                            .color(Mth.clamp(rCol * 1.2f * cols[2], 0, 1.0f), Mth.clamp(gCol * 1.2f * cols[1], 0, 1.0f), Mth.clamp(bCol * 1.2f * cols[0], 0, 1.0f), alpha * cols[3])
                            .normal((float) normal.x, (float) normal.y, (float) normal.z)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .endVertex();
                }
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<CauldronParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(CauldronParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CauldronParticle cauldronParticle = new CauldronParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            Random random = new Random();

//            this.spriteSet = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
            MixingCauldronTile mixingCauldronTile = null;
            FluidStack fluidStack = data.fluid;

            Color color = new Color(BiomeColors.getAverageWaterColor(worldIn, new BlockPos((int) x, (int) (y), (int) z)));

            BlockState blockStateAtPos = worldIn.getBlockState(new BlockPos((int) x, (int) (y - 0.1), (int) z));

            cauldronParticle.clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            Function<ResourceLocation, TextureAtlasSprite> textureAtlasSpriteFunction = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

            ResourceLocation stillLoc = cauldronParticle.clientFluid.getStillTexture(fluidStack);

            TextureAtlasSprite sprite = textureAtlasSpriteFunction.apply(stillLoc);

            if (sprite != null)
                cauldronParticle.pixelCol = sprite.getPixelRGBA(0, random.nextInt(sprite.contents().width()), random.nextInt(sprite.contents().height()));

            int colorInt = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
            float alpha = (colorInt >> 24 & 255) / 275f;
            float red = (colorInt >> 16 & 255) / 275f;
            float green = (colorInt >> 8 & 255) / 275f;
            float blue = (colorInt & 255) / 275f;
//            colorInt = sprite.getPixelRGBA(0, random.nextInt(sprite.contents().width()), random.nextInt(sprite.contents().height()));
//            float alpha2 = (colorInt >> 24 & 255) / 275f;
//            float blue2 = (colorInt >> 16 & 255) / 275f;
//            float green2 = (colorInt >> 8 & 255) / 275f;
//            float red2 = (colorInt & 255) / 275f;

            float colorOffset = (random.nextFloat() * 0.15f);
//            if (red > 0.75f && blue > 0.75f && green > 0.75f)
//                cauldronParticle.setColor(Mth.clamp(red2 + colorOffset, 0, 1), Mth.clamp(green2 + colorOffset, 0, 1), Mth.clamp(blue2 + colorOffset, 0, 1));
//            else
            cauldronParticle.setColor(Mth.clamp(red + colorOffset, 0, 1), Mth.clamp(green + colorOffset, 0, 1), Mth.clamp(blue + colorOffset, 0, 1));


            if (fluidStack.isFluidEqual(new FluidStack(Fluids.WATER, 1)))
                cauldronParticle.setColor(color.getRed() / 450f + colorOffset, color.getGreen() / 450f + colorOffset, color.getBlue() / 450f + colorOffset);

            cauldronParticle.setAlpha(1.0f);

            cauldronParticle.pickSprite(this.spriteSet);
            return cauldronParticle;
        }
    }


}

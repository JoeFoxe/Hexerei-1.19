package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.fluid.PotionFluidType;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;
    @Shadow
    private static int targetBiomeFog = -1;
    @Shadow
    private static int previousBiomeFog = -1;
    @Shadow
    private static long biomeChangedTime = -1L;

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "setupColor", at = @At(value = "HEAD"), cancellable = true)
    private static void setupColor(Camera pCamera, float pPartialTicks, ClientLevel pLevel, int pRenderDistanceChunks, float pBossColorModifier, CallbackInfo ci) {

        BlockPos cameraPos = pCamera.getBlockPosition();
        BlockGetter level = pCamera.level;

        BlockEntity be = level.getBlockEntity(cameraPos);
        if(be instanceof MixingCauldronTile tile){
            double d0 = pCamera.getPosition().y() - (double) 0.11111111F;
            BlockPos blockpos = new BlockPos(pCamera.getPosition().x(), d0, pCamera.getPosition().z());
            if (tile.renderedFluid != null) {
                double d1 = (float) blockpos.getY() + tile.renderedFluid.getAmount() / 2000f;
                if (d1 > d0) {
                    FogType fogtype = getFluidInCamera(tile.renderedFluid);
                    Entity entity = pCamera.getEntity();
                    if (fogtype == FogType.WATER) {
                        long i = Util.getMillis();
                        int j = pLevel.getBiome(new BlockPos(pCamera.getPosition())).value().getWaterFogColor();
                        if (biomeChangedTime < 0L) {
                            targetBiomeFog = j;
                            previousBiomeFog = j;
                            biomeChangedTime = i;
                        }

                        int k = targetBiomeFog >> 16 & 255;
                        int l = targetBiomeFog >> 8 & 255;
                        int i1 = targetBiomeFog & 255;
                        int j1 = previousBiomeFog >> 16 & 255;
                        int k1 = previousBiomeFog >> 8 & 255;
                        int l1 = previousBiomeFog & 255;
                        float f = Mth.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
                        float f1 = Mth.lerp(f, (float)j1, (float)k);
                        float f2 = Mth.lerp(f, (float)k1, (float)l);
                        float f3 = Mth.lerp(f, (float)l1, (float)i1);
                        fogRed = f1 / 255.0F;
                        fogGreen = f2 / 255.0F;
                        fogBlue = f3 / 255.0F;
                        if (targetBiomeFog != j) {
                            targetBiomeFog = j;
                            previousBiomeFog = Mth.floor(f1) << 16 | Mth.floor(f2) << 8 | Mth.floor(f3);
                            biomeChangedTime = i;
                        }
                    } else if (fogtype == FogType.LAVA) {
                        fogRed = 0.6F;
                        fogGreen = 0.1F;
                        fogBlue = 0.0F;
                        biomeChangedTime = -1L;
                    } else if (fogtype == FogType.POWDER_SNOW) {
                        fogRed = 0.623F;
                        fogGreen = 0.734F;
                        fogBlue = 0.785F;
                        biomeChangedTime = -1L;
                        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
                    } else {
                        float f4 = 0.25F + 0.75F * (float)pRenderDistanceChunks / 32.0F;
                        f4 = 1.0F - (float) Math.pow(f4, 0.25D);
                        Vec3 vec3 = pLevel.getSkyColor(pCamera.getPosition(), pPartialTicks);
                        float f6 = (float)vec3.x;
                        float f8 = (float)vec3.y;
                        float f10 = (float)vec3.z;
                        float f11 = Mth.clamp(Mth.cos(pLevel.getTimeOfDay(pPartialTicks) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
                        BiomeManager biomemanager = pLevel.getBiomeManager();
                        Vec3 vec31 = pCamera.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
                        Vec3 vec32 = CubicSampler.gaussianSampleVec3(vec31, (p_109033_, p_109034_, p_109035_) -> {
                            return pLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_109033_, p_109034_, p_109035_).value().getFogColor()), f11);
                        });
                        fogRed = (float)vec32.x();
                        fogGreen = (float)vec32.y();
                        fogBlue = (float)vec32.z();
                        if (pRenderDistanceChunks >= 4) {
                            float f12 = Mth.sin(pLevel.getSunAngle(pPartialTicks)) > 0.0F ? -1.0F : 1.0F;
                            Vector3f vector3f = new Vector3f(f12, 0.0F, 0.0F);
                            float f16 = pCamera.getLookVector().dot(vector3f);
                            if (f16 < 0.0F) {
                                f16 = 0.0F;
                            }

                            if (f16 > 0.0F) {
                                float[] afloat = pLevel.effects().getSunriseColor(pLevel.getTimeOfDay(pPartialTicks), pPartialTicks);
                                if (afloat != null) {
                                    f16 *= afloat[3];
                                    fogRed = fogRed * (1.0F - f16) + afloat[0] * f16;
                                    fogGreen = fogGreen * (1.0F - f16) + afloat[1] * f16;
                                    fogBlue = fogBlue * (1.0F - f16) + afloat[2] * f16;
                                }
                            }
                        }

                        fogRed += (f6 - fogRed) * f4;
                        fogGreen += (f8 - fogGreen) * f4;
                        fogBlue += (f10 - fogBlue) * f4;
                        float f13 = pLevel.getRainLevel(pPartialTicks);
                        if (f13 > 0.0F) {
                            float f14 = 1.0F - f13 * 0.5F;
                            float f17 = 1.0F - f13 * 0.4F;
                            fogRed *= f14;
                            fogGreen *= f14;
                            fogBlue *= f17;
                        }

                        float f15 = pLevel.getThunderLevel(pPartialTicks);
                        if (f15 > 0.0F) {
                            float f18 = 1.0F - f15 * 0.5F;
                            fogRed *= f18;
                            fogGreen *= f18;
                            fogBlue *= f18;
                        }

                        biomeChangedTime = -1L;
                    }

                    float f5 = ((float)pCamera.getPosition().y - (float)pLevel.getMinBuildHeight()) * pLevel.getLevelData().getClearColorScale();

                    if (f5 < 1.0F && fogtype != FogType.LAVA && fogtype != FogType.POWDER_SNOW) {
                        if (f5 < 0.0F) {
                            f5 = 0.0F;
                        }

                        f5 *= f5;
                        fogRed *= f5;
                        fogGreen *= f5;
                        fogBlue *= f5;
                    }

                    if (pBossColorModifier > 0.0F) {
                        fogRed = fogRed * (1.0F - pBossColorModifier) + fogRed * 0.7F * pBossColorModifier;
                        fogGreen = fogGreen * (1.0F - pBossColorModifier) + fogGreen * 0.6F * pBossColorModifier;
                        fogBlue = fogBlue * (1.0F - pBossColorModifier) + fogBlue * 0.6F * pBossColorModifier;
                    }

                    float f7;
                    if (fogtype == FogType.WATER) {
                        if (entity instanceof LocalPlayer) {
                            f7 = ((LocalPlayer)entity).getWaterVision();
                        } else {
                            f7 = 1.0F;
                        }
                    } else {
                        label86: {
                            if (entity instanceof LivingEntity livingentity1) {
                                if (livingentity1.hasEffect(MobEffects.NIGHT_VISION) && !livingentity1.hasEffect(MobEffects.DARKNESS)) {
                                    f7 = GameRenderer.getNightVisionScale(livingentity1, pPartialTicks);
                                    break label86;
                                }
                            }

                            f7 = 0.0F;
                        }
                    }

                    if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
                        float f9 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
                        fogRed = fogRed * (1.0F - f7) + fogRed * f9 * f7;
                        fogGreen = fogGreen * (1.0F - f7) + fogGreen * f9 * f7;
                        fogBlue = fogBlue * (1.0F - f7) + fogBlue * f9 * f7;
                    }

                    Vector3f fogColor = getFogColor(pCamera, pPartialTicks, pLevel, pRenderDistanceChunks, pBossColorModifier, fogRed, fogGreen, fogBlue, tile.renderedFluid);

                    fogRed = fogColor.x();
                    fogGreen = fogColor.y();
                    fogBlue = fogColor.z();

                    RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
                    ci.cancel();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
    private static void setupFog(Camera pCamera, FogRenderer.FogMode pFogMode, float pFarPlaneDistance, boolean p_234176_, float p_234177_, CallbackInfo ci) {

        BlockPos cameraPos = pCamera.getBlockPosition();
        BlockGetter level = pCamera.level;

        BlockEntity be = level.getBlockEntity(cameraPos);
        if(be instanceof MixingCauldronTile tile){
            double d0 = pCamera.getPosition().y() - (double) 0.11111111F;
            BlockPos blockpos = new BlockPos(pCamera.getPosition().x(), d0, pCamera.getPosition().z());
            if (tile.renderedFluid != null) {
                double d1 = (float) blockpos.getY() + tile.renderedFluid.getAmount() / 2000f;
                if (d1 > d0) {
                    FogType fogtype = getFluidInCamera(tile.renderedFluid);
                    Entity entity = pCamera.getEntity();
                    HexereiUtil.FogData fogrenderer$fogdata = new HexereiUtil.FogData(pFogMode);

                    fogrenderer$fogdata.start = -8.0F;
                    fogrenderer$fogdata.end = 96.0F;
                    if (entity instanceof LocalPlayer localplayer) {
                        fogrenderer$fogdata.end *= Math.max(0.25F, getWaterVision(localplayer));
                        Holder<Biome> holder = localplayer.level.getBiome(localplayer.blockPosition());
                        if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                            fogrenderer$fogdata.end *= 0.85F;
                        }
                    }

                    if (fogrenderer$fogdata.end > pFarPlaneDistance) {
                        fogrenderer$fogdata.end = pFarPlaneDistance;
                        fogrenderer$fogdata.shape = FogShape.CYLINDER;
                    }


                    RenderSystem.setShaderFogStart(fogrenderer$fogdata.start);
                    RenderSystem.setShaderFogEnd(fogrenderer$fogdata.end);
                    RenderSystem.setShaderFogShape(fogrenderer$fogdata.shape);
                    onFogRender(pFogMode, fogtype, pCamera, p_234177_, pFarPlaneDistance, fogrenderer$fogdata.start, fogrenderer$fogdata.end, fogrenderer$fogdata.shape, tile.renderedFluid.getFluid());
                    ci.cancel();
                }
            }
        }


    }



    @OnlyIn(Dist.CLIENT)
    private static float getWaterVision(LocalPlayer localPlayer) {
        float f = 600.0F;
        float f1 = 100.0F;
        if ((float)localPlayer.waterVisionTime >= 600.0F) {
            return 1.0F;
        } else {
            float f2 = Mth.clamp((float)localPlayer.waterVisionTime / 100.0F, 0.0F, 1.0F);
            float f3 = (float)localPlayer.waterVisionTime < 100.0F ? 0.0F : Mth.clamp(((float)localPlayer.waterVisionTime - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
        }
    }

    private static FogType getFluidInCamera(FluidStack fluidStack) {


        if (fluidStack.getFluid().is(FluidTags.WATER)) {
            return FogType.WATER;
        } else if (fluidStack.getFluid().is(FluidTags.LAVA)) {
            return FogType.LAVA;
        }
        return FogType.NONE;
    }

    private static Vector3f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue, FluidStack fluid)
    {
        // Modify fog color depending on the fluid
        Vector3f in = new Vector3f(fogRed, fogGreen, fogBlue);
        Vector3f fluidFogColor = IClientFluidTypeExtensions.of(fluid.getFluid()).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, in);

        if(fluidFogColor.equals(in) && ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).getPath().equals("potion")){

            float[] f = HexereiUtil.rgbIntToFloatArray(PotionFluidType.getTintColor(fluid));
            fluidFogColor = new Vector3f(f[0],f[1],f[2]);
        }
        ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(camera, partialTick, fluidFogColor.x(), fluidFogColor.y(), fluidFogColor.z());
        MinecraftForge.EVENT_BUS.post(event);

        fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
        return fluidFogColor;
    }

    private static void onFogRender(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape, Fluid fluid)
    {
        // Modify fog rendering depending on the fluid
        IClientFluidTypeExtensions.of(fluid).modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);

        ViewportEvent.RenderFog event = new ViewportEvent.RenderFog(mode, type, camera, partialTick, nearDistance, farDistance, shape);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            RenderSystem.setShaderFogStart(event.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd(event.getFarPlaneDistance());
            RenderSystem.setShaderFogShape(event.getFogShape());
        }
    }

}
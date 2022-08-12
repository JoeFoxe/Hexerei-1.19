package net.joefoxe.hexerei.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModFluidTypes {

    public static final ResourceLocation QUICKSILVER_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_still");
    public static final ResourceLocation QUICKSILVER_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_flow");
    public static final ResourceLocation QUICKSILVER_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/quicksilver_overlay");
    public static final ResourceLocation BLOOD_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_still");
    public static final ResourceLocation BLOOD_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_flow");
    public static final ResourceLocation BLOOD_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/blood_overlay");
    public static final ResourceLocation TALLOW_STILL_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_still");
    public static final ResourceLocation TALLOW_FLOWING_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_flow");
    public static final ResourceLocation TALLOW_OVERLAY_RL = new ResourceLocation(Hexerei.MOD_ID + ":block/tallow_overlay");


    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Hexerei.MOD_ID);


    public static final RegistryObject<FluidType> QUICKSILVER_FLUID_TYPE = FLUID_TYPES.register("quicksilver_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(15).viscosity(5).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.BUCKET_FILL_LAVA))
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation STILL = QUICKSILVER_STILL_RL,
                                FLOW = QUICKSILVER_FLOWING_RL,
                                OVERLAY = QUICKSILVER_OVERLAY_RL,
                                VIEW_OVERLAY = new ResourceLocation("textures/block/red_sand.png");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return FLOW;
                        }

                        @Override
                        public ResourceLocation getOverlayTexture()
                        {
                            return OVERLAY;
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                        {
                            return VIEW_OVERLAY;
                        }

                        @Override
                        public int getTintColor()
                        {
                            return 0xF9FFFFFF;
                        }

                        @Override
                        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                        {
                            int color = this.getTintColor();
                            return new Vector3f(80f / 256f, 80f / 255f, 80f / 255f);
                        }

                        @Override
                        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                        {
                            nearDistance = -8F;
                            farDistance = 3F;

                            if (farDistance > renderDistance)
                            {
                                farDistance = renderDistance;
                                shape = FogShape.CYLINDER;
                            }

                            RenderSystem.setShaderFogStart(nearDistance);
                            RenderSystem.setShaderFogEnd(farDistance);
                            RenderSystem.setShaderFogShape(shape);
                        }
                    });
                }
            });



    public static final RegistryObject<FluidType> BLOOD_FLUID_TYPE = FLUID_TYPES.register("blood_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.HONEY_DRINK))
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation STILL = BLOOD_STILL_RL,
                                FLOW = BLOOD_FLOWING_RL,
                                OVERLAY = BLOOD_OVERLAY_RL,
                                VIEW_OVERLAY = new ResourceLocation("textures/block/nether_wart_block.png");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return FLOW;
                        }

                        @Override
                        public ResourceLocation getOverlayTexture()
                        {
                            return OVERLAY;
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                        {
                            return VIEW_OVERLAY;
                        }

                        @Override
                        public int getTintColor()
                        {
                            return 0xF9FFFFFF;
                        }

                        @Override
                        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                        {
                            int color = this.getTintColor();
                            return new Vector3f(48f / 256f, 4f / 255f, 4f / 255f);
                        }

                        @Override
                        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                        {
                            nearDistance = -8F;
                            farDistance = 4F;

                            if (farDistance > renderDistance)
                            {
                                farDistance = renderDistance;
                                shape = FogShape.CYLINDER;
                            }

                            RenderSystem.setShaderFogStart(nearDistance);
                            RenderSystem.setShaderFogEnd(farDistance);
                            RenderSystem.setShaderFogShape(shape);
                        }
                    });
                }
            });


    public static final RegistryObject<FluidType> TALLOW_FLUID_TYPE = FLUID_TYPES.register("tallow_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true).lightLevel(0).density(1500).viscosity(2000).sound(SoundAction.get("bucket_fill"),
                    SoundEvents.HONEY_DRINK))
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation STILL = TALLOW_STILL_RL,
                                FLOW = TALLOW_FLOWING_RL,
                                OVERLAY = TALLOW_OVERLAY_RL,
                                VIEW_OVERLAY = new ResourceLocation("textures/block/sand.png");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return FLOW;
                        }

                        @Override
                        public ResourceLocation getOverlayTexture()
                        {
                            return OVERLAY;
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                        {
                            return VIEW_OVERLAY;
                        }

                        @Override
                        public int getTintColor()
                        {
                            return 0xF9FFFFFF;
                        }

                        @Override
                        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                        {
                            int color = this.getTintColor();
                            return new Vector3f(153f / 256f, 153f / 255f, 114f / 255f);
                        }

                        @Override
                        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                        {
                            nearDistance = -8F;
                            farDistance = 3F;

                            if (farDistance > renderDistance)
                            {
                                farDistance = renderDistance;
                                shape = FogShape.CYLINDER;
                            }

                            RenderSystem.setShaderFogStart(nearDistance);
                            RenderSystem.setShaderFogEnd(farDistance);
                            RenderSystem.setShaderFogShape(shape);
                        }
                    });
                }
            });

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}

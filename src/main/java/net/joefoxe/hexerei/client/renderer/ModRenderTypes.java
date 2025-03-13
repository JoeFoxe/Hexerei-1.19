package net.joefoxe.hexerei.client.renderer;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;


public class ModRenderTypes extends RenderType {
    public ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable pre, Runnable post) {
        super(name, format, drawMode, bufferSize, useDelegate, needsSorting, pre, post);
    }

    private static final LineStateShard THICK_LINE = new LineStateShard(OptionalDouble.of(10.0));

    public static final RenderType BLOCK_HIGHLIGHT_FACE = create("block_highlight",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    );

    public static RenderType hueSlider(ResourceLocation location) {
        return HUE_SLIDER.apply(location, true);
    }

    public static final BiFunction<ResourceLocation, Boolean, RenderType> HUE_SLIDER = Util.memoize(
            (location, bool) -> {
                RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                        .setShaderState(new ShaderStateShard(() -> ClientEvents.hueSliderShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(bool);
                return create("hue_slider", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, rendertype$compositestate);
            }
    );

    public static RenderType slider(ResourceLocation location) {
        return SLIDER.apply(location, true);
    }

    public static final BiFunction<ResourceLocation, Boolean, RenderType> SLIDER = Util.memoize(
            (location, bool) -> {
                RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                        .setShaderState(new ShaderStateShard(() -> ClientEvents.sliderShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(bool);
                return create("slider", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, rendertype$compositestate);
            }
    );

    public static RenderType bookTranslucent(ResourceLocation location) {
        return BOOK_TRANSLUCENT.apply(location, true);
    }

    public static final BiFunction<ResourceLocation, Boolean, RenderType> BOOK_TRANSLUCENT = Util.memoize(
            (location, bool) -> {
                RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                        .setShaderState(new ShaderStateShard(() -> ClientEvents.bookTranslucentShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(bool);
                return create("book_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, rendertype$compositestate);
            }
    );

    public static final RenderType MOON_PHASE = RenderType.create(
            "moon_phase",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                    .setTextureState(new TextureStateShard(HexereiUtil.getResource("textures/gui/moon_phases.png"), false, false))
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTransparencyState(new TransparencyStateShard("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .createCompositeState(false)
    );

    public static final RenderType BLOCK_HILIGHT_LINE = create("block_hilight_line",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, false,
            RenderType.CompositeState.builder().setLineState(THICK_LINE)
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    );


    private static final RenderType FLUID = RenderType.create("hexerei:fluid",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));

    public static RenderType getFluid() {
        return FLUID;
    }


    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize((p_286156_, p_286157_) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_286156_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_286157_);
        return create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    });

    public static RenderType createGenericRenderType(String name, VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader, TransparencyStateShard transparency, ResourceLocation texture) {
        RenderType type = RenderType.create(
                Hexerei.MOD_ID + ":" + name, format, mode, 256, false, false, RenderType.CompositeState.builder()
                        .setShaderState(shader)
                        .setWriteMaskState(new WriteMaskStateShard(true, true))
                        .setLightmapState(new LightmapStateShard(false))
                        .setTransparencyState(transparency)
                        .setTextureState(new TextureStateShard(texture, false, false))
                        .setCullState(new CullStateShard(true))
                        .createCompositeState(true)
        );
        return type;
    }
//    new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false)

    public static final Function<ResourceLocation, RenderType> SOLID_TEXTURE = (texture) -> createGenericRenderType("solid_texture", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER, StateShards.ADDITIVE_TRANSPARENCY, texture);

    public static class StateShards extends RenderStateShard {

        public StateShards(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
            super(p_110161_, p_110162_, p_110163_);
        }

        public static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("additive_transparency", () -> {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
        });

        public static final TransparencyStateShard NORMAL_TRANSPARENCY = new TransparencyStateShard("normal_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });

    }

}
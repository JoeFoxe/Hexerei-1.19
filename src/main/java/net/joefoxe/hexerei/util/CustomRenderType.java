package net.joefoxe.hexerei.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class CustomRenderType extends RenderType {

    // Dummy
    public CustomRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }


    public static final RenderType TRANSPARENCY_FIX2 = create("transparent_fix", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder()
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    private static CompositeState addState(ShaderStateShard shard) {
        return CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(shard)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(NO_TRANSPARENCY)
                .setOverlayState(OverlayStateShard.OVERLAY)
                .createCompositeState(true);
    }


    public static final Function<ResourceLocation, RenderType> TRANSPARENCY_FIX = Util.memoize((p_173200_) -> {
        return create("transparent_fix", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, true, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setTextureState(new TextureStateShard(p_173200_, true, true))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(true));
    });

    public static final RenderType ADD = create("cutout",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
            2097152, true, true,
            addState(RENDERTYPE_CUTOUT_SHADER));

}

package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class PaintData {
    private final List<LayerData> layers;
    private final int width;
    private final int height;
    public ResourceLocation page;
    public UUID uuid;
    public boolean locked = false;
    public UUID lockedByUUID = new UUID(0, 0);
    public Component lockedByName = Component.empty();

    public PaintData(int width, int height, List<LayerData> layers, ResourceLocation page, UUID uuid) {
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.page = page;
        this.uuid = uuid;
    }

    public PaintData(int width, int height, List<LayerData> layers, ResourceLocation page, UUID uuid, boolean locked, UUID lockedByUUID, Component lockedByName) {
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.page = page;
        this.uuid = uuid;
        this.locked = locked;
        this.lockedByUUID = lockedByUUID;
        this.lockedByName = lockedByName;
    }

    public static final Codec<PaintData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("width").forGetter(PaintData::getWidth),
            Codec.INT.fieldOf("height").forGetter(PaintData::getHeight),
            LayerData.CODEC.listOf().fieldOf("layers").forGetter(PaintData::getLayers),
            ResourceLocation.CODEC.fieldOf("page").forGetter(PaintData::getPage),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(PaintData::getUuid),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(PaintData::isLocked),
            UUIDUtil.CODEC.optionalFieldOf("lockedByUUID", new UUID(0, 0)).forGetter(PaintData::getLockedByUUID),
            ComponentSerialization.CODEC.optionalFieldOf("lockedByName", Component.empty()).forGetter(PaintData::getLockedByName)
    ).apply(instance, PaintData::new));

    public static StreamCodec<ByteBuf, PaintData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    // Getters and utility methods
    public List<LayerData> getLayers() { return layers; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public ResourceLocation getPage() {
        return page;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isLocked() {
        return locked;
    }

    public UUID getLockedByUUID() {
        return lockedByUUID;
    }

    public Component getLockedByName() {
        return lockedByName;
    }

    public record LayerData(
            int width,
            int height,
            List<Integer> pixels, // ARGB format (0xAARRGGBB)
            float opacity,
            String blendMode, // BlendMode enum name (e.g., "NORMAL")
            String name
    ) {
        public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("width").forGetter(LayerData::width),
                Codec.INT.fieldOf("height").forGetter(LayerData::height),
                Codec.INT.listOf().fieldOf("pixels").forGetter(LayerData::pixels),
                Codec.FLOAT.fieldOf("opacity").forGetter(LayerData::opacity),
                Codec.STRING.fieldOf("blendMode").forGetter(LayerData::blendMode),
                Codec.STRING.fieldOf("name").forGetter(LayerData::name)
        ).apply(instance, LayerData::new));
    }

}

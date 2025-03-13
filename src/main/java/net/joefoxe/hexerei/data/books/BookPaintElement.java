package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.util.thread.EffectiveSide;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BookPaintElement {
    public float x;
    public float y;
    public float z;
    public float width;
    public float height;
    public float scale;
    public int index;

    public ResourceLocation parentLocation;

    public Client client;



    BookPaintElement(ResourceLocation parentLocation, float x, float y, float z, float width, float height, float scale, int index) {
        this.parentLocation = parentLocation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.index = index;
        this.client = EffectiveSide.get().isClient() ? new Client(this) : null;

    }

    public static final Codec<BookPaintElement> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("parentLocation", HexereiUtil.getResource("missing")).forGetter(p -> p.parentLocation),
                    Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(p -> p.x),
                    Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(p -> p.y),
                    Codec.FLOAT.optionalFieldOf("z", 0f).forGetter(p -> p.z),
                    Codec.FLOAT.optionalFieldOf("width", 16f).forGetter(p -> p.width),
                    Codec.FLOAT.optionalFieldOf("height", 16f).forGetter(p -> p.height),
                    Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(p -> p.scale),
                    Codec.INT.optionalFieldOf("index", 0).forGetter(p -> p.index)
                ).apply(instance, BookPaintElement::new));

    public static class Client {
        BookPaintElement parent;
        Map<UUID, PaintSystem> paintSystems;

        public PaintSystem getPaintSystem(UUID uuid) {
            if (!paintSystems.containsKey(uuid)) {
                PaintSystem ps = new PaintSystem((int) parent.width, (int) parent.height, parent.parentLocation, uuid);
                paintSystems.put(uuid, ps);
                ps.addAndUpdateTexture();

            }
            return paintSystems.get(uuid);
        }

        public Client(BookPaintElement parent) {
            this.parent = parent;
            paintSystems = new HashMap<>();

//            paintSystem.rebuildComposite();
//
//            DynamicTexture dynamicTexture = new DynamicTexture(PaintSystem.convertToNativeImage(paintSystem.compositeImage));
//            Minecraft.getInstance().getTextureManager().register(imageLocation, dynamicTexture);
        }
    }

}

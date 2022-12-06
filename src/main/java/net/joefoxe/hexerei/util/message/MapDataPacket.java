package net.joefoxe.hexerei.util.message;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class MapDataPacket {

    private final int mapId;
    private final byte scale;
    private final boolean locked;
    @Nullable
    private final List<MapDecoration> decorations;
    @Nullable
    private final MapItemSavedData.MapPatch colorPatch;


    public MapDataPacket(int pMapId, byte pScale, boolean pLocked, @Nullable Collection<MapDecoration> pDecorations, @Nullable MapItemSavedData.MapPatch pColorPatch) {
        this.mapId = pMapId;
        this.scale = pScale;
        this.locked = pLocked;
        this.decorations = pDecorations != null ? Lists.newArrayList(pDecorations) : null;
        this.colorPatch = pColorPatch;
    }
    public MapDataPacket(FriendlyByteBuf pBuffer) {
        this.mapId = pBuffer.readVarInt();
        this.scale = pBuffer.readByte();
        this.locked = pBuffer.readBoolean();
        this.decorations = pBuffer.readNullable((p_237731_) -> {
            return p_237731_.readList((p_178981_) -> {
                MapDecoration.Type mapdecoration$type = p_178981_.readEnum(MapDecoration.Type.class);
                byte b0 = p_178981_.readByte();
                byte b1 = p_178981_.readByte();
                byte b2 = (byte)(p_178981_.readByte() & 15);
                Component component = p_178981_.readNullable(FriendlyByteBuf::readComponent);
                return new MapDecoration(mapdecoration$type, b0, b1, b2, component);
            });
        });
        int i = pBuffer.readUnsignedByte();
        if (i > 0) {
            int j = pBuffer.readUnsignedByte();
            int k = pBuffer.readUnsignedByte();
            int l = pBuffer.readUnsignedByte();
            byte[] abyte = pBuffer.readByteArray();
            this.colorPatch = new MapItemSavedData.MapPatch(k, l, i, j, abyte);
        } else {
            this.colorPatch = null;
        }

    }

    public static void encode(MapDataPacket object, FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(object.mapId);
        pBuffer.writeByte(object.scale);
        pBuffer.writeBoolean(object.locked);
        pBuffer.writeNullable(object.decorations, (p_237728_, p_237729_) -> {
            p_237728_.writeCollection(p_237729_, (p_237725_, p_237726_) -> {
                p_237725_.writeEnum(p_237726_.getType());
                p_237725_.writeByte(p_237726_.getX());
                p_237725_.writeByte(p_237726_.getY());
                p_237725_.writeByte(p_237726_.getRot() & 15);
                p_237725_.writeNullable(p_237726_.getName(), FriendlyByteBuf::writeComponent);
            });
        });
        if (object.colorPatch != null) {
            pBuffer.writeByte(object.colorPatch.width);
            pBuffer.writeByte(object.colorPatch.height);
            pBuffer.writeByte(object.colorPatch.startX);
            pBuffer.writeByte(object.colorPatch.startY);
            pBuffer.writeByteArray(object.colorPatch.mapColors);
        } else {
            pBuffer.writeByte(0);
        }

    }

    public static MapDataPacket decode(FriendlyByteBuf buffer) {
        return new MapDataPacket(buffer);
    }

    public static void consume(MapDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }
            Minecraft minecraft = Minecraft.getInstance();
            MapRenderer maprenderer = minecraft.gameRenderer.getMapRenderer();
            int i = packet.getMapId();
            String s = MapItem.makeKey(i);
            MapItemSavedData mapitemsaveddata = minecraft.level.getMapData(s);
            if (mapitemsaveddata == null) {
                mapitemsaveddata = MapItemSavedData.createForClient(packet.getScale(), packet.isLocked(), minecraft.level.dimension());
                minecraft.level.setMapData(s, mapitemsaveddata);
            }

            packet.applyToMap(mapitemsaveddata);
            maprenderer.update(i, mapitemsaveddata);
        });
        ctx.get().setPacketHandled(true);
    }

    public int getMapId() {
        return this.mapId;
    }

    /**
     * Sets new MapData from the packet to given MapData param
     */
    public void applyToMap(MapItemSavedData pMapdata) {
        if (this.decorations != null) {
            pMapdata.addClientSideDecorations(this.decorations);
        }

        if (this.colorPatch != null) {
            this.colorPatch.applyToMap(pMapdata);
        }

    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }



}
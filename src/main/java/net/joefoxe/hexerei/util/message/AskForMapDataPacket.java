package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class AskForMapDataPacket {

    ItemStack stack;

    public AskForMapDataPacket(ItemStack stack) {
        this.stack = stack;
    }
    public AskForMapDataPacket(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public static void encode(AskForMapDataPacket object, FriendlyByteBuf buffer) {
        buffer.writeItem(object.stack);
    }

    public static AskForMapDataPacket decode(FriendlyByteBuf buffer) {
        return new AskForMapDataPacket(buffer);
    }

    public static void consume(AskForMapDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }
            HoldingPlayer holdingPlayer = HoldingPlayer.create(ctx.get().getSender());
            MapItemSavedData.MapPatch mapitemsaveddata$mappatch;
            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(packet.stack, world);
            if(mapitemsaveddata != null){
                Integer integer = MapItem.getMapId(packet.stack);
                int id = integer == null ? 0 : integer;
                mapitemsaveddata$mappatch = holdingPlayer.createPatch(mapitemsaveddata);
                Collection<MapDecoration> collection = new ArrayList<>();
                mapitemsaveddata.getDecorations().forEach(collection::add);
                MapDataPacket mapDataPacket = new MapDataPacket(id, mapitemsaveddata.scale, mapitemsaveddata.locked, collection, mapitemsaveddata$mappatch);
                HexereiPacketHandler.instance.send(PacketDistributor.ALL.noArg(), mapDataPacket);
            }
        });
        ctx.get().setPacketHandled(true);
    }


    public static class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        /** The lowest dirty x value */
        private int minDirtyX;
        /** The lowest dirty z value */
        private int minDirtyY;
        /** The highest dirty x value */
        private int maxDirtyX = 127;
        /** The highest dirty z value */
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        HoldingPlayer(Player pPlayer) {
            this.player = pPlayer;
        }

        public static HoldingPlayer create(Player pPlayer){
            return new HoldingPlayer(pPlayer);
        }

        private MapItemSavedData.MapPatch createPatch(MapItemSavedData mapItemSavedData) {
            int i = this.minDirtyX;
            int j = this.minDirtyY;
            int k = this.maxDirtyX + 1 - this.minDirtyX;
            int l = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] abyte = new byte[k * l];

            for(int i1 = 0; i1 < k; ++i1) {
                for(int j1 = 0; j1 < l; ++j1) {
                    abyte[i1 + j1 * k] = mapItemSavedData.colors[i + i1 + (j + j1) * 128];
                }
            }

            return new MapItemSavedData.MapPatch(i, j, k, l, abyte);
        }


        void markColorsDirty(int pX, int pZ) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, pX);
                this.minDirtyY = Math.min(this.minDirtyY, pZ);
                this.maxDirtyX = Math.max(this.maxDirtyX, pX);
                this.maxDirtyY = Math.max(this.maxDirtyY, pZ);
            } else {
                this.dirtyData = true;
                this.minDirtyX = pX;
                this.minDirtyY = pZ;
                this.maxDirtyX = pX;
                this.maxDirtyY = pZ;
            }

        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    public static class MapPatch {
        public final int startX;
        public final int startY;
        public final int width;
        public final int height;
        public final byte[] mapColors;

        public MapPatch(int pStartX, int pStartY, int pWidth, int pHeight, byte[] pMapColors) {
            this.startX = pStartX;
            this.startY = pStartY;
            this.width = pWidth;
            this.height = pHeight;
            this.mapColors = pMapColors;
        }

        public void applyToMap(MapItemSavedData pSavedData) {
            for(int i = 0; i < this.width; ++i) {
                for(int j = 0; j < this.height; ++j) {
                    pSavedData.setColor(this.startX + i, this.startY + j, this.mapColors[i + j * this.width]);
                }
            }

        }
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CrowWhitelistSyncToServer {
    int sourceId;
    List<ResourceLocation> whitelist;

    public CrowWhitelistSyncToServer(Entity entity, List<Block> whitelist) {
        List<ResourceLocation> list = new ArrayList<>();
        this.sourceId = entity.getId();
        for (Block block : whitelist) {
            list.add(ForgeRegistries.BLOCKS.getKey(block));
        }
        this.whitelist = list;
    }
    public CrowWhitelistSyncToServer(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        List<ResourceLocation> list = new ArrayList<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            list.add(buf.readResourceLocation());
        }
        this.whitelist = list;
    }

    public static void encode(CrowWhitelistSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.whitelist.size());
        for(int i = 0; i < object.whitelist.size(); i++){
            buffer.writeResourceLocation(object.whitelist.get(i));
        }
    }

    public static CrowWhitelistSyncToServer decode(FriendlyByteBuf buffer) {
        return new CrowWhitelistSyncToServer(buffer);
    }

    public static void consume(CrowWhitelistSyncToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            if(world.getEntity(packet.sourceId) instanceof CrowEntity crowEntity) {
                List<Block> blockList = new ArrayList<>();
                for(int i = 0; i < packet.whitelist.size(); i++){
                    blockList.add(ForgeRegistries.BLOCKS.getValue(packet.whitelist.get(i)));
                }
                crowEntity.harvestWhitelist = blockList;
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class RecipeToServer {
    private List<ItemStack> stacks;
    private BlockPos pos;

    private UUID uuid;

    public RecipeToServer(List<ItemStack> stacks, BlockPos pos, UUID uuid) {
        this.stacks = stacks;
        this.pos = pos;
        this.uuid = uuid;
    }
    public RecipeToServer(FriendlyByteBuf buf) {
        int num = buf.readInt();
        List<ItemStack> stacks1 = new ArrayList<>();
        for(int i = 0; i < num; i++)
            stacks1.add(buf.readItem());
        this.stacks = stacks1;
        if (buf.readBoolean()) {
            this.pos = buf.readBlockPos();
        } else {
            this.pos = null;
        }
        this.uuid = buf.readUUID();
    }

    public static void encode(RecipeToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.stacks.size());
        for(int i = 0; i < object.stacks.size(); i++)
            buffer.writeItem(object.stacks.get(i));
        if (object.pos != null) {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(object.pos);
        } else {
            buffer.writeBoolean(false);
        }
        buffer.writeUUID(object.uuid);
    }

    public static RecipeToServer decode(FriendlyByteBuf buffer) {
        return new RecipeToServer(buffer);
    }

    public static void consume(RecipeToServer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            ((MixingCauldronTile)world.getBlockEntity(packet.pos)).setContents(packet.stacks, world.getPlayerByUUID(packet.uuid));
        });
        ctx.get().setPacketHandled(true);
    }
}
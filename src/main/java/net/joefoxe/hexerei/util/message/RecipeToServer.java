package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeToServer> CODEC  = StreamCodec.ofMember(RecipeToServer::encode, RecipeToServer::new);
    public static final Type<RecipeToServer> TYPE = new Type<>(HexereiUtil.getResource("recipe_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private List<ItemStack> stacks;
    private BlockPos pos;

    private UUID uuid;

    public RecipeToServer(List<ItemStack> stacks, BlockPos pos, UUID uuid) {
        this.stacks = stacks;
        this.pos = pos;
        this.uuid = uuid;
    }
    public RecipeToServer(RegistryFriendlyByteBuf buf) {
        int num = buf.readInt();
        List<ItemStack> stacks1 = new ArrayList<>();
        for(int i = 0; i < num; i++)
            stacks1.add(ItemStack.STREAM_CODEC.decode(buf));
        this.stacks = stacks1;
        if (buf.readBoolean()) {
            this.pos = buf.readBlockPos();
        } else {
            this.pos = null;
        }
        this.uuid = buf.readUUID();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        List<ItemStack> non_empty = stacks.stream().filter((stack -> !stack.isEmpty())).toList();
        buffer.writeInt(non_empty.size());
        for (ItemStack stack : non_empty)
            ItemStack.STREAM_CODEC.encode(buffer, stack);
        if (pos != null) {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(pos);
        } else {
            buffer.writeBoolean(false);
        }
        buffer.writeUUID(uuid);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getBlockEntity(pos) instanceof MixingCauldronTile cauldron)
            cauldron.setContents(stacks, player.level().getPlayerByUUID(uuid));
    }
}
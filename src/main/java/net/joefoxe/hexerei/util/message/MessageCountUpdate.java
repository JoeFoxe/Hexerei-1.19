package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MessageCountUpdate extends AbstractPacket
{

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCountUpdate> CODEC  = StreamCodec.ofMember(MessageCountUpdate::encode, MessageCountUpdate::new);
    public static final Type<MessageCountUpdate> TYPE = new Type<>(HexereiUtil.getResource("message_count_update"));

    private int x;
    private int y;
    private int z;
    private int slot;
    private int count;
    private boolean failed;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageCountUpdate(BlockPos pos, int slot, int count) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.slot = slot;
        this.count = count;
        this.failed = false;
    }

    public MessageCountUpdate(RegistryFriendlyByteBuf buf) {
        this(new BlockPos(buf.readInt(), buf.readShort(), buf.readInt()), buf.readByte(), buf.readInt());
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(slot);
        buf.writeInt(count);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (!failed) {
            if (minecraft.level != null) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockEntity tileEntity = minecraft.level.getBlockEntity(pos);
                if (tileEntity instanceof HerbJarTile) {
                    ((HerbJarTile) tileEntity).clientUpdateCount(slot, count);
                }
            }
        }
    }
}
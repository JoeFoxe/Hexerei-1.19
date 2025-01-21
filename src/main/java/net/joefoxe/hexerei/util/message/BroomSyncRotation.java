package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BroomSyncRotation extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncRotation> CODEC  = StreamCodec.ofMember(BroomSyncRotation::encode, BroomSyncRotation::new);
    public static final CustomPacketPayload.Type<BroomSyncRotation> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync_rot"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    float rotation;

    public BroomSyncRotation(Entity entity, float rot) {
        this.sourceId = entity.getId();
        this.rotation = rot;
    }
    public BroomSyncRotation(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.rotation = buf.readFloat();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeFloat(rotation);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(minecraft.level.getEntity(sourceId) instanceof BroomEntity broom) {
            if(!broom.isControlledByLocalInstance())
                broom.setRotation(rotation);
        }
    }
}
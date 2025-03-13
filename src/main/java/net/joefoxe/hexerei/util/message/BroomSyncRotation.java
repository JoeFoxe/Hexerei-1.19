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
import net.minecraft.world.phys.Vec3;

public class BroomSyncRotation extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncRotation> CODEC  = StreamCodec.ofMember(BroomSyncRotation::encode, BroomSyncRotation::new);
    public static final CustomPacketPayload.Type<BroomSyncRotation> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync_rot"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    float deltaRotation;
    Vec3 deltaMovement;

    public BroomSyncRotation(BroomEntity broom) {
        this.sourceId = broom.getId();
        this.deltaRotation = broom.deltaRotation;
        this.deltaMovement = broom.getDeltaMovement();
    }

    public BroomSyncRotation(int id, float deltaRotation, Vec3 deltaMovement) {
        this.sourceId = id;
        this.deltaRotation = deltaRotation;
        this.deltaMovement = deltaMovement;
    }

    public BroomSyncRotation(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.deltaRotation = buf.readFloat();
        this.deltaMovement = buf.readVec3();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeFloat(deltaRotation);
        buffer.writeVec3(deltaMovement);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(minecraft.level.getEntity(sourceId) instanceof BroomEntity broom) {

            if(!broom.isControlledByLocalInstance()) {
                broom.deltaRotationLerp = deltaRotation;
                broom.deltaMovementLerp = deltaMovement;
            }
        }
    }
}
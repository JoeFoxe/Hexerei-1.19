package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CrowCanAttackToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowCanAttackToServer> CODEC  = StreamCodec.ofMember(CrowCanAttackToServer::encode, CrowCanAttackToServer::new);
    public static final Type<CrowCanAttackToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_can_attack_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    boolean canAttack;

    public CrowCanAttackToServer(CrowEntity entity, boolean canAttack) {
        this.sourceId = entity.getId();
        this.canAttack = canAttack;
        entity.canAttack = canAttack;
    }
    public CrowCanAttackToServer(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.canAttack = buf.readBoolean();
    }

    public static void encode(CrowCanAttackToServer object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeBoolean(object.canAttack);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getEntity(sourceId) instanceof CrowEntity crowEntity)
            crowEntity.canAttack = canAttack;
    }
}
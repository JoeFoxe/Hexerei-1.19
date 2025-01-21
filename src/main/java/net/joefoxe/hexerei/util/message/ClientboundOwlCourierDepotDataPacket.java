package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ClientboundOwlCourierDepotDataPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOwlCourierDepotDataPacket> CODEC  = StreamCodec.ofMember(ClientboundOwlCourierDepotDataPacket::encode, ClientboundOwlCourierDepotDataPacket::new);
    public static final CustomPacketPayload.Type<ClientboundOwlCourierDepotDataPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier_depot_clientbound"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    CompoundTag tag;

    public CompoundTag getTag() {
        return tag;
    }

    public ClientboundOwlCourierDepotDataPacket(CompoundTag tag) {
        this.tag = tag;
    }
    public ClientboundOwlCourierDepotDataPacket(RegistryFriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientOwlCourierDepotData.update(this);
    }
}
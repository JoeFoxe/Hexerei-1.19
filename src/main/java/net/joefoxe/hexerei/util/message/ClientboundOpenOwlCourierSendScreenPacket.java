package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientboundOpenOwlCourierSendScreenPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenOwlCourierSendScreenPacket> CODEC  = StreamCodec.ofMember(ClientboundOpenOwlCourierSendScreenPacket::encode, ClientboundOpenOwlCourierSendScreenPacket::new);
    public static final CustomPacketPayload.Type<ClientboundOpenOwlCourierSendScreenPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier_send_screen"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int owlId;
    InteractionHand hand;
    int selected;

    public ClientboundOpenOwlCourierSendScreenPacket(int owlId, InteractionHand hand, int selected) {
        this.owlId = owlId;
        this.hand = hand;
        this.selected = selected;
    }

    public ClientboundOpenOwlCourierSendScreenPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buf.readInt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(owlId);
        buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
        buffer.writeInt(selected);
    }

    public static ClientboundOpenOwlCourierSendScreenPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOpenOwlCourierSendScreenPacket(buffer.readInt(), buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buffer.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if (player.level().getEntity(owlId) instanceof OwlEntity owl) {
            minecraft.setScreen(new net.joefoxe.hexerei.screen.OwlCourierSendScreen(owl, hand, selected));

        }
    }

}
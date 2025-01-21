package net.joefoxe.hexerei.util.message;

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

public class ClientboundOpenCourierLetterScreenPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenCourierLetterScreenPacket> CODEC  = StreamCodec.ofMember(ClientboundOpenCourierLetterScreenPacket::encode, ClientboundOpenCourierLetterScreenPacket::new);
    public static final CustomPacketPayload.Type<ClientboundOpenCourierLetterScreenPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("open_courier_letter_screen"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int slotIndex;
    InteractionHand hand;

    public ClientboundOpenCourierLetterScreenPacket(int slotIndex, InteractionHand hand) {
        this.slotIndex = slotIndex;
        this.hand = hand;
    }
    public ClientboundOpenCourierLetterScreenPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(slotIndex);
        buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static ClientboundOpenCourierLetterScreenPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOpenCourierLetterScreenPacket(buffer.readInt(), buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        minecraft.setScreen(new net.joefoxe.hexerei.screen.CourierLetterScreen(slotIndex, hand, slotIndex > 0 ? player.getInventory().getItem(slotIndex) : player.getItemInHand(InteractionHand.OFF_HAND)));
    }
}
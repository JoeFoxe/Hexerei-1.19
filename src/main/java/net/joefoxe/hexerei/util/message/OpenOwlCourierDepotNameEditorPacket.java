package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class OpenOwlCourierDepotNameEditorPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenOwlCourierDepotNameEditorPacket> CODEC  = StreamCodec.ofMember(OpenOwlCourierDepotNameEditorPacket::encode, OpenOwlCourierDepotNameEditorPacket::new);
    public static final CustomPacketPayload.Type<OpenOwlCourierDepotNameEditorPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier_depot_name"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;

    public OpenOwlCourierDepotNameEditorPacket(BlockPos pos) {
        this.pos = pos;

    }
    public OpenOwlCourierDepotNameEditorPacket(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public static void encode(OpenOwlCourierDepotNameEditorPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getBlockEntity(pos) instanceof OwlCourierDepotTile depot) {
            minecraft.setScreen(new net.joefoxe.hexerei.screen.OwlCourierDepotNameScreen(depot, Component.translatable("hexerei.owl_courier_depot_name.edit")));
        }
    }
}
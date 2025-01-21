package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CourierLetterUpdatePacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CourierLetterUpdatePacket> CODEC  = StreamCodec.ofMember(CourierLetterUpdatePacket::encode, CourierLetterUpdatePacket::new);
    public static final CustomPacketPayload.Type<CourierLetterUpdatePacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("courier_letter_update"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    final CompoundTag lines;
    int slotIndex;
    boolean sealed;

    public CourierLetterUpdatePacket(int slotIndex, CompoundTag lines, boolean sealed) {
        this.slotIndex = slotIndex;
        this.lines = lines;
        this.sealed = sealed;

    }
    public CourierLetterUpdatePacket(RegistryFriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
        this.lines = buf.readNbt();
        this.sealed = buf.readBoolean();
    }

    public static void encode(CourierLetterUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.slotIndex);
        buffer.writeNbt(object.lines);
        buffer.writeBoolean(object.sealed);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player != null){
            if (Inventory.isHotbarSlot(slotIndex)) {
                if (slotIndex == player.getInventory().selected) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {

                        CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
                        tag.put("Message", lines);
                        if (sealed) {
                            tag.putBoolean("Sealed", true);
//                            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
                        }
                        BlockItem.setBlockEntityData(stack, ModTileEntities.COURIER_LETTER_TILE.get(), tag);
                    }

                }
            } else {
                //offhand
                ItemStack stack = player.getOffhandItem();
                if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {

                    CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
                    tag.put("Message", lines);
                    if (sealed) {
                        tag.putBoolean("Sealed", true);
                    }
                    BlockItem.setBlockEntityData(stack, ModTileEntities.COURIER_LETTER_TILE.get(), tag);
                }
            }
        }
    }

}
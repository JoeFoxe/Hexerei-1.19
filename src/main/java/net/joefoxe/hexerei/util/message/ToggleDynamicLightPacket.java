package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleDynamicLightPacket {
    boolean enabled;

    public ToggleDynamicLightPacket(boolean enabled) {
        this.enabled = enabled;
    }
    public ToggleDynamicLightPacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    public static void encode(ToggleDynamicLightPacket object, FriendlyByteBuf buffer) {
        buffer.writeBoolean(object.enabled);
    }

    public static ToggleDynamicLightPacket decode(FriendlyByteBuf buffer) {
        return new ToggleDynamicLightPacket(buffer);
    }

    public static void consume(ToggleDynamicLightPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level;
            }

            LightManager.toggleLightsAndConfig(packet.enabled);
        });
        ctx.get().setPacketHandled(true);
    }
}
package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class HexereiClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    @Shadow public abstract ClientLevel getLevel();

    protected HexereiClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(at = @At("HEAD"), method = "handleSetEntityPassengersPacket", cancellable = true)
    private void passengersPacket(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener) (Object) this, this.minecraft);
        Entity entity = this.getLevel().getEntity(packet.getVehicle());

        if (entity instanceof BroomEntity broom && this.minecraft.player != null) {
            boolean flag = broom.hasIndirectPassenger(this.minecraft.player);
            broom.ejectPassengers();

            for(int i : packet.getPassengers()) {
                Entity entity1 = this.getLevel().getEntity(i);
                if (entity1 != null) {
                    entity1.startRiding(broom, true);
                    if (entity1 == this.minecraft.player && !flag) {

                        Component component = Component.translatable("mount.onboard", ModKeyBindings.broomDismount.getTranslatedKeyMessage());
                        this.minecraft.gui.setOverlayMessage(component, false);
                        this.minecraft.getNarrator().sayNow(component);
                    }
                }
            }

            ci.cancel();
        }
    }
}

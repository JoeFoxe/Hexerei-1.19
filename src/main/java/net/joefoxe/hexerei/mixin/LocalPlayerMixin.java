package net.joefoxe.hexerei.mixin;

import com.mojang.authlib.GameProfile;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Redirect(method = "tick", at = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ServerboundPlayerInputPacket"))
    public ServerboundPlayerInputPacket redirectConstructor(float xxa, float zza, boolean jumping, boolean shiftKeyDown) {
        if (this.getVehicle() instanceof BroomEntity broom) {
            shiftKeyDown = ModKeyBindings.broomDismount.isDown();
        }
        return new ServerboundPlayerInputPacket(xxa, zza, jumping, shiftKeyDown);
    }






}
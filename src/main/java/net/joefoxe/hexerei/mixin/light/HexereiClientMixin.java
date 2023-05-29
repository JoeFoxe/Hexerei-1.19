package net.joefoxe.hexerei.mixin.light;

import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class HexereiClientMixin {
    @Inject(method = "updateLevelInEngines", at = @At("HEAD"))
    private void onSetWorld(ClientLevel world, CallbackInfo ci) {
        LightManager.clearLightSources();
    }
}

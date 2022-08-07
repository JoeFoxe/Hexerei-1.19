package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.Blaze3D;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    Minecraft minecraft;
    @Shadow
    private double lastMouseEventTime;
    @Shadow
    private double accumulatedDX;
    @Shadow
    private double accumulatedDY;

    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();

    @Shadow
    private boolean isMouseGrabbed() {
        throw new IllegalStateException();
    }

    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    public void turnPlayer(CallbackInfo callback) {
        if(Hexerei.entityClicked) {

            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;

            callback.cancel();
        }
        if(this.minecraft.options.getCameraType().isFirstPerson() && Hexerei.glassesZoomKeyPressEvent != null && Hexerei.glassesZoomKeyPressEvent.zoomToggled){
            double d0 = Blaze3D.getTime();
            this.lastMouseEventTime = d0;
            if (this.isMouseGrabbed() && this.minecraft.isWindowActive() && this.minecraft.player != null && !this.minecraft.player.isScoping()) {
                double d4 = this.minecraft.options.sensitivity().get() * (double) 0.6F + (double) 0.2F;

                this.smoothTurnX.reset();
                this.smoothTurnY.reset();
                double d2 = this.accumulatedDX * d4 * d4 * (d4 * 4);
                double d3 = this.accumulatedDY * d4 * d4 * (d4 * 4);

                this.accumulatedDX = 0.0D;
                this.accumulatedDY = 0.0D;
                int i = 1;
                if (this.minecraft.options.invertYMouse().get()) {
                    i = -1;
                }

                this.minecraft.getTutorial().onMouse(d2, d3);
                if (this.minecraft.player != null) {
                    this.minecraft.player.turn(d2, d3 * (double) i);
                }

                callback.cancel();
            }

        }
    }
}
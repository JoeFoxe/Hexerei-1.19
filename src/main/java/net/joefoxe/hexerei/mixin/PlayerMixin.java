package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updatePlayerPose", at = @At(value = "RETURN"))
    public void hexerei$updatePlayerPose(CallbackInfo ci) {

        if (this.getVehicle() instanceof BroomEntity broom) {
            this.setPose(Pose.SITTING);
        }

    }






}
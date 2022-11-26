package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {


    @OnlyIn(Dist.CLIENT)
    @Inject(method = "renderScreenEffect", at = @At(value = "HEAD"), cancellable = true)
    private static void renderScreenEffect(Minecraft pMinecraft, PoseStack pPoseStack, CallbackInfo ci) {
        Player player = pMinecraft.player;
        Camera camera = pMinecraft.gameRenderer.getMainCamera();

        double d0 = camera.getPosition().y() - (double) 0.11111111F;
        BlockPos blockpos = new BlockPos(camera.getPosition().x(), d0, camera.getPosition().z());
        if(player.level.getBlockEntity(blockpos) instanceof MixingCauldronTile tile){
            if(tile.renderedFluid != null) {
                double d1 = (double) ((float) blockpos.getY() + tile.renderedFluid.getAmount() / 2000f);
                if (d1 > d0) {
                    if(!tile.renderedFluid.getFluid().isSame(Fluids.WATER))
                        IClientFluidTypeExtensions.of(tile.renderedFluid.getFluid()).renderOverlay(pMinecraft, pPoseStack);
                }
            }
        }
    }
}

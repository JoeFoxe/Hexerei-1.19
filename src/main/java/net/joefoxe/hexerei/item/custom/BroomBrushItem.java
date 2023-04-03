package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BroomBrushItem extends BroomAttachmentItem {

    public Model model = null;

    public ResourceLocation texture;
    public ResourceLocation dye_texture;
    // list of particles and their respective random delay
    public List<Tuple<ParticleOptions, Integer>> list = null;
    public BroomBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public void renderParticles(BroomEntity broom, Level level, BroomEntity.Status status, RandomSource random) {
        if (this.list != null) {
            for (Tuple<ParticleOptions, Integer> tuple : this.list) {
                ParticleOptions option = tuple.getA();
                int delay = tuple.getB();
                if (random.nextInt(delay) == 0) {
                    float rotOffset = random.nextFloat() * 10 - 5;
                    level.addParticle(option, broom.getX() - Math.sin(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + broom.getDeltaMovement().length() / 4), broom.getY() + broom.floatingOffset + 0.25f * random.nextFloat() - broom.getDeltaMovement().y(), broom.getZ() + Math.cos(((broom.getYRot() - 90f + broom.deltaRotation + rotOffset) / 180f) * (Math.PI)) * (1.25f + broom.getDeltaMovement().length() / 4), (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d, (random.nextDouble() - 0.5d) * 0.015d);
                }
            }
        }
    }

    public boolean shouldGlow(@Nullable Level level, ItemStack brushStack){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void bakeModels() {
        EntityModelSet context = Minecraft.getInstance().getEntityModels();
        this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
        this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel.png");
        this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel_dye.png");
        this.list = new ArrayList<>();
        this.list.add(new Tuple<>(ModParticleTypes.BROOM.get(), 50));
        this.list.add(new Tuple<>(ModParticleTypes.BROOM_2.get(), 20));
        this.list.add(new Tuple<>(ModParticleTypes.BROOM_3.get(), 80));
        this.list.add(new Tuple<>(ModParticleTypes.BROOM_4.get(), 500));
        this.list.add(new Tuple<>(ModParticleTypes.BROOM_5.get(), 500));
        this.list.add(new Tuple<>(ModParticleTypes.BROOM_6.get(), 500));
    }

    //override for the broom brush to have a speed modifier
    public float getSpeedModifier(){
        return 0f;
    }

}

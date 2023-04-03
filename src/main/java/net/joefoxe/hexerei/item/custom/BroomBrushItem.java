package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class BroomBrushItem extends BroomAttachmentItem {

    public Model model = null;

    public ResourceLocation texture;
    public ResourceLocation dye_texture;
    public boolean glow_on_full_moon = false;
    // list of particles and their respective random delay
    public List<Tuple<ParticleOptions, Integer>> list = null;
    public BroomBrushItem(Properties properties) {
        super(properties);
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

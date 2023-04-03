package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BroomAttachmentItem extends Item {

    public Model model = null;

    public ResourceLocation texture;
    public ResourceLocation dye_texture;
    public BroomAttachmentItem(Properties properties) {
        super(properties);
    }

    public void onBrushDamage(BroomEntity broom, RandomSource random){}

    @OnlyIn(Dist.CLIENT)
    public void renderParticles(BroomEntity broom, Level world, BroomEntity.Status status, RandomSource random){}

    public boolean shouldRenderParticles(BroomEntity broom, Level world, BroomEntity.Status status){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void bakeModels() {
        EntityModelSet context = Minecraft.getInstance().getEntityModels();
        this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
        this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel.png");
        this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/broom_satchel_dye.png");
    }

}

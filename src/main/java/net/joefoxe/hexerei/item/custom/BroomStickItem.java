package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomStickBaseModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BroomStickItem extends Item {

    public Model model = null;
    public Model outter_model = null;

    public ResourceLocation texture;
    public ResourceLocation dye_texture;
    public BroomStickItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void bakeModels() {
        EntityModelSet context = Minecraft.getInstance().getEntityModels();
        this.model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION));
        this.outter_model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
        this.texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/mahogany_broom.png");
        this.dye_texture = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/mahogany_broom.png");
    }

}

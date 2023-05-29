package net.joefoxe.hexerei.light;

import net.joefoxe.hexerei.config.HexConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class DynamicLightUtil {


    public static int getSectionCoord(double coord) {
        return getSectionCoord(Mth.floor(coord));
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }


    private static int getLuminance(Entity entity){
        if(entity.isOnFire() || entity.isCurrentlyGlowing()) {
            return 15;
        }
        if(HexConfig.ENTITY_LIGHT_MAP.containsKey(keyFor(entity)))
            return HexConfig.ENTITY_LIGHT_MAP.get(keyFor(entity));
        return Math.min(15, LightManager.getValue(entity));
    }

    public static boolean couldGiveLight(Entity entity){
        return LightManager.getLightRegistry().containsKey(entity.getType()) || HexConfig.ENTITY_LIGHT_MAP.containsKey(keyFor(entity)) || (entity instanceof Player player && getPlayerLight(player) > 0) || entity.isOnFire() || entity.isCurrentlyGlowing();
    }

    public static int getPlayerLight(Player player){
        int mainLight = HexConfig.ITEM_LIGHTMAP.getOrDefault(keyFor(player.getMainHandItem().getItem()), 0);
        int offHandLight = HexConfig.ITEM_LIGHTMAP.getOrDefault(keyFor(player.getOffhandItem().getItem()), 0);
        return Math.max(mainLight, offHandLight);
    }

    public static int lightForEntity(Entity entity){
        int light = 0;
        if(entity instanceof Player player){
            light = getPlayerLight(player);
        }
        if(entity.isOnFire() || entity.isCurrentlyGlowing()){
            return 15;
        }
        if(light < 15 && LightManager.containsEntity(entity.getType())) {
            int entityLuminance = getLuminance(entity);
            return Math.max(entityLuminance, light);
        }

        return Math.min(15, light);
    }

    public static int fromItemLike(ItemLike itemLike){
        return HexConfig.ITEM_LIGHTMAP.getOrDefault(keyFor(itemLike), 0);
    }

    public static ResourceLocation keyFor(Entity entity){
        return ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
    }

    public static ResourceLocation keyFor(ItemLike itemLike){
        return ForgeRegistries.ITEMS.getKey(itemLike.asItem());
    }

}

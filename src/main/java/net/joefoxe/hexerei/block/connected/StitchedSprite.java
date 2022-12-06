package net.joefoxe.hexerei.block.connected;


import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CREDIT: https://github.com/Jozufozu/Flywheel/tree/1.19/dev by Jozufozu & team (with edits made by JoeFoxe)
// Under MIT-License: https://github.com/Jozufozu/Flywheel/blob/1.18/dev/LICENSE.md
public class StitchedSprite {
    public static final Map<ResourceLocation, List<StitchedSprite>> ALL = new HashMap<>();

    protected final ResourceLocation atlasLocation;
    protected final ResourceLocation location;
    protected TextureAtlasSprite sprite;

    public StitchedSprite(ResourceLocation atlas, ResourceLocation location) {
        atlasLocation = atlas;
        this.location = location;
        ALL.computeIfAbsent(atlasLocation, $ -> new ArrayList<>()).add(this);
    }

    public StitchedSprite(ResourceLocation location) {
        this(InventoryMenu.BLOCK_ATLAS, location);
    }

    public void loadSprite(TextureAtlas atlas) {
        sprite = atlas.getSprite(location);
    }
    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public ResourceLocation getAtlasLocation() {
        return atlasLocation;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public TextureAtlasSprite get() {
        return sprite;
    }
}

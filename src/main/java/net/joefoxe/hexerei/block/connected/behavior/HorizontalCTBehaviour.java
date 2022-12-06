package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE

public class HorizontalCTBehaviour extends ConnectedTextureBehaviour.Base {

    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;

    public HorizontalCTBehaviour(CTSpriteShiftEntry layerShift) {
        this(layerShift, null);
    }

    public HorizontalCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
        this.layerShift = layerShift;
        this.topShift = topShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return direction.getAxis()
                .isHorizontal() ? layerShift : topShift;
    }

}
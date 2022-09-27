package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.connected.*;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class FullBlockCTBehaviour extends ConnectedTextureBehaviour.Base {

    private CTSpriteShiftEntry shift;


    public FullBlockCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        if (isBeingBlocked(state, reader, pos, otherPos, face))
            return false;
        BlockConnectivity cc = ClientProxy.BLOCK_CONNECTIVITY;
        BlockConnectivity.Entry entry = cc.get(state);
        BlockConnectivity.Entry otherEntry = cc.get(other);
        if (entry == null || otherEntry == null)
            return false;
        if (!entry.isSideValid(state, face) || !otherEntry.isSideValid(other, face))
            return false;
        if (entry.getCTSpriteShiftEntry() != otherEntry.getCTSpriteShiftEntry())
            return false;
        return true;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return shift;
    }

}
package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team with edits made by JoeFoxe
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class DyedFullBlockCTBehaviour extends ConnectedTextureBehaviour.Base {

    private CTSpriteShiftEntry shift;


    public DyedFullBlockCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        if (isBeingBlocked(state, reader, pos, otherPos, face))
            return false;
        if(state.getBlock() instanceof CTDyable waxedLayeredBlockDyed && other.getBlock() instanceof CTDyable otherLayeredBlockDyed){
            if(!waxedLayeredBlockDyed.getDyeColor().equals(otherLayeredBlockDyed.getDyeColor()))
                return false;
        }
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
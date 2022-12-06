package net.joefoxe.hexerei.block.connected;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class BlockConnectivity {

    private Map<Block, Entry> entries;

    public BlockConnectivity() {
        entries = new IdentityHashMap<>();
    }

    public Entry get(BlockState blockState) {
        return entries.get(blockState.getBlock());
    }

    public void makeBlock(Block block, CTSpriteShiftEntry casing) {
        new Entry(block, casing, (s, f) -> true).register();
    }

    public void make(Block block, CTSpriteShiftEntry casing) {
        new Entry(block, casing, (s, f) -> true).register();
    }

    public void make(Block block, CTSpriteShiftEntry casing, BiPredicate<BlockState, Direction> predicate) {
        new Entry(block, casing, predicate).register();
    }

    public class Entry {

        private Block block;
        private CTSpriteShiftEntry ctSpriteShiftEntry;
        private BiPredicate<BlockState, Direction> predicate;

        private Entry(Block block, CTSpriteShiftEntry ctSpriteShiftEntry, BiPredicate<BlockState, Direction> predicate) {
            this.block = block;
            this.ctSpriteShiftEntry = ctSpriteShiftEntry;
            this.predicate = predicate;
        }

        public CTSpriteShiftEntry getCTSpriteShiftEntry() {
            return ctSpriteShiftEntry;
        }

        public boolean isSideValid(BlockState state, Direction face) {
            return predicate.test(state, face);
        }

        public void register() {
            entries.put(block, this);
        }

    }
}
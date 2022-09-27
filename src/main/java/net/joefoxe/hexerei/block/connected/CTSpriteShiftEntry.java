package net.joefoxe.hexerei.block.connected;


// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public class CTSpriteShiftEntry extends SpriteShiftEntry {

    protected final CTType type;

    public CTSpriteShiftEntry(CTType type) {
        this.type = type;
    }

    public CTType getType() {
        return type;
    }

    public float getTargetU(float localU, int index) {
        float uOffset = (index % type.getSheetSize());
        return getTarget().getU(
                (getUnInterpolatedU(getOriginal(), localU) + (uOffset * 16)) / ((float) type.getSheetSize()));
    }

    public float getTargetV(float localV, int index) {
        float vOffset = (index / type.getSheetSize());
        return getTarget().getV(
                (getUnInterpolatedV(getOriginal(), localV) + (vOffset * 16)) / ((float) type.getSheetSize()));
    }

}
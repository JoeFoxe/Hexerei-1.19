package net.joefoxe.hexerei.block.connected;

import net.minecraft.resources.ResourceLocation;

// CREDIT: https://github.com/Creators-of-Create/Create/tree/mc1.19/dev by simibubi & team
// Under MIT-License: https://github.com/Creators-of-Create/Create/blob/mc1.19/dev/LICENSE
public interface CTType {
    ResourceLocation getId();

    int getSheetSize();

    default int getExtraFaceVariations() {
      return 0;
    }

    default float getPercent() {
      return 1;
    }

    ConnectedTextureBehaviour.ContextRequirement getContextRequirement();

    int getTextureIndex(ConnectedTextureBehaviour.CTContext context);
}
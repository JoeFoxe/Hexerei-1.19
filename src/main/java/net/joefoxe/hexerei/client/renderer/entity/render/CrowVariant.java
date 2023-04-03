package net.joefoxe.hexerei.client.renderer.entity.render;

import java.util.Arrays;
import java.util.Comparator;

public enum CrowVariant {
    BLACK(0),
    HOODED(1),
    NORTHWESTERN(2),
    PIED(3),
    ALBINO(4),
    GRAY(5),
    DARKBROWN(6);

    private static final CrowVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(CrowVariant::getId)).toArray(CrowVariant[]::new);
    private final int id;

    private CrowVariant(int pId) {
        this.id = pId;
    }

    public int getId() {
        return this.id;
    }

    public static CrowVariant byId(int pId) {
        return BY_ID[pId % BY_ID.length];
    }
}

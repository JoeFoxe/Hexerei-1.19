package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.events.AnimalFatAdditionModifier;
import net.joefoxe.hexerei.events.SageSeedAdditionModifier;

public class ModLootModifiers {
    public static void init()
    {
        SageSeedAdditionModifier.init();
        AnimalFatAdditionModifier.init();
    }
}

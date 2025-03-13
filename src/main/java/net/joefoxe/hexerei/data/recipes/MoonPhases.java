package net.joefoxe.hexerei.data.recipes;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MoonPhases {
    public enum MoonCondition implements StringRepresentable {
        NONE("none"),
        NEW_MOON("new_moon"),
        WAXING_CRESCENT("waxing_crescent"),
        FIRST_QUARTER("first_quarter"),
        WAXING_GIBBOUS("waxing_gibbous"),
        FULL_MOON("full_moon"),
        WANING_GIBBOUS("waning_gibbous"),
        LAST_QUARTER("last_quarter"),
        WANING_CRESCENT("waning_crescent");

        private final String name;

        MoonCondition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static final StringRepresentable.EnumCodec<MoonCondition> CODEC = StringRepresentable.fromEnum(MoonCondition::values);

        public String getNameTranslated(){
            return "tooltip.hexerei." + name;
        }
        public static MoonCondition getMoonCondition(String str) {
            return switch (str) {
                case "new_moon" -> NEW_MOON;
                case "waxing_crescent" -> WAXING_CRESCENT;
                case "first_quarter" -> FIRST_QUARTER;
                case "waxing_gibbous" -> WAXING_GIBBOUS;
                case "full_moon" -> FULL_MOON;
                case "waning_gibbous" -> WANING_GIBBOUS;
                case "last_quarter" -> LAST_QUARTER;
                case "waning_crescent" -> WANING_CRESCENT;
                default -> NONE;
            };
        }

        public static MoonCondition getMoonPhase(Level level) {

            if (level == null)
                return NONE;
            long time = level.getDayTime();
            int phase = level.getMoonPhase();
            if (time % 24000 < 12300 || time % 24000 > 23850) {// (time % 24000 < 12300 || time % 24000 > 23850) {
                return NONE;
            }
            return switch (phase) {
                case 0 -> FULL_MOON;
                case 1 -> WANING_GIBBOUS;
                case 2 -> LAST_QUARTER;
                case 3 -> WANING_CRESCENT;
                case 4 -> NEW_MOON;
                case 5 -> WAXING_CRESCENT;
                case 6 -> FIRST_QUARTER;
                default -> WAXING_GIBBOUS;
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return switch (this){
                default -> "none";
                case NEW_MOON -> NEW_MOON.getName();
                case WAXING_CRESCENT -> WAXING_CRESCENT.getName();
                case FIRST_QUARTER -> FIRST_QUARTER.getName();
                case WAXING_GIBBOUS -> WAXING_GIBBOUS.getName();
                case FULL_MOON -> FULL_MOON.getName();
                case WANING_GIBBOUS -> WANING_GIBBOUS.getName();
                case LAST_QUARTER -> LAST_QUARTER.getName();
                case WANING_CRESCENT -> WANING_CRESCENT.getName();
            };
        }
    }
}

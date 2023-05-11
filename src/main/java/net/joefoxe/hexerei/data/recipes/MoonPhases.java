package net.joefoxe.hexerei.data.recipes;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

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

        public String getNameTranslated(){
            return "tooltip.hexerei." + name;
        }
        public static MoonCondition getMoonCondition(String str) {
            switch (str) {
                case "new_moon":
                    return NEW_MOON;
                case "waxing_crescent":
                    return WAXING_CRESCENT;
                case "first_quarter":
                    return FIRST_QUARTER;
                case "waxing_gibbous":
                    return WAXING_GIBBOUS;
                case "full_moon":
                    return FULL_MOON;
                case "waning_gibbous":
                    return WANING_GIBBOUS;
                case "last_quarter":
                    return LAST_QUARTER;
                case "waning_crescent":
                    return WANING_CRESCENT;
                default:
                    return NONE;
            }
        }

        public static MoonCondition getMoonPhase(Level level) {
            long time = level.getDayTime();
            int days = (int) (time / 24000L);
            int phase = days % 8;
            if (time % 24000 < 12300 || time % 24000 > 23850) {
                return NONE;
            }
            switch (phase) {
                case 0:
                    return FULL_MOON;
                case 1:
                    return WANING_GIBBOUS;
                case 2:
                    return LAST_QUARTER;
                case 3:
                    return WANING_CRESCENT;
                case 4:
                    return NEW_MOON;
                case 5:
                    return WAXING_CRESCENT;
                case 6:
                    return FIRST_QUARTER;
                default:
                    return WAXING_GIBBOUS;
            }
        }

        @Override
        public String getSerializedName() {
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

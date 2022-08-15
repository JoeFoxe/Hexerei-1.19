package net.joefoxe.hexerei.util;

import java.util.*;

public class HexereiSupporterBenefits {

    public static UUID getUuid(String uuid) {
        UUID id = UUID.fromString(uuid);
        return id;
    }

    public static final UUID JOE = getUuid("1241d572-92a5-4b6b-a650-a5272139e52a");
    public static final UUID DEV = getUuid("380df991-f603-344c-a090-369bad2a924a");
    public static final UUID KAUPENJOE = getUuid("665ad10f-5548-4985-986b-b642732ddce0");
    public static final UUID WILLTARAX = getUuid("7c78529d-d1a6-4564-ba45-35d8892de403");
    public static final UUID ALEC = getUuid("490255a6-f8e3-402a-8e22-22ee95c4b9f6");
    public static final UUID SAPHRYM = getUuid("71b78099-ad3d-42bd-ae79-7cc51fd51860");
    public static final UUID GWEN_WAV = getUuid("476facfc-a8eb-452d-a8a2-7ccd50eae6cd");

    public static final Collection<UUID> supporters = new ArrayList<>(Arrays.asList(JOE, DEV, KAUPENJOE, WILLTARAX, ALEC, SAPHRYM, GWEN_WAV));

    public static boolean matchesSupporterUUID(UUID uuid){

        for(UUID supporter_uuid : supporters){
            if(uuid.equals(supporter_uuid))
                return true;
        }

        return false;
    }

}

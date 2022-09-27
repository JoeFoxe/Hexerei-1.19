package net.joefoxe.hexerei.sounds;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Hexerei.MOD_ID);

    public static RegistryObject<SoundEvent> CROW_CAW = registerSoundEvent("crow_caw");

    public static RegistryObject<SoundEvent> CROW_FLUTE = registerSoundEvent("crow_flute");
    public static RegistryObject<SoundEvent> CROW_FLUTE_SELECT = registerSoundEvent("crow_flute_select");
    public static RegistryObject<SoundEvent> CROW_FLUTE_DESELECT = registerSoundEvent("crow_flute_deselect");

    public static RegistryObject<SoundEvent> BROOM_WHISTLE = registerSoundEvent("whistle");
    public static RegistryObject<SoundEvent> HOOTSIFER = registerSoundEvent("hootsifer");

    public static RegistryObject<SoundEvent> BOOK_TURN_PAGE_SLOW = registerSoundEvent("book_turn_page_slow");
    public static RegistryObject<SoundEvent> BOOK_TURN_PAGE_FAST = registerSoundEvent("book_turn_page_fast");
    public static RegistryObject<SoundEvent> BOOKMARK_BUTTON = registerSoundEvent("bookmark_button");
    public static RegistryObject<SoundEvent> BOOKMARK_SWAP = registerSoundEvent("bookmark_swap");
    public static RegistryObject<SoundEvent> BOOKMARK_DELETE = registerSoundEvent("bookmark_delete");

    public static RegistryObject<SoundEvent> BOOK_CLOSE = registerSoundEvent("book_close");
    public static RegistryObject<SoundEvent> BOOK_OPENING = registerSoundEvent("book_opening");


    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Hexerei.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
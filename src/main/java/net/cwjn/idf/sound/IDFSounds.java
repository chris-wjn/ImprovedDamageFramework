package net.cwjn.idf.sound;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IDFSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<SoundEvent> CRIT_SOUND = register("crit_sound");
    public static final RegistryObject<SoundEvent> EVADE_SOUND = register("evade_sound");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(ImprovedDamageFramework.MOD_ID, name)));
    }

}

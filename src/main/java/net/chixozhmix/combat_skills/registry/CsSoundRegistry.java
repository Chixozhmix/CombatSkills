package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CsSoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CombatSkills.MODID);

    public static final RegistryObject<SoundEvent> DRAGON_SPEAR_CAST = registerSoundEvent("dragon_spear_cast");
    public static final RegistryObject<SoundEvent> RAGE_START_SOUND = registerSoundEvent("rage_start_sound");
    public static final RegistryObject<SoundEvent> RAGE_END_SOUND = registerSoundEvent("rage_end_sound");
    public static final RegistryObject<SoundEvent> HOOKING = registerSoundEvent("hooking");
    public static final RegistryObject<SoundEvent> CRUSHING_BLOW = registerSoundEvent("crushing_blow");
    public static final RegistryObject<SoundEvent> SWIFT_JERK = registerSoundEvent("swift_jerk");
    public static final RegistryObject<SoundEvent> SLICING_VORTEX = registerSoundEvent("slicing_vortex");
    public static final RegistryObject<SoundEvent> WORLD_CRUSHER_START = registerSoundEvent("world_crusher_start");
    public static final RegistryObject<SoundEvent> WORLD_CRUSHER_END = registerSoundEvent("world_crusher_end");
    public static final RegistryObject<SoundEvent> SWORD_AURA_END = registerSoundEvent("sword_aura_end");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CombatSkills.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}

package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.effect.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class CsMobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, CombatSkills.MODID);

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }

    public static final RegistryObject<MobEffect> SWIFT_JERK_EFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("swift_jerk_effect",
            () -> new SwiftJerkEffect(MobEffectCategory.BENEFICIAL, 0xffef95));
    public static final RegistryObject<MobEffect> RAM_EFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("ram_effect",
            () -> new RamEffect(MobEffectCategory.BENEFICIAL, 0xffef95));
    public static final RegistryObject<MobEffect> PARRY_EFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("parry_effect", ParryEffect::new);
    public static final RegistryObject<MobEffect> INSTANT_STAMINA = MOB_EFFECT_DEFERRED_REGISTER.register("instant_stamina",
            () -> new InstantStaminaEffect(MobEffectCategory.BENEFICIAL, 0xF0AE4D));
    public static final RegistryObject<MobEffect> STUNNING = MOB_EFFECT_DEFERRED_REGISTER.register("stunning",
            () -> new StuningEffect());

    public static final RegistryObject<MobEffect> RAGE_EFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("rage_effect", RageEffect::new);


}

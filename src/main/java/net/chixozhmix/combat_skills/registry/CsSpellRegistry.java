package net.chixozhmix.combat_skills.registry;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.skills.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CsSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, CombatSkills.MODID);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static RegistryObject<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final RegistryObject<AbstractSpell> SWORD_AURA = registerSpell(new SwordAura());
    public static final RegistryObject<AbstractSpell> SWIFT_JERK = registerSpell(new SwiftJerkSkill());
    public static final RegistryObject<AbstractSpell> CRUSHING_BLOW = registerSpell(new CrushingBlowSkill());
    public static final RegistryObject<AbstractSpell> DRAGON_SPEAR = registerSpell(new DragonSpearSkill());
    public static final RegistryObject<AbstractSpell> RAGE = registerSpell(new RageSkill());
    public static final RegistryObject<AbstractSpell> RUTHLESS_UPPERCUT = registerSpell(new RuthlessUppercutSkill());
    public static final RegistryObject<AbstractSpell> HOOKING = registerSpell(new HookingSkill());
    public static final RegistryObject<AbstractSpell> PARRY = registerSpell(new ParrySkill());
    public static final RegistryObject<AbstractSpell> SHIELD_STRIKE = registerSpell(new ShieldStrikeSkill());
    public static final RegistryObject<AbstractSpell> RAM = registerSpell(new RamSkill());


}
package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class CsDamageTypes {

    public static ResourceKey<DamageType> register(String name)
    {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CombatSkills.MODID, name));
    }

    // Magic
    public static final ResourceKey<DamageType> SWORD_MAGIC = register("sword_magic");


    // Do we actually need this?
    public static void bootstrap(BootstapContext<DamageType> context)
    {
        //context.register(ABYSSAL_MAGIC, new DamageType(ABYSSAL_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0F));
    }

}

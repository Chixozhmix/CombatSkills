package net.chixozhmix.combat_skills.registry;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.util.CsTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CsSchoolRegistry extends SchoolRegistry {



    private static final DeferredRegister<SchoolType> BAGELS_SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, CombatSkills.MODID);

    public static void register(IEventBus eventBus)
    {
        BAGELS_SCHOOLS.register(eventBus);
    }

    private static RegistryObject<SchoolType> registerSchool(SchoolType type)
    {
        return BAGELS_SCHOOLS.register(type.getId().getPath(), () -> type);
    }


    // Sword
    public static final ResourceLocation SWORD_RESOURCE = CombatSkills.id("sword");

    public static final RegistryObject<SchoolType> SWORD = registerSchool(new SchoolType
            (
                    SWORD_RESOURCE,
                    CsTags.SWORD_FOCUS,
                    Component.translatable("school.combat_skills.sword").withStyle(Style.EMPTY.withColor(0xffffff)),
                    LazyOptional.of(CsAttributeRegistry.SWORD_MAGIC_POWER::get),
                    LazyOptional.of(CsAttributeRegistry.SWORD_MAGIC_RESIST::get),
                    LazyOptional.of(SoundRegistry.EVOCATION_CAST::get),
                    CsDamageTypes.SWORD_MAGIC
            ));

}

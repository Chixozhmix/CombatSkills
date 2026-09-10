package net.chixozhmix.combat_skills.registry;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


@Mod.EventBusSubscriber(modid = CombatSkills.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CsAttributeRegistry {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, CombatSkills.MODID);

    public static final RegistryObject<Attribute> SWORD_MAGIC_RESIST = registerResistanceAttribute("sword");
    public static final RegistryObject<Attribute> SWORD_MAGIC_POWER = registerPowerAttribute("sword");
    public static final RegistryObject<Attribute> SKILL_POWER = ATTRIBUTES.register("skill_power", () -> (new MagicPercentAttribute(
            "attribute.combat_skills.skill_power", (double)1.0F, (double)-100.0F, (double)100.0F)).setSyncable(true));


    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }



    private static RegistryObject<Attribute> registerResistanceAttribute(String id)
    {
        return ATTRIBUTES.register(id + "_magic_resist", () ->
                (new MagicRangedAttribute("attribute.combat_skills." + id + "_magic_resist",
                        1.0D, 0, 10).setSyncable(true)));
    }

    private static RegistryObject<Attribute> registerPowerAttribute(String id)
    {
        return ATTRIBUTES.register(id + "_spell_power", () ->
                (new MagicRangedAttribute("attribute.combat_skills." + id + "_spell_power",
                        1.0D, 0, 10).setSyncable(true)));
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute.get())));
    }

    private static RegistryObject<Attribute> newResistanceAttribute(String id) {
        return ATTRIBUTES.register(id + "_magic_resist", () -> (new MagicRangedAttribute("attribute.irons_spellbooks." + id + "_magic_resist", 1.0D, -100, 100).setSyncable(true)));
    }

    private static RegistryObject<Attribute> newPowerAttribute(String id) {
        return ATTRIBUTES.register(id + "_spell_power", () -> (new MagicRangedAttribute("attribute.irons_spellbooks." + id + "_spell_power", 1.0D, -100, 100).setSyncable(true)));
    }
}

package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CsItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CombatSkills.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}



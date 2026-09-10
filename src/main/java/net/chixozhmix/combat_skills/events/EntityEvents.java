package net.chixozhmix.combat_skills.events;

import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityEvents {
    @SubscribeEvent
    public static void attackEvent(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();

        if(attacker instanceof Mob mob && mob.hasEffect(CsMobEffectRegistry.STUNNING.get())) {
            event.setCanceled(true);
        }
    }
}

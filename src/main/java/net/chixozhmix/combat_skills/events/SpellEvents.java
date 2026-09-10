package net.chixozhmix.combat_skills.events;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CombatSkills.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpellEvents {
    @SubscribeEvent
    public static void cast(SpellPreCastEvent event) {
        LivingEntity entity = event.getEntity();

        if(entity.hasEffect(CsMobEffectRegistry.RAGE_EFFECT.get()) && event.getSchoolType() != CsSchoolRegistry.SWORD.get()) {
            event.setCanceled(true);

            if(entity instanceof Player player) {
                player.displayClientMessage(Component.translatable("ui.combat_scills.no_cast"), true);
            }
        }
    }
}

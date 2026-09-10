package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.screen.ScSpellEffectsOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CombatSkills.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CsOverlayRegistry {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("cs_spell_effects", ScSpellEffectsOverlay.instance);
    }
}

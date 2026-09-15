package net.chixozhmix.combat_skills.events;

import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.entity.sword_aura.SwordAuraRenderer;
import net.chixozhmix.combat_skills.registry.CsEntityRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CombatSkills.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
   @SubscribeEvent
   public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
       event.registerEntityRenderer(CsEntityRegistry.SWORD_AURA_PROJECTILE.get(), SwordAuraRenderer::new);
       event.registerEntityRenderer(CsEntityRegistry.DRAGON_SPEAR_AOE.get(), NoopRenderer::new);
   }
}
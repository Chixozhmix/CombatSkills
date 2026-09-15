package net.chixozhmix.combat_skills.events;

import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.util.CsTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CombatSkills.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if(stack.is(CsTags.SWORDS)) event.getToolTip().add(getTagName(ItemTags.SWORDS));
        else if(stack.is(CsTags.AXES)) event.getToolTip().add(getTagName(ItemTags.AXES));
        else if(stack.is(CsTags.SPEARS)) event.getToolTip().add(getTagName(CsTags.SPEARS));
        else if(stack.is(CsTags.DAGGERS)) event.getToolTip().add(getTagName(CsTags.DAGGERS));
        else if(stack.is(CsTags.HAMMERS)) event.getToolTip().add(getTagName(CsTags.HAMMERS));
        else if(stack.is(CsTags.MACES)) event.getToolTip().add(getTagName(CsTags.MACES));
        else if(stack.is(CsTags.STAFFS)) event.getToolTip().add(getTagName(CsTags.STAFFS));
        else if(stack.is(CsTags.SCYTHE)) event.getToolTip().add(getTagName(CsTags.SCYTHE));
        else if(stack.is(CsTags.SHIELDS)) event.getToolTip().add(getTagName(CsTags.SHIELDS));
        else if(stack.is(CsTags.BOWS)) event.getToolTip().add(getTagName(CsTags.BOWS));

    }

    private static MutableComponent getTagName(TagKey<Item> tag) {
        return Component.translatable("tooltip.combat_skills.skill_type", Component.translatable(tag.location().toLanguageKey())).withStyle(ChatFormatting.GOLD);
    }
}

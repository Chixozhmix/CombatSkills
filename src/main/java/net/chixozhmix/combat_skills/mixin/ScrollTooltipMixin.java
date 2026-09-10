package net.chixozhmix.combat_skills.mixin;


import io.redspace.ironsspellbooks.api.events.CustomizeScrollModNameEvent;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(TooltipsUtils.class)
public class ScrollTooltipMixin {
    @Shadow public static MutableComponent getLevelComponenet(SpellData spellData, LivingEntity caster) {throw new AssertionError();}
    @Shadow public static MutableComponent getManaCostComponent(CastType castType, int manaCost) {throw new AssertionError();}
    @Shadow public static MutableComponent getCastTimeComponent(CastType type, String castTime) {throw new AssertionError();}
    @Shadow public static Style getStyleFor(Player player, AbstractSpell spell) {throw new AssertionError();}

    @Overwrite(remap = false)
    public static List<Component> formatScrollTooltip(ItemStack stack, Player player) {
        if (stack.getItem() instanceof Scroll && ISpellContainer.isSpellContainer(stack)) {
            ISpellContainer spellList = ISpellContainer.get(stack);
            if (spellList.isEmpty()) {
                return List.of();
            } else {
                SpellData spellData = spellList.getSpellAtIndex(0);
                AbstractSpell spell = spellData.getSpell();
                int spellLevel = spell.getLevelFor(spellData.getLevel(), player);
                MutableComponent levelText = getLevelComponenet(spellData, player);
                MutableComponent title = Component.translatable("tooltip.irons_spellbooks.level", new Object[]{levelText}).append(" ").append(Component.translatable("tooltip.irons_spellbooks.rarity", new Object[]{spell.getRarity(spellData.getLevel()).getDisplayName()}).withStyle(spell.getRarity(spellData.getLevel()).getDisplayName().getStyle())).withStyle(ChatFormatting.GRAY);
                List<MutableComponent> uniqueInfo = spell.getUniqueInfo(spellLevel, player);
                MutableComponent whenInSpellBook = Component.translatable("tooltip.irons_spellbooks.scroll_tooltip").withStyle(ChatFormatting.GRAY);
                MutableComponent cost;
                if (spell instanceof AbstractCombatSkill combatSkill) {
                    cost = getStaminaCostComponent(combatSkill.getCastType(), combatSkill.getManaCost(spellLevel)).withStyle(ChatFormatting.BLUE);
                } else {
                    cost = getManaCostComponent(spell.getCastType(), spell.getManaCost(spellLevel)).withStyle(ChatFormatting.BLUE);
                }
                MutableComponent cooldownTime = Component.translatable("tooltip.irons_spellbooks.cooldown_length_seconds", new Object[]{Utils.timeFromTicks((float) MagicManager.getEffectiveSpellCooldown(spell, player, CastSource.SCROLL), 2)}).withStyle(ChatFormatting.BLUE);
                MutableComponent castType = null;
                if (spell.getCastType() != CastType.INSTANT) {
                    castType = Component.literal(" ").append(getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks((float)spell.getEffectiveCastTime(spellLevel, player), 2)).withStyle(ChatFormatting.BLUE));
                }

                List<Component> lines = new ArrayList();
                String parentModId = spell.getSpellResource().getNamespace();
                if (!parentModId.equals("irons_spellbooks")) {
                    Optional<Component> modLabel = CustomizeScrollModNameEvent.resolveModLabel(parentModId);
                    Objects.requireNonNull(lines);
                    modLabel.ifPresent(lines::add);
                }

                lines.add(Component.literal(" ").append(title));
                uniqueInfo.forEach((line) -> lines.add(Component.literal(" ").append(line.withStyle(line.getStyle().applyTo(getStyleFor(player, spell))))));
                if (castType != null) {
                    lines.add(castType);
                }

                lines.add(Component.empty());
                lines.add(whenInSpellBook);
                if (spell.getManaCost(spellLevel) > 0) {
                    lines.add(cost);
                }

                if (spell.getSpellCooldown() > 0) {
                    lines.add(cooldownTime);
                }

                lines.add(spell.getSchoolType().getDisplayName().copy());
                return lines;
            }
        } else {
            return List.of();
        }
    }

    @Unique
    private static MutableComponent getStaminaCostComponent(CastType castType, int manaCost) {
        return castType == CastType.CONTINUOUS ? Component.translatable("tooltip.combat_skills.stamina_cost_per_second", new Object[]{manaCost * 2}) : Component.translatable("ui.combat_skills.stamina_cost", new Object[]{manaCost});
    }
}

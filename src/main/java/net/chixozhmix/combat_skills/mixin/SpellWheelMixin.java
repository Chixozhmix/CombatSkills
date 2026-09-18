package net.chixozhmix.combat_skills.mixin;

import com.mojang.blaze3d.vertex.*;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpellWheelOverlay.class)
public class SpellWheelMixin {
    @Shadow
    private int wheelSelection;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
            ),
            remap = true
    )
    private MutableComponent combatSkills$replaceManaCost(String key, Object[] args) {
        // Перехватываем только стоимость маны
        if (!"ui.irons_spellbooks.mana_cost".equals(key))
            return Component.translatable(key, args);

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null)
            return Component.translatable(key, args);

        SpellSelectionManager manager = ClientMagicData.getSpellSelectionManager();

        if (wheelSelection < 0 || wheelSelection >= manager.getSpellCount())
            return Component.translatable(key, args);

        AbstractSpell spell = manager.getSpellData(wheelSelection).getSpell();

        if (spell instanceof AbstractCombatSkill combatSkill) {
            int spellLevel = spell.getLevelFor(manager.getSpellData(wheelSelection).getLevel(), minecraft.player);
            return Component.translatable("ui.combat_skills.stamina_cost", combatSkill.getStaminaCost(spellLevel)).withStyle(ChatFormatting.AQUA);
        }

        // Обычное заклинание — возвращаем оригинальную стоимость маны
        return Component.translatable(key, args);
    }
}

package net.chixozhmix.combat_skills.effect;

import net.funkpla.staminafortweakers.registry.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;

public class InstantStaminaEffect extends MobEffect {

    public InstantStaminaEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        int level = amplifier + 1;

        AttributeInstance staminaAttribute = entity.getAttribute(Attributes.STAMINA);
        AttributeInstance maxStaminaAttribute = entity.getAttribute(Attributes.MAX_STAMINA);

        if (staminaAttribute == null || maxStaminaAttribute == null) {
            return;
        }

        double currentStamina = staminaAttribute.getBaseValue();
        double maxStamina = maxStaminaAttribute.getValue();

        double staminaAdd = level * 25.0;

        double newStamina = Math.min(currentStamina + staminaAdd, maxStamina);

        staminaAttribute.setBaseValue(newStamina);
    }
}
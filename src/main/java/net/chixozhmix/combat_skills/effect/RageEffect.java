package net.chixozhmix.combat_skills.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RageEffect extends MobEffect {
    public RageEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xCF0606);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "1d18a2a1-6b1f-11ae-4c90-0322bc101005", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "1d18a2a1-6b1f-11ae-4c90-0322bc101011", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "1d18a2a1-6b1f-11ae-4c90-1322bc101101", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "1d18a2a1-6b1f-11ae-4c91-0322bc101101", 0.15, AttributeModifier.Operation.MULTIPLY_BASE);
        //this.addAttributeModifier(AttributesMod.JUMP, "1d18a2a1-6b1f-11ae-4c90-0322bc101101", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "1d18a2a1-6b1f-11ae-4c80-0322bc101101", 0.1, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.level().isClientSide)
            return;

        if (pLivingEntity.getEffect(this) != null &&
                pLivingEntity.getEffect(this).getDuration() <= 1) {

            pLivingEntity.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    100,
                    0
            ));
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration <= 1;
    }
}

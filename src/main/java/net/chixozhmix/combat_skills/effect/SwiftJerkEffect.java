package net.chixozhmix.combat_skills.effect;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.mixin.LivingEntityAccessor;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SwiftJerkEffect extends MagicMobEffect {
    public SwiftJerkEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        List<Entity> list = pLivingEntity.level().getEntities(pLivingEntity, pLivingEntity.getBoundingBox().inflate((double)0.25F, (double)0.5F, (double)0.25F));
        if (!list.isEmpty()) {
            for(Entity entity : list) {
                if (entity instanceof LivingEntity) {
                    DamageSources.applyDamage(entity, (float)pAmplifier, (CsSpellRegistry.SWIFT_JERK.get()).getDamageSource(pLivingEntity));
                    entity.invulnerableTime = 20;
                }
            }
        } else if (pLivingEntity.horizontalCollision) {
            pLivingEntity.removeEffect(this);
            return;
        }

        pLivingEntity.fallDistance = 0.0F;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        ((LivingEntityAccessor)pLivingEntity).setLivingEntityFlagInvoker(4, true);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        ((LivingEntityAccessor)pLivingEntity).setLivingEntityFlagInvoker(4, false);
    }
}

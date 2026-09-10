package net.chixozhmix.combat_skills.effect;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RamEffect extends MagicMobEffect {
    public RamEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 movement = entity.getDeltaMovement();
        AABB box = entity.getBoundingBox().expandTowards(movement).inflate(0.3D, 0.2D, 0.3D);
        List<Entity> entities = entity.level().getEntities(entity, box);

        for (Entity target : entities) {
            if (target instanceof LivingEntity livingTarget) {
                DamageSources.applyDamage(livingTarget, (float) amplifier, CsSpellRegistry.RAM.get().getDamageSource(entity));

                livingTarget.invulnerableTime = 20;
            }
        }

        if (entity.horizontalCollision) {
            entity.removeEffect(this);
            return;
        }

        entity.fallDistance = 0.0F;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
    }
}

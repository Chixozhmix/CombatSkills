package net.chixozhmix.combat_skills.entity;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import net.chixozhmix.combat_skills.registry.CsEntityRegistry;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class DragonSpearQuakeEntity extends EarthquakeAoe {
    public DragonSpearQuakeEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DragonSpearQuakeEntity(Level level) {
        this(CsEntityRegistry.DRAGON_SPEAR_AOE.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        SpellDamageSource damageSource = (CsSpellRegistry.DRAGON_SPEAR.get()).getDamageSource(this, this.getOwner());
        DamageSources.ignoreNextKnockback(target);
        if (target.hurt(damageSource, this.getDamage())) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, this.getSlownessAmplifier()));
            target.setDeltaMovement(target.getDeltaMovement().add((double)0.0F, (double)0.5F, (double)0.0F));
            target.hurtMarked = true;
        }
    }
}

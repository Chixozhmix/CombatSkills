package net.chixozhmix.combat_skills.entity;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.chixozhmix.combat_skills.registry.CsEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class DamageAoe extends AoeEntity implements AntiMagicSusceptible {

    public DamageAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public DamageAoe(Level level) {
        this(CsEntityRegistry.DAMAGE_AOE.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        //var owner = getOwner();
        //IronsSpellbooks.LOGGER.debug("HealingAoe apply effect: target: {} owner: {} should heal: {}",target.getName().getString(),owner==null?null:owner.getName().getString(),owner==null?false: Utils.shouldHealEntity((LivingEntity) owner,target));
        if (target != getOwner()) {
            //DamageSources.applyDamage(target, getDamage(), CombatSkills.flower_domain_spell.get().getDamageSource(this, getOwner()));
            DamageSources.ignoreNextKnockback(target);
        } else if (target == getOwner()) {
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 2));
        }
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public void ambientParticles() {


    }

    @Override
    protected Vec3 getInflation() {
        return new Vec3(0, 1, 0);
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public void onAntiMagic(MagicData magicData) {
        discard();
    }
}

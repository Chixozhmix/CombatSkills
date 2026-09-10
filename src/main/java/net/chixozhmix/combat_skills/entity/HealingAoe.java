package net.chixozhmix.combat_skills.entity;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class HealingAoe extends AoeEntity implements AntiMagicSusceptible {

    public HealingAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public HealingAoe(Level level) {
        this(EntityRegistry.HEALING_AOE.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        //var owner = getOwner();
        //IronsSpellbooks.LOGGER.debug("HealingAoe apply effect: target: {} owner: {} should heal: {}",target.getName().getString(),owner==null?null:owner.getName().getString(),owner==null?false: Utils.shouldHealEntity((LivingEntity) owner,target));


        if (getOwner() instanceof LivingEntity owner && Utils.shouldHealEntity(owner, target)) {
            float healAmount = getDamage();
            MinecraftForge.EVENT_BUS.post(new SpellHealEvent((LivingEntity) getOwner(), target, healAmount, SchoolRegistry.HOLY.get()));
            target.heal(healAmount);
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

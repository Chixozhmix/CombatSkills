package net.chixozhmix.combat_skills.entity.slicing_vortex;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class SlicingVortexEntity extends AoeEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> DATA_IS_ANIMATING_RISE = SynchedEntityData.defineId(SlicingVortexEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_ANIMATING_DISCARD = SynchedEntityData.defineId(SlicingVortexEntity.class, EntityDataSerializers.BOOLEAN);
    public static final float HEIGHT = 3f;

    private static final RawAnimation RISE_ANIM = RawAnimation.begin().thenPlay("spawn");
    private static final RawAnimation VORTEX_ANIM = RawAnimation.begin().thenLoop("vortex");
    private static final RawAnimation DESPAWN_ANIM = RawAnimation.begin().thenPlay("despawn");

    private final AnimatableInstanceCache cache  = GeckoLibUtil.createInstanceCache(this);
    private int riseAnimTime;
    private int discardAnimTime;

    private final AnimationController<SlicingVortexEntity> riseController;
    private final AnimationController<SlicingVortexEntity> discardController;
    private final AnimationController<SlicingVortexEntity> vortexController;

    public SlicingVortexEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCircular();
        this.riseAnimTime = 15;

        this.riseController = new AnimationController<>(this, "rise", 0, this::risePredicate);
        this.discardController = new AnimationController<>(this, "discard", 0, this::discardPredicate);
        this.vortexController = new AnimationController<>(this, "vortex", 0, this::vortexPredicate);

        triggerRiseAnimation();
    }

    public boolean isAnimatingRise() {
        return this.entityData.get(DATA_IS_ANIMATING_RISE);
    }

    public void triggerRiseAnimation() {
        this.entityData.set(DATA_IS_ANIMATING_RISE, true);
    }

    public boolean isAnimatingDiscard() {
        return this.entityData.get(DATA_IS_ANIMATING_DISCARD);
    }

    public void triggerDespawnAnimation() {
        if (this.isAnimatingDiscard())
            return;

        this.entityData.set(DATA_IS_ANIMATING_DISCARD, true);
        this.discardAnimTime = 15;
    }

    private PlayState risePredicate(AnimationState<SlicingVortexEntity> state) {
        if (!this.isAnimatingRise()) {
            return PlayState.STOP;
        }

        if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().setAnimation(RISE_ANIM);
        }

        return PlayState.CONTINUE;
    }

    private PlayState discardPredicate(AnimationState<SlicingVortexEntity> state) {
        if (!this.isAnimatingDiscard()) {
            return PlayState.STOP;
        }

        if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().setAnimation(DESPAWN_ANIM);
        }

        return PlayState.CONTINUE;
    }

    private PlayState vortexPredicate(AnimationState<SlicingVortexEntity> state) {
        if(this.isAnimatingRise())
            return PlayState.STOP;

        if(this.tickCount < this.getDuration())
            state.getController().setAnimation(VORTEX_ANIM);

        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ANIMATING_RISE, false);
        this.entityData.define(DATA_IS_ANIMATING_DISCARD, false);
    }

    @Override
    public void applyEffect(LivingEntity livingEntity) {

    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if(this.isAnimatingDiscard()) {
            if(--this.discardAnimTime <= 0) {
                this.entityData.set(DATA_IS_ANIMATING_DISCARD, false);
                this.discard();
            }
            return;
        }

        if (owner == null || !owner.isAlive() || this.tickCount >= 185) {
            this.triggerDespawnAnimation();
            return;
        }

        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.setDeltaMovement(Vec3.ZERO);

        super.tick();

        if(this.isAnimatingRise()) {
            if (--this.riseAnimTime < 0) {
                this.entityData.set(DATA_IS_ANIMATING_RISE, false);
            }
        }
        var radius = getRadius();
        var entities = level().getEntities(this, this.getBoundingBox(), this::canHitEntity);

        for (Entity entity : entities) {
                DamageSources.applyDamage(entity, this.damage, (CsSpellRegistry.SLICING_VORTEX.get()).getDamageSource(this, this.getOwner()));
            }
//        if((tickCount - 1) % 10 == 0)
//            playSound(SoundRegistry.CONE_OF_COLD_LOOP.get(), 2, random.nextIntBetweenInclusive(15, 20)*0.1f);
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return super.canHitEntity(pTarget) && !DamageSources.isFriendlyFireBetween(this.getOwner(), pTarget);
    }

    @Override
    public void ambientParticles() {

    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 1.5F, HEIGHT);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(vortexController);
        controllerRegistrar.add(riseController);
        controllerRegistrar.add(discardController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

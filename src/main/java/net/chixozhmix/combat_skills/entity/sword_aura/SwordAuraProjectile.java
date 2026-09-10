package net.chixozhmix.combat_skills.entity.sword_aura;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import net.chixozhmix.combat_skills.registry.CsEntityRegistry;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SwordAuraProjectile extends Projectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SwordAuraProjectile.class, EntityDataSerializers.FLOAT);
    private static final double SPEED = 0.7d;
    private static final int EXPIRE_TIME = 40;
    public final int animationSeed;
    private final float maxRadius;
    public AABB oldBB;
    private int age;
    private float damage;
    public int animationTime;
    private List<Entity> victims;

    private static final int DELAY_TICKS = 5; // Adjust for how long to delay the slash (in ticks)

    public SwordAuraProjectile(EntityType<? extends SwordAuraProjectile> entityType, Level level) {
        super(entityType, level);
        animationSeed = Utils.random.nextInt(9999);

        setRadius(3f);
        maxRadius = 3;
        oldBB = getBoundingBox();
        victims = new ArrayList<>();
        this.setNoGravity(true);
    }

    public SwordAuraProjectile(EntityType<? extends SwordAuraProjectile> entityType, Level levelIn, LivingEntity shooter) {
        this(entityType, levelIn);
        setOwner(shooter);
        setYRot(shooter.getYRot());
        setXRot(shooter.getXRot());
    }

    public SwordAuraProjectile(Level levelIn, LivingEntity shooter) {
        this(CsEntityRegistry.SWORD_AURA_PROJECTILE.get(), levelIn, shooter);
    }

    public void shoot(Vec3 rotation) {
        setDeltaMovement(rotation.scale(SPEED));
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    //TODO: override "doWaterSplashEffect"
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);
    }

    public void setRadius(float newRadius) {
        if (newRadius <= maxRadius && !this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(newRadius, 0.0F, maxRadius));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        if (age <= DELAY_TICKS) {
            // Optional: You can even hide the slash by setting radius = 0 or skipping animation
            setRadius(0);
            return;
        }

        if (age > EXPIRE_TIME) {
            discard();
            return;
        }

        oldBB = getBoundingBox();
        setRadius(getRadius() + 0.7f);

        animationTime++;
        int totalFrames = 8;
        int frameDuration = 4;
        int maxAnimationTime = totalFrames * frameDuration;

        if (animationTime >= maxAnimationTime) {
            discard();
        }

        if (!level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                onHitBlock((BlockHitResult) hitresult);
            }

            for (Entity entity : level().getEntities(this, this.getBoundingBox())
                    .stream()
                    .filter(target -> canHitEntity(target) && !victims.contains(target))
                    .collect(Collectors.toSet())) {
                damageEntity(entity);
                if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
                    discard();
                    return;
                }
            }
        }

        setPos(position().add(getDeltaMovement()));
    }

    @Override
    public EntityDimensions getDimensions(Pose p_19721_) {
        this.getBoundingBox();
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        //irons_spellbooks.LOGGER.info("onSynchedDataUpdated");

        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        discard();
    }

    private void damageEntity(Entity entity) {
        if (!victims.contains(entity)) {
            DamageSources.applyDamage(entity, damage, CsSpellRegistry.SWORD_AURA.get().getDamageSource(this, getOwner()));
            victims.add(entity);
        }
    }

    //https://forge.gemwire.uk/wiki/Particles


    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != getOwner() && super.canHitEntity(entity);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
    }
}

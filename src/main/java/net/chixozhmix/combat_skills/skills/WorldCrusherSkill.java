package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.entity.DragonSpearQuakeEntity;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.registry.CsSpellRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class WorldCrusherSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = ResourceLocation.fromNamespaceAndPath(CombatSkills.MODID, "world_crusher");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMinRarity(SpellRarity.LEGENDARY)
            .setMaxLevel(3)
            .setCooldownSeconds(600)
            .build();

    public WorldCrusherSkill() {
        this.baseStaminaCost = 300;
        this.baseSkillPower = 40;
        this.staminaCostPerLevel = 100;
        this.skillPowerPerLevel = 10;
        this.castTime = 80;
    }


    @Override
    public ResourceLocation getSpellResource() {
        return skillId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return super.getCastStartAnimation();
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return super.getCastFinishAnimation();
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return super.getCastStartSound();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return super.getCastFinishSound();
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", this.getDamage(caster, spellLevel)),
                Component.translatable("ui.irons_spellbooks.radius", this.getRadius(caster, spellLevel)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.SWORDS, CsTags.AXES, CsTags.MACES, CsTags.HAMMERS))
                        .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.AXES) || SkillUtils.holdItem(entity, ItemTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.MACES)
                || SkillUtils.holdItem(entity, CsTags.HAMMERS)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        AABB area = new AABB(entity.blockPosition()).inflate(this.getRadius(entity, spellLevel));
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for(LivingEntity target : entities) {
            if(target == entity) continue;

            target.addEffect(new MobEffectInstance(CsMobEffectRegistry.STUNNING.get(), 100));
            DamageSources.applyDamage(target, getDamage(entity, spellLevel), getDamageSource(entity));
            knockbackEntity(entity, target);

            if(target.isDeadOrDying()) {
                Explosion explosion = new Explosion(level, entity, (CsSpellRegistry.WORLD_CRUSHER.get()).getDamageSource(entity, entity),
                        null, target.getX(), target.getY(), target.getZ(), 3, false, Explosion.BlockInteraction.DESTROY);
                if (!MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(level, explosion))) {
                    explosion.explode();
                    explosion.finalizeExplosion(false);
                }

                level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE, target.getSoundSource(), 4.0F, 0.7F + level.random.nextFloat() * 0.2F);
            }
        }

        DragonSpearQuakeEntity aoeEntity = new DragonSpearQuakeEntity(level);
        aoeEntity.moveTo(entity.position());
        aoeEntity.setOwner(entity);
        aoeEntity.setCircular();
        aoeEntity.setRadius(6);
        aoeEntity.setDuration(15);
        aoeEntity.setDamage(0);
        aoeEntity.setSlownessAmplifier(1);
        level.addFreshEntity(aoeEntity);

        spawnParticles(level, entity);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private void spawnParticles(Level level, LivingEntity entity) {
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions((CsSchoolRegistry.SWORD).get().getTargetingColor(), 10),
                entity.getX(), entity.getBoundingBox().getCenter().y, entity.getZ(), 3, (double)1.0F, (double)0.0F, (double)1.0F, (double)0.0F, true);
        MagicManager.spawnParticles(level, ParticleTypes.CLOUD,
                entity.getX(), entity.getBoundingBox().getCenter().y, entity.getZ(), 20, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.3F, true);
        MagicManager.spawnParticles(level, ParticleTypes.EXPLOSION,
                entity.getX(), entity.getBoundingBox().getCenter().y, entity.getZ(), 10, (double)1.0F, (double)0.0F, (double)1.0F, (double)0.3F, true);
    }

    private void knockbackEntity(LivingEntity caster, LivingEntity target) {
        Vec3 direction = target.position().subtract(caster.position()).normalize();

        float knockbackStrength = 1.2F;

        target.knockback(knockbackStrength, -direction.x, -direction.z);
    }

    private float getRadius(LivingEntity entity, int skillLevel) {
        return (this.getSpellPower(skillLevel, entity) / 2);
    }

    private float getDamage(LivingEntity entity, int skillLevel) {
        return this.getSpellPower(skillLevel, entity);
    }
}

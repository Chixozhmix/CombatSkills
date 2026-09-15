package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.api.skills.SkillAnimations;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.registry.CsSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RamSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "ram_skill");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(35)
            .build();

    public RamSkill() {
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 10;
        this.skillPowerPerLevel = 5;
        this.castTime = 25;
        this.baseStaminaCost = 45;
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
        return SkillAnimations.RAM_START;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.RAM_END;
    }

    @Override
    public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
        if (castData instanceof ImpulseCastData impulse) {
            entity.hasImpulse = impulse.hasImpulse;
            entity.setDeltaMovement(entity.getDeltaMovement().add(impulse.x, impulse.y, impulse.z));
        }

        super.onClientCast(level, spellLevel, entity, castData);
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new ImpulseCastData();
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)));
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(CsSoundRegistry.CRUSHING_BLOW.get());
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.hasImpulse = true;
        float multiplier = ((15.0F + this.getSpellPower(spellLevel, entity)) * 2) / 12.0F;
        Vec3 forward = entity.getLookAngle();
        Vec3 horizontalForward = new Vec3(forward.x, 0.0D, forward.z).normalize();

        Vec3 vec = horizontalForward.scale(multiplier);

        playerMagicData.setAdditionalCastData(new ImpulseCastData((float)vec.x, (float)vec.y, (float)vec.z, true));
        entity.setDeltaMovement(new Vec3(Mth.lerp((double)0.75F, entity.getDeltaMovement().x, vec.x), Mth.lerp((double)0.75F, entity.getDeltaMovement().y, vec.y), Mth.lerp((double)0.75F, entity.getDeltaMovement().z, vec.z)));
        entity.addEffect(new MobEffectInstance((MobEffect) CsMobEffectRegistry.RAM_EFFECT.get(), 15, (int)(getDamage(spellLevel, entity)), false, false, false));
        entity.invulnerableTime = 20;
        playerMagicData.getSyncedData();

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public float getDamage(int spellLevel, LivingEntity entity) {
        float additionalDamage = getAdditionalDamage(entity);

        return 4.0F + additionalDamage + spellLevel;
    }
}

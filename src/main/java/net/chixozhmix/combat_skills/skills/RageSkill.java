package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RageSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "rage_skill");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(120)
            .build();

    public RageSkill() {
        this.baseStaminaCost = 45;
        this.castTime = 40;
        this.staminaCostPerLevel = 4;
        this.baseSkillPower = 10;
        this.skillPowerPerLevel = 2;
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
        return SkillAnimations.RAGE_START;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.RAGE_END;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(CsSoundRegistry.RAGE_START_SOUND.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(CsSoundRegistry.RAGE_END_SOUND.get());
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks((float)this.getDurationTicks(spellLevel, caster), 1)}));
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

        entity.addEffect(new MobEffectInstance(CsMobEffectRegistry.RAGE_EFFECT.get(), getDurationTicks(spellLevel, entity)));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, getDurationTicks(spellLevel, entity)));

        CameraShakeManager.addCameraShake(new CameraShakeData(30, entity.position(), 2.0F));
    }

    private int getDurationTicks(int spellLevel, LivingEntity entity) {
        return (int)(500.0F * spellLevel);
    }

    @Override
    public boolean canBeInterrupted(@Nullable Player player) {
        return false;
    }
}

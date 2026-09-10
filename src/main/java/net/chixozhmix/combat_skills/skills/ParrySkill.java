package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.api.skills.SkillAnimations;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ParrySkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "parry");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(20)
            .build();

    public ParrySkill() {
        this.staminaCostPerLevel = 3;
        this.baseSkillPower = 3;
        this.skillPowerPerLevel = 1;
        this.castTime = 0;
        this.baseStaminaCost = 20;
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
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.PARRY;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.combat_skills.skill_component", getTagNames(CsTags.SHIELDS)).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.SHIELDS)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 13, 20, false, false, false));
        entity.addEffect(new MobEffectInstance(CsMobEffectRegistry.PARRY_EFFECT.get(), 13, 0, false, false, false));

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

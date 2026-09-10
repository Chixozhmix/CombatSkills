package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.entity.sword_aura.SwordAuraProjectile;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SwordAura extends AbstractCombatSkill {
    private final ResourceLocation spellId = new ResourceLocation(CombatSkills.MODID, "sword_aura");

    public SwordAura() {
        this.baseStaminaCost = 45;
        this.castTime = 4;
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 2;
        this.skillPowerPerLevel = 1;
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setMaxLevel(4)
            .setCooldownSeconds(20)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .build();

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.SWORDS, CsTags.STAFFS, CsTags.SCYTHE)).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, ItemTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.STAFFS) || SkillUtils.holdItem(entity, CsTags.SCYTHE)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {

    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SwordAuraProjectile slash = new SwordAuraProjectile(level, entity);
        slash.setPos(entity.getEyePosition());
        slash.shoot(entity.getLookAngle());
        slash.setDamage(getDamage(spellLevel, entity));
        level.addFreshEntity(slash);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public float getDamage(int spellLevel, LivingEntity entity) {
        return(4.0F + this.getSpellPower(spellLevel, entity) + getAdditionalDamage(entity));
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.PREPARE_CROSS_ARMS;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.SLASH_ANIMATION;
    }
}
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
import net.chixozhmix.combat_skills.entity.slicing_vortex.SlicingVortexEntity;
import net.chixozhmix.combat_skills.registry.CsEntityRegistry;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoSpellConfig
public class SlicingVortexSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "slicing_vortex");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(60)
            .build();

    public SlicingVortexSkill() {
        this.baseStaminaCost = 70;
        this.castTime = 20;
        this.staminaCostPerLevel = 5;
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
        return SkillAnimations.JUTSU;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(CsTags.SWORDS, CsTags.STAFFS, CsTags.HAMMERS,
                        CsTags.AXES, CsTags.SCYTHE, CsTags.MACES)).withStyle(ChatFormatting.GOLD)
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.STAFFS) || SkillUtils.holdItem(entity, CsTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.HAMMERS) ||
                SkillUtils.holdItem(entity, CsTags.AXES) || SkillUtils.holdItem(entity, CsTags.SCYTHE) || SkillUtils.holdItem(entity, CsTags.MACES)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SlicingVortexEntity vortex = new SlicingVortexEntity(CsEntityRegistry.SLICING_VORTEX_ENTITY.get(), level);
        vortex.moveTo(entity.position());
        vortex.setOwner(entity);
        vortex.setRadius(this.getWeaponRange(entity) + 2);
        vortex.setDamage(getDamage(spellLevel, entity));
        vortex.setDuration(200);
        level.addFreshEntity(vortex);
        entity.addEffect(new MobEffectInstance(CsMobEffectRegistry.SLICING_VORTEX.get(), 200, 0, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, false, false));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

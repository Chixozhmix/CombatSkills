package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.api.skills.SkillAnimations;
import net.chixozhmix.combat_skills.api.utils.RaycastBuilder;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.registry.CsSoundRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class HookingSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "hooking");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(25)
            .build();

    public HookingSkill() {
        this.staminaCostPerLevel = 5;
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
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.HOOKING_ATTACK;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.SWORDS, CsTags.SCYTHE, CsTags.DAGGERS))
                        .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(CsSoundRegistry.HOOKING.get());
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.DAGGERS) || SkillUtils.holdItem(entity, ItemTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.SCYTHE)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float range = this.getWeaponRange(entity);
        Entity target;
        HitResult result = RaycastBuilder.begin(entity.level(), entity).range(range).checkForBlocks(true).bbInflation(0.35f).build();

        if(result instanceof EntityHitResult entityHitResult) {
            target = entityHitResult.getEntity();

            if(target instanceof LivingEntity livingEntity) {

                if (!livingEntity.isAlive() || !entity.isAlive()) {
                    return;
                }

                DamageSources.applyDamage(livingEntity, getDamage(spellLevel, entity), getDamageSource(livingEntity, entity));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 0));
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

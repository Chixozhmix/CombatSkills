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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RuthlessUppercutSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "ruthless_uppercut");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(45)
            .build();

    public RuthlessUppercutSkill() {
        this.baseStaminaCost = 45;
        this.castTime = 0;
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 10;
        this.skillPowerPerLevel = 2;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(CsSoundRegistry.CRUSHING_BLOW.get());
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
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.AXES, CsTags.HAMMERS, CsTags.MACES)).withStyle(ChatFormatting.GOLD)
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, ItemTags.AXES) || SkillUtils.holdItem(entity, CsTags.HAMMERS) || SkillUtils.holdItem(entity, CsTags.MACES)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.RUTHLESS_UPPERCUT;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float range = this.getWeaponRange(entity);
        Entity target;
        HitResult result = RaycastBuilder.begin(entity.level(), entity).range(range).checkForBlocks(true).bbInflation(0.35f).build();

        if(result instanceof EntityHitResult entityHitResult) {
            target = entityHitResult.getEntity();

            if(target instanceof LivingEntity livingEntity) {

                Vec3 velocity = livingEntity.getDeltaMovement();
                if (!livingEntity.isAlive() || !entity.isAlive() || entity.distanceTo(livingEntity) > range) {
                    return;
                }

                DamageSources.applyDamage(livingEntity, getDamage(spellLevel, entity), getDamageSource(livingEntity, entity));
                livingEntity.setDeltaMovement(velocity.x, this.getPower(spellLevel, entity), velocity.z);
                livingEntity.hurtMarked = true;
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private double getPower(int spellLevel, LivingEntity entity) {
        return (double)((1.0D * spellLevel) * 0.5);
    }
}

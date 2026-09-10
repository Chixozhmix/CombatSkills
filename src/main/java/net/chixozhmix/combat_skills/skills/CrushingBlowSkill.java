package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.chixozhmix.chilib.events.TickHelper;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
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
public class CrushingBlowSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "crushing_blow");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(35)
            .build();

    public CrushingBlowSkill() {
        this.baseStaminaCost = 30;
        this.castTime = 15;
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 8;
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
        return SpellAnimations.PREPARE_CROSS_ARMS;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.AXES, ItemTags.SWORDS, CsTags.HAMMERS, CsTags.MACES))
                        .withStyle(ChatFormatting.GOLD)
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, ItemTags.AXES) || SkillUtils.holdItem(entity, ItemTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.HAMMERS)
                || SkillUtils.holdItem(entity, CsTags.MACES)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
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
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        float range = this.getWeaponRange(entity);
        Entity target;
        HitResult result = RaycastBuilder.begin(entity.level(), entity).range(range).checkForBlocks(true).bbInflation(0.35f).build();

        if(result instanceof EntityHitResult entityHitResult) {
            target = entityHitResult.getEntity();

            if(target instanceof LivingEntity livingEntity) {

                Vec3 view = entity.getViewVector(1.0f);
                TickHelper.runLater(level, 15, () -> {
                    if (!livingEntity.isAlive() || !entity.isAlive()) {
                        return;
                    }

                    DamageSources.applyDamage(livingEntity, getDamage(spellLevel, entity), getDamageSource(livingEntity, entity));
                    livingEntity.knockback(2.0d, -view.x, -view.z);
                    livingEntity.hurtMarked = true;
                });
            }
        }

        entity.level().playSound(null, entity.blockPosition(), CsSoundRegistry.CRUSHING_BLOW.get(), entity.getSoundSource(), 2.0f, 0.8f);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

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
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@AutoSpellConfig
public class ShieldStrikeSkill extends AbstractCombatSkill {
    private static final Random RANDOM = new Random();
    public static final TagKey<EntityType<?>> BOSSES = ForgeRegistries.ENTITY_TYPES.tags()
            .createTagKey(new ResourceLocation("forge", "bosses"));

    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "shield_strike");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(45)
            .build();

    public ShieldStrikeSkill() {
        this.baseStaminaCost = 30;
        this.castTime = 15;
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 2;
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
        return CastType.INSTANT;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.SHIELD_STRIKE;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.SHIELD_BLOCK);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamage(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(CsTags.SHIELDS))
                        .withStyle(ChatFormatting.GOLD)
        );
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
        float range = this.getWeaponRange(entity);
        Entity target;
        HitResult result = RaycastBuilder.begin(entity.level(), entity).range(range).checkForBlocks(true).bbInflation(0.35f).build();

        if(result instanceof EntityHitResult entityHitResult) {
            target = entityHitResult.getEntity();

            if(target instanceof LivingEntity livingEntity && !target.getType().is(BOSSES)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, this.getDuration(spellLevel, entity)));

                if(RANDOM.nextFloat() <= 0.2f) {
                    livingEntity.addEffect(new MobEffectInstance(CsMobEffectRegistry.STUNNING.get(), 100));
                }

                DamageSources.applyDamage(livingEntity, getDamage(spellLevel, entity), getDamageSource(livingEntity, entity));
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public float getDamage(int spellLevel, LivingEntity entity) {
        return (spellLevel + 2) * baseSkillPower;
    }

    private int getDuration(int spellLevel, Entity caster) {
        return (int) ((100.0F * spellLevel) * 2);
    }
}

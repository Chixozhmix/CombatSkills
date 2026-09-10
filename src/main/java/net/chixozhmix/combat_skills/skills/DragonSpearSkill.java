package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.chixozhmix.chilib.utils.SpellUtils;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.api.skills.SkillAnimations;
import net.chixozhmix.combat_skills.entity.DragonSpearQuakeEntity;
import net.chixozhmix.combat_skills.registry.CsSchoolRegistry;
import net.chixozhmix.combat_skills.registry.CsSoundRegistry;
import net.chixozhmix.combat_skills.util.CsTags;
import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DragonSpearSkill extends AbstractCombatSkill {
    private final ResourceLocation skillId = new ResourceLocation(CombatSkills.MODID, "dragon_spear");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(40)
            .build();

    public DragonSpearSkill() {
        this.baseStaminaCost = 60;
        this.castTime = 15;
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
        return SkillAnimations.DRAGON_SPEAR_START;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SkillAnimations.DRAGON_SPEAR_END;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(CsSoundRegistry.DRAGON_SPEAR_CAST.get());
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(CsTags.SPEARS)).withStyle(ChatFormatting.GOLD)
        );
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if(playerMagicData != null && playerMagicData.getCastDurationRemaining() <= 30 && playerMagicData.getCastDurationRemaining() >= 1)
            SpellUtils.applyHovering(entity, 7.0F, 0.5f, 0.1, true);


        super.onServerCastTick(level, spellLevel, entity, playerMagicData);
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.SPEARS)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.setDeltaMovement(entity.getDeltaMovement().x, -3.5D, entity.getDeltaMovement().z);

        entity.hasImpulse = true;
        entity.hurtMarked = true;
        entity.fallDistance = 0.0F;

        BlockPos groundPos = entity.blockPosition();

        while (groundPos.getY() > level.getMinBuildHeight() && !level.getBlockState(groundPos.below()).isSolid()) {
            groundPos = groundPos.below();
        }

        Vec3 spawn = new Vec3(entity.getX(), groundPos.getY() + 0.5D, entity.getZ());

        int duration = 15;
        DragonSpearQuakeEntity aoeEntity = new DragonSpearQuakeEntity(level);
        aoeEntity.moveTo(spawn);
        aoeEntity.setOwner(entity);
        aoeEntity.setCircular();
        aoeEntity.setRadius(6);
        aoeEntity.setDuration(duration);
        aoeEntity.setDamage(this.getDamage(spellLevel, entity));
        aoeEntity.setSlownessAmplifier(1);
        level.addFreshEntity(aoeEntity);

        AABB damage_area = new AABB(aoeEntity.blockPosition()).inflate(6);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damage_area);

        for (LivingEntity target : entities) {
            if(target == entity) continue;
            DamageSources.applyDamage(target, getDamage(spellLevel, entity), getDamageSource(entity));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}

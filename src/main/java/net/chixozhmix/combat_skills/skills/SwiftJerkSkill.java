package net.chixozhmix.combat_skills.skills;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
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
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SwiftJerkSkill extends AbstractCombatSkill {
    private final ResourceLocation spellId = new ResourceLocation(CombatSkills.MODID, "swift_jerk");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(CsSchoolRegistry.SWORD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(15)
            .build();

    public SwiftJerkSkill() {
        this.staminaCostPerLevel = 5;
        this.baseSkillPower = 5;
        this.skillPowerPerLevel = 5;
        this.castTime = 0;
        this.baseStaminaCost = 35;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
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
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.combat_skills.skill_component", getTagNames(ItemTags.SWORDS, CsTags.SPEARS, CsTags.SCYTHE))
                        .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(CsSoundRegistry.SWIFT_JERK.get());
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (SkillUtils.holdItem(entity, CsTags.SPEARS) || SkillUtils.holdItem(entity, ItemTags.SWORDS) || SkillUtils.holdItem(entity, CsTags.SCYTHE)) {
            return true;
        }

        if (entity instanceof Player player) {
            SkillUtils.lossWaponMessage(player);
        }

        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.hasImpulse = true;
        float multiplier = (15.0F + this.getSpellPower(spellLevel, entity)) / 12.0F;
        Vec3 forward = entity.getLookAngle();
        if (playerMagicData.getAdditionalCastData() instanceof SwiftJerkSkill.SwiftJerkDirectionOverrideCastData) {
            if (Utils.random.nextBoolean()) {
                forward = forward.yRot(90.0F);
            } else {
                forward = forward.yRot(-90.0F);
            }
        }

        Vec3 vec = forward.multiply((double)3.0F, (double)1.0F, (double)3.0F).normalize().add((double)0.0F, (double)0.25F, (double)0.0F).scale((double)multiplier);
        if (entity.onGround()) {
            entity.setPos(entity.position().add((double)0.0F, (double)1.5F, (double)0.0F));
            vec.add((double)0.0F, (double)0.25F, (double)0.0F);
        }

        playerMagicData.setAdditionalCastData(new ImpulseCastData((float)vec.x, (float)vec.y, (float)vec.z, true));
        entity.setDeltaMovement(new Vec3(Mth.lerp((double)0.75F, entity.getDeltaMovement().x, vec.x), Mth.lerp((double)0.75F, entity.getDeltaMovement().y, vec.y), Mth.lerp((double)0.75F, entity.getDeltaMovement().z, vec.z)));
        entity.addEffect(new MobEffectInstance((MobEffect) CsMobEffectRegistry.SWIFT_JERK_EFFECT.get(), 15, (int)(this.getDamage(spellLevel, entity)), false, false, false));
        entity.invulnerableTime = 20;
        playerMagicData.getSyncedData();

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public float getDamage(int spellLevel, LivingEntity caster) {
        return(2.0F + this.getSpellPower(spellLevel, caster) + getAdditionalDamage(caster));
    }

    public static class SwiftJerkDirectionOverrideCastData implements ICastData {
        public void reset() {
        }
    }
}

package net.chixozhmix.combat_skills.api.skills;

import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.network.casting.OnClientCastPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.chixozhmix.combat_skills.registry.CsAttributeRegistry;
import net.funkpla.staminafortweakers.Exhaustible;
import net.funkpla.staminafortweakers.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCombatSkill extends AbstractSpell {

    protected int baseStaminaCost;
    protected int staminaCostPerLevel;
    protected int baseSkillPower;
    protected int skillPowerPerLevel;
    protected float baseRange = 3;

    @Override
    public int getManaCost(int level) {
        return getStaminaCost(level);
    }

    public int getStaminaCost(int level) {
        return (int)((double)(this.baseStaminaCost + this.staminaCostPerLevel * (level - 1)) * 1.0);
    }

    @Override
    public void castSpell(Level world, int spellLevel, ServerPlayer serverPlayer, CastSource castSource, boolean triggerCooldown) {
        MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
        PlayerRecasts playerRecasts = magicData.getPlayerRecasts();
        boolean playerAlreadyHasRecast = playerRecasts.hasRecastForSpell(this.getSpellId());
        SpellOnCastEvent event = new SpellOnCastEvent(serverPlayer, this.getSpellId(), spellLevel, this.getManaCost(spellLevel), this.getSchoolType(), castSource);
        MinecraftForge.EVENT_BUS.post(event);
        if (castSource.consumesMana() && !playerAlreadyHasRecast && !serverPlayer.isCreative()) {
            if (serverPlayer instanceof Exhaustible exhaustible) {
                exhaustible.depleteStamina(event.getManaCost());
            }
        }

        this.onCast(world, event.getSpellLevel(), serverPlayer, castSource, magicData);
        boolean playerHasRecastsLeft = playerRecasts.hasRecastForSpell(this.getSpellId());
        if (playerAlreadyHasRecast && playerHasRecastsLeft) {
            playerRecasts.decrementRecastCount(this.getSpellId());
        } else if (!playerHasRecastsLeft && triggerCooldown && (!serverPlayer.isCreative() || (Boolean)ServerConfigs.CREATIVE_COOLDOWN.get())) {
            MagicHelper.MAGIC_MANAGER.addCooldown(serverPlayer, this, castSource);
        }

        PacketDistributor.sendToPlayer(serverPlayer, new OnClientCastPacket(this.getSpellId(), spellLevel, castSource, magicData.getAdditionalCastData()));

    }

    @Override
    public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
        if ((Boolean)ServerConfigs.DISABLE_ADVENTURE_MODE_CASTING.get() && player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE) {
                return new CastResult(CastResult.Type.FAILURE, Component.translatable("ui.irons_spellbooks.cast_error_adventure").withStyle(ChatFormatting.RED));
            }
        }

        AttributeInstance stamina = player.getAttribute(Services.REGISTRY.getStaminaAttribute());

        double staminaValue = stamina != null ? stamina.getBaseValue() : 0;

        boolean hasEnoughStamina = staminaValue >= getManaCost(spellLevel);
        boolean isSpellOnCooldown = playerMagicData.getPlayerCooldowns().isOnCooldown(this);
        boolean hasRecastForSpell = playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId());
        if (this.requiresLearning() && !this.isLearned(player)) {
            return new CastResult(CastResult.Type.FAILURE, Component.translatable("ui.irons_spellbooks.cast_error_unlearned").withStyle(ChatFormatting.RED));
        } else if (castSource == CastSource.SCROLL && this.getRecastCount(spellLevel, player) > 0) {
            return new CastResult(CastResult.Type.FAILURE, Component.translatable("ui.irons_spellbooks.cast_error_scroll", new Object[]{this.getDisplayName(player)}).withStyle(ChatFormatting.RED));
        } else if (castSource != CastSource.SPELLBOOK && castSource != CastSource.SWORD || !isSpellOnCooldown || player.isCreative() && !(Boolean)ServerConfigs.CREATIVE_COOLDOWN.get()) {
            return hasRecastForSpell || !castSource.consumesMana() || hasEnoughStamina || player.isCreative() && !(Boolean)ServerConfigs.CREATIVE_MANA_COST.get() ? new CastResult(CastResult.Type.SUCCESS) : new CastResult(CastResult.Type.FAILURE, Component.translatable("ui.combat_skills.cast_error_stamina", new Object[]{this.getDisplayName(player)}).withStyle(ChatFormatting.RED));
        } else {
            return new CastResult(CastResult.Type.FAILURE, Component.translatable("ui.irons_spellbooks.cast_error_cooldown", new Object[]{this.getDisplayName(player)}).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public float getSpellPower(int skillLevel, @Nullable Entity sourceEntity) {
        double entitySkillPowerModifier = (double)1.0F;
        double entitySchoolPowerModifier = (double)1.0F;
        float configPowerModifier = 1.0f;
        if (sourceEntity instanceof LivingEntity livingEntity) {
            entitySkillPowerModifier = (double)((float)livingEntity.getAttributeValue((Attribute) CsAttributeRegistry.SKILL_POWER.get()));
            entitySchoolPowerModifier = this.getSchoolType().getPowerFor(livingEntity);
        }

        return (float)((double)(this.baseSkillPower + this.skillPowerPerLevel * (skillLevel - 1)) * entitySkillPowerModifier * entitySchoolPowerModifier * (double)configPowerModifier);

    }

    @Override
    public float getEntityPowerMultiplier(@Nullable LivingEntity entity) {
        float base = 1.0f;
        if (entity == null) {
            return base;
        } else {
            float entitySkillPowerModifier = (float)entity.getAttributeValue((Attribute) CsAttributeRegistry.SKILL_POWER.get());
            double entitySchoolPowerModifier = this.getSchoolType().getPowerFor(entity);
            return (float)((double)(base * entitySkillPowerModifier) * entitySchoolPowerModifier);
        }
    }

    public float getAdditionalDamage(LivingEntity entity) {
        ItemStack weapon = entity.getMainHandItem();

        if (weapon.isEmpty()) {
            return 0;
        }

        float damage = 0;

        Multimap<Attribute, AttributeModifier> modifiers =
                weapon.getAttributeModifiers(EquipmentSlot.MAINHAND);

        for (AttributeModifier modifier : modifiers.get(Attributes.ATTACK_DAMAGE)) {
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                damage += modifier.getAmount();
            }
        }

        return damage;
    }

    public String getDamageText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float additionalDamage = getAdditionalDamage(entity);
            String plus = "";
            if (additionalDamage > 0) {
                plus = String.format(" (+%s)", Utils.stringTruncation(additionalDamage, 1));
            }
            String damage = Utils.stringTruncation(getDamage(spellLevel, entity), 1);
            return damage + plus;
        }
        return "" + Utils.stringTruncation(getSpellPower(spellLevel, entity), 1);
    }

    public float getDamage(int spellLevel, LivingEntity entity) {
        float spellPower = getSpellPower(spellLevel, entity);
        float additionalDamage = getAdditionalDamage(entity);
        return spellPower + additionalDamage;
    }

    public float getWeaponRange(LivingEntity entity) {
        if(entity == null)
            return baseRange;

        AttributeInstance attackRange = entity.getAttribute(ForgeMod.ENTITY_REACH.get());

        if(attackRange != null)
            return (float) attackRange.getValue();

        return baseRange;
    }

    private MutableComponent getTagName(TagKey<Item> tag) {
        return Component.translatable(tag.location().toLanguageKey());
    }

    @SafeVarargs
    public final MutableComponent getTagNames(TagKey<Item>... tags) {
        MutableComponent result = Component.empty();

        for (int i = 0; i < tags.length; i++) {
            if (i > 0) {
                result.append(", ");
            }

            result.append(getTagName(tags[i]));
        }

        return result;
    }
}

package net.chixozhmix.combat_skills.effect;

import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ParryEffect extends MobEffect {
    public ParryEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xF0AE4D);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target.hasEffect(CsMobEffectRegistry.PARRY_EFFECT.get()) &&
                event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!isMeleeAttack(event.getSource())) {
                return;
            }

            attacker.knockback(2.0f, -target.getX(), -target.getZ());

            if (target.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SHIELD_BLOCK, target.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    private static boolean isMeleeAttack(DamageSource source) {
        return source.is(DamageTypes.MOB_ATTACK) ||
                source.is(DamageTypes.PLAYER_ATTACK) ||
                source.is(DamageTypes.MOB_ATTACK_NO_AGGRO);
    }
}

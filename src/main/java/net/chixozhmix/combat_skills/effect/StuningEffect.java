package net.chixozhmix.combat_skills.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class StuningEffect extends MobEffect {
    public static final TagKey<EntityType<?>> BOSSES =
            ForgeRegistries.ENTITY_TYPES.tags()
                    .createTagKey(new ResourceLocation("forge", "bosses"));

    public StuningEffect() {
        super(MobEffectCategory.HARMFUL, 0x5C1515);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide && pLivingEntity instanceof Mob mob && !mob.getType().is(BOSSES)) {

            mob.setTarget(null);
            mob.getNavigation().stop();

            mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}

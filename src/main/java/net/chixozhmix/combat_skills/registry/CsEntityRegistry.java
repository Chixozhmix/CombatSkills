package net.chixozhmix.combat_skills.registry;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.entity.DamageAoe;
import net.chixozhmix.combat_skills.entity.DragonSpearQuakeEntity;
import net.chixozhmix.combat_skills.entity.sword_aura.SwordAuraProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CsEntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CombatSkills.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final RegistryObject<EntityType<SwordAuraProjectile>> SWORD_AURA_PROJECTILE =
            ENTITIES.register("sword_aura", () -> EntityType.Builder.<SwordAuraProjectile>of(SwordAuraProjectile::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(CombatSkills.MODID, "sword_aura").toString()));

    public static final RegistryObject<EntityType<DamageAoe>> DAMAGE_AOE =
        ENTITIES.register("damage_aoe", () -> EntityType.Builder.<DamageAoe>of(DamageAoe::new, MobCategory.MISC)
                        .sized(4f, .8f)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation(CombatSkills.MODID, "damage_aoe").toString()));

    public static final RegistryObject<EntityType<DragonSpearQuakeEntity>> DRAGON_SPEAR_AOE =
            ENTITIES.register("dragon_spear_aoe", () -> EntityType.Builder.<DragonSpearQuakeEntity>of(DragonSpearQuakeEntity::new, MobCategory.MISC)
                    .sized(4f, .8f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(CombatSkills.MODID, "dragon_spear_aoe").toString()));
}

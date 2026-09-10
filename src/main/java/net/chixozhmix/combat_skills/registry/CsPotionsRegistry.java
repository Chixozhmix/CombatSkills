package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//@Mod.EventBusSubscriber
public class CsPotionsRegistry {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, CombatSkills.MODID);

    public static final RegistryObject<Potion> STAMINA_POTION_ONE = POTIONS.register("stamina_potion_one", () ->
            new Potion(new MobEffectInstance(CsMobEffectRegistry.INSTANT_STAMINA.get())));
    public static final RegistryObject<Potion> STAMINA_POTION_TWO = POTIONS.register("stamina_potion_two", () ->
            new Potion(new MobEffectInstance(CsMobEffectRegistry.INSTANT_STAMINA.get(), 0, 1)));
    public static final RegistryObject<Potion> STAMINA_POTION_THREE = POTIONS.register("stamina_potion_three", () ->
            new Potion(new MobEffectInstance(CsMobEffectRegistry.INSTANT_STAMINA.get(), 0, 2)));


//    public static void addRecipes(FMLCommonSetupEvent event) {
//        event.enqueueWork(() -> {
//            PotionBrewing.addMix(Potions.AWKWARD, (Item) ItemRegistry.ARCANE_ESSENCE.get(), (Potion)STAMINA_POTION_ONE.get());
//        });
//    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
        //eventBus.addListener(PotionRegistry::addRecipes);
    }
}

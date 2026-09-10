package net.chixozhmix.combat_skills.registry;

import net.chixozhmix.chilib.registers.CLBrewingRecipeRegister;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;

public class CSRecipeRegister {
    public static void registerBrewingRecipes() {
        CLBrewingRecipeRegister.register(Potions.MUNDANE, Items.SUGAR, CsPotionsRegistry.STAMINA_POTION_ONE.get());
        CLBrewingRecipeRegister.register(CsPotionsRegistry.STAMINA_POTION_ONE.get(), Items.GOLDEN_CARROT, CsPotionsRegistry.STAMINA_POTION_TWO.get());
        CLBrewingRecipeRegister.register(CsPotionsRegistry.STAMINA_POTION_ONE.get(), Items.GLOWSTONE_DUST, CsPotionsRegistry.STAMINA_POTION_THREE.get());
    }
}

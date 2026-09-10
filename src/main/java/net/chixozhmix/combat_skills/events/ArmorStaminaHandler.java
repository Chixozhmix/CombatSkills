package net.chixozhmix.combat_skills.events;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.funkpla.staminafortweakers.registry.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class ArmorStaminaHandler {
    private static final UUID HEAD_UUID =
            UUID.fromString("04148e9b-3dca-4c60-93b3-a0e650c0882d");

    private static final UUID CHEST_UUID =
            UUID.fromString("04148e9b-3dca-4c60-93b3-a0e650c0881d");

    private static final UUID LEGS_UUID =
            UUID.fromString("04148e9b-3dca-4c60-93b3-a0e650c0880d");

    private static final UUID FEET_UUID =
            UUID.fromString("04148e9b-3dca-4c60-93b3-a0e650c0809d");

    private static final float LEATHER_AMP = 25f;
    private static final float IRON_AMP = 50f;
    private static final float DIAMOND_AMP = 75f;
    private static final float NETHERITE_AMP = 100f;

    @SubscribeEvent
    public static void onItemAttribute(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item armor = itemStack.getItem();

        if (event.getSlotType() == EquipmentSlot.HEAD) {
            if(armor == Items.LEATHER_HELMET)
                staminaSet(event, LEATHER_AMP, HEAD_UUID);
            if(armor == Items.CHAINMAIL_HELMET)
                staminaSet(event, LEATHER_AMP, HEAD_UUID);
            if(armor == Items.IRON_HELMET)
                staminaSet(event, IRON_AMP, HEAD_UUID);
            if(armor == Items.GOLDEN_HELMET)
                staminaSet(event, IRON_AMP, HEAD_UUID);
            if (armor == Items.DIAMOND_HELMET)
               staminaSet(event, DIAMOND_AMP, HEAD_UUID);
            if (armor == Items.NETHERITE_HELMET)
                staminaSet(event, NETHERITE_AMP, HEAD_UUID);
            if (armor == ItemRegistry.NETHERITE_MAGE_HELMET.get())
                staminaSet(event, NETHERITE_AMP, HEAD_UUID);

        }
        if (event.getSlotType() == EquipmentSlot.CHEST) {
            if(armor == Items.LEATHER_CHESTPLATE)
                staminaSet(event, LEATHER_AMP, CHEST_UUID);
            if(armor == Items.CHAINMAIL_CHESTPLATE)
                staminaSet(event, LEATHER_AMP, CHEST_UUID);
            if(armor == Items.IRON_CHESTPLATE)
                staminaSet(event, IRON_AMP, CHEST_UUID);
            if(armor == Items.GOLDEN_CHESTPLATE)
                staminaSet(event, IRON_AMP, CHEST_UUID);
            if (armor == Items.DIAMOND_CHESTPLATE)
                staminaSet(event, DIAMOND_AMP, CHEST_UUID);
            if (armor == Items.NETHERITE_CHESTPLATE)
                staminaSet(event, NETHERITE_AMP, CHEST_UUID);
            if (armor == ItemRegistry.NETHERITE_MAGE_CHESTPLATE.get())
                staminaSet(event, NETHERITE_AMP, CHEST_UUID);
        }
        if (event.getSlotType() == EquipmentSlot.LEGS) {
            if(armor == Items.LEATHER_LEGGINGS)
                staminaSet(event, LEATHER_AMP, LEGS_UUID);
            if(armor == Items.CHAINMAIL_LEGGINGS)
                staminaSet(event, LEATHER_AMP, LEGS_UUID);
            if(armor == Items.IRON_LEGGINGS)
                staminaSet(event, IRON_AMP, LEGS_UUID);
            if(armor == Items.GOLDEN_LEGGINGS)
                staminaSet(event, IRON_AMP, LEGS_UUID);
            if (armor == Items.DIAMOND_LEGGINGS)
                staminaSet(event, DIAMOND_AMP, LEGS_UUID);
            if (armor == Items.NETHERITE_LEGGINGS)
                staminaSet(event, NETHERITE_AMP, LEGS_UUID);
            if (armor == ItemRegistry.NETHERITE_MAGE_LEGGINGS.get())
                staminaSet(event, NETHERITE_AMP, LEGS_UUID);
        }
        if (event.getSlotType() == EquipmentSlot.FEET) {
            if(armor == Items.LEATHER_BOOTS)
                staminaSet(event, LEATHER_AMP, FEET_UUID);
            if(armor == Items.CHAINMAIL_BOOTS)
                staminaSet(event, LEATHER_AMP, FEET_UUID);
            if(armor == Items.IRON_BOOTS)
                staminaSet(event, IRON_AMP, FEET_UUID);
            if(armor == Items.GOLDEN_BOOTS)
                staminaSet(event, IRON_AMP, FEET_UUID);
            if (armor == Items.DIAMOND_BOOTS)
                staminaSet(event, DIAMOND_AMP, FEET_UUID);
            if (armor == Items.NETHERITE_BOOTS)
                staminaSet(event, NETHERITE_AMP, FEET_UUID);
            if (armor == ItemRegistry.NETHERITE_MAGE_BOOTS.get())
                staminaSet(event, NETHERITE_AMP, FEET_UUID);
        }

    }

    private static void staminaSet(ItemAttributeModifierEvent event, float amplifier, UUID uuid) {
        event.addModifier(Attributes.MAX_STAMINA, new AttributeModifier(uuid, "Stamina Modifier", amplifier, AttributeModifier.Operation.ADDITION));
    }
}

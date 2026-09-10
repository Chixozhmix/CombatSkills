package net.chixozhmix.combat_skills.util;

import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CsTags {

    // Blossom School Focus
    public static final TagKey<Item> SWORD_FOCUS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "sword_focus"));

    //Weapons tags
    public static final TagKey<Item> SPEARS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "spears"));
    public static final TagKey<Item> DAGGERS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "daggers"));
    public static final TagKey<Item> HAMMERS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "hammers"));
    public static final TagKey<Item> MACES = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "maces"));
    public static final TagKey<Item> STAFFS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "staffs"));
    public static final TagKey<Item> SCYTHE = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "scythes"));

    public static final TagKey<Item> BOWS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "bows"));
    public static final TagKey<Item> SHIELDS = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "shields"));

    public static final TagKey<Item> CUTTING = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "cutting"));
    public static final TagKey<Item> CRUSHING = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "crushing"));
    public static final TagKey<Item> STABBING = ItemTags.create(new ResourceLocation(CombatSkills.MODID, "stabbing"));



}

package net.chixozhmix.combat_skills.entity.slicing_vortex;

import net.chixozhmix.chilib.utils.entity.geckolib.GeckoUtils;
import net.chixozhmix.combat_skills.CombatSkills;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SlicingVortexModel extends DefaultedEntityGeoModel<SlicingVortexEntity> {
    public static final ResourceLocation TEXTURE = GeckoUtils.textureLocation(CombatSkills.MODID, "slicing_vortex");
    public static final ResourceLocation GEO = GeckoUtils.geoLocation(CombatSkills.MODID, "vortex.geo");
    public static final ResourceLocation ANIMATION = GeckoUtils.animLocation(CombatSkills.MODID, "model.animation");

    public SlicingVortexModel() {
        super(CombatSkills.id("spellentitymob"));
    }

    @Override
    public ResourceLocation getTextureResource(SlicingVortexEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SlicingVortexEntity animatable) {
        return ANIMATION;
    }

    @Override
    public ResourceLocation getModelResource(SlicingVortexEntity animatable) {
        return GEO;
    }
}

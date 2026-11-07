package net.chixozhmix.combat_skills.entity.slicing_vortex;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SlicingVortexRenderer extends GeoEntityRenderer<SlicingVortexEntity> {
    public SlicingVortexRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SlicingVortexModel());
    }
}

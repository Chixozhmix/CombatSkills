package net.chixozhmix.combat_skills.screen;

import net.chixozhmix.chilib.utils.Overlays;
import net.chixozhmix.combat_skills.CombatSkills;
import net.chixozhmix.combat_skills.registry.CsMobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ScSpellEffectsOverlay implements IGuiOverlay {
    public static final ScSpellEffectsOverlay instance = new ScSpellEffectsOverlay();

    public final static ResourceLocation RAGE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CombatSkills.MODID, "textures/gui/overlays/rage_overlay.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator())
            return;

        Player player = Minecraft.getInstance().player;
        if(player == null)
            return;

        if(player.hasEffect(CsMobEffectRegistry.RAGE_EFFECT.get())) {
            Overlays.renderOverlayAdditive(guiGraphics, RAGE_TEXTURE, 0.3f, 0.0f, 0.0f, 0.25f, screenWidth, screenHeight);
        }
    }
}

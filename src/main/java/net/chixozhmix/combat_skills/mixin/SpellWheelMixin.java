package net.chixozhmix.combat_skills.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.chixozhmix.combat_skills.api.skills.AbstractCombatSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Objects;

@Mixin(SpellWheelOverlay.class)
public class SpellWheelMixin {
    @Shadow  public boolean active;
    @Shadow private SpellSelectionManager swsm;
    @Shadow private int wheelSelection;;
    @Shadow  private double ringOuterEdge;

    @Shadow public void close() {throw new AssertionError();}
    @Shadow private void drawRadialBackgrounds(BufferBuilder buffer, double centerX, double centerY, int selectedSpellIndex) {throw new AssertionError();}
    @Shadow private void drawDividingLines(BufferBuilder buffer, double centerX, double centerY) {throw new AssertionError();}
    @Shadow  private void drawTextBackground(GuiGraphics guiHelper, double centerX, double centerY, double textYOffset, int textCenterMargin, int textHeight){throw new AssertionError();}

    @Shadow @Final
    public static ResourceLocation TEXTURE;

    @Overwrite(remap = false)
    public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
        if (this.active) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && minecraft.screen == null && !minecraft.mouseHandler.isMouseGrabbed()) {
                this.swsm = ClientMagicData.getSpellSelectionManager();
                int totalSpellsAvailable = this.swsm.getSpellCount();
                if (totalSpellsAvailable <= 0) {
                    this.close();
                } else {
                    PoseStack poseStack = guiHelper.pose();
                    poseStack.pushPose();
                    int centerX = screenWidth / 2;
                    int centerY = screenHeight / 2;
                    Vec2 screenCenter = new Vec2((float)minecraft.getWindow().getScreenWidth() * 0.5F, (float)minecraft.getWindow().getScreenHeight() * 0.5F);
                    Vec2 mousePos = new Vec2((float)minecraft.mouseHandler.xpos(), (float)minecraft.mouseHandler.ypos());
                    double radiansPerSpell = Math.toRadians((double)(360.0F / (float)totalSpellsAvailable));
                    float mouseRotation = (Utils.getAngle(mousePos, screenCenter) + 1.57F + (float)radiansPerSpell * 0.5F) % 6.283F;
                    this.wheelSelection = (int) Mth.clamp((double)mouseRotation / radiansPerSpell, (double)0.0F, (double)(totalSpellsAvailable - 1));
                    if ((double)mousePos.distanceToSqr(screenCenter) < (double)4225.0F) {
                        this.wheelSelection = Math.max(0, this.swsm.getSelectionIndex());
                    }

                    guiHelper.fill(0, 0, screenWidth, screenHeight, 0);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder buffer = tesselator.getBuilder();
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    this.drawRadialBackgrounds(buffer, (double)centerX, (double)centerY, this.wheelSelection);
                    this.drawDividingLines(buffer, (double)centerX, (double)centerY);
                    tesselator.end();
                    RenderSystem.disableBlend();
                    SpellData selectedSpell = this.swsm.getSpellData(this.wheelSelection);
                    int spellLevel = selectedSpell.getSpell().getLevelFor(selectedSpell.getLevel(), player);
                    Font font = gui.getFont();
                    List<MutableComponent> info = selectedSpell.getSpell().getUniqueInfo(spellLevel, minecraft.player);
                    int var10000 = Math.max(3, info.size());
                    Objects.requireNonNull(font);
                    int textHeight = var10000 * 9 + 5;
                    int textCenterMargin = 5;
                    int textTitleMargin = 5;
                    AbstractSpell spell = selectedSpell.getSpell();
                    MutableComponent title = spell.getDisplayName(minecraft.player).withStyle(Style.EMPTY.withUnderlined(true));
                    MutableComponent level = Component.translatable("ui.irons_spellbooks.level", new Object[]{TooltipsUtils.getLevelComponenet(selectedSpell, player).withStyle(spell.getRarity(spellLevel).getDisplayName().getStyle())});
                    MutableComponent cost;

                    if (selectedSpell.getSpell() instanceof AbstractCombatSkill combatSkill) {
                        cost = Component.translatable(
                                "ui.combat_skills.stamina_cost",
                                combatSkill.getStaminaCost(spellLevel)
                        ).withStyle(ChatFormatting.AQUA);
                    } else {
                        cost = Component.translatable(
                                "ui.irons_spellbooks.mana_cost",
                                selectedSpell.getSpell().getManaCost(spellLevel)
                        ).withStyle(ChatFormatting.AQUA);
                    }
                    int cooldownTicks = MagicManager.getEffectiveSpellCooldown(spell, player, this.swsm.getSpellSlot(this.wheelSelection).getCastSource());
                    MutableComponent cooldownTime = Component.translatable("tooltip.irons_spellbooks.cooldown_length_seconds", new Object[]{Utils.timeFromTicks((float)cooldownTicks, 2)}).withStyle(ChatFormatting.YELLOW);
                    double var10002 = (double)centerX;
                    double var10003 = (double)centerY;
                    double var10004 = this.ringOuterEdge + (double)textHeight - (double)textTitleMargin;
                    Objects.requireNonNull(font);
                    var10004 -= (double)9.0F;
                    int var10006 = Math.max(2, info.size());
                    Objects.requireNonNull(font);
                    this.drawTextBackground(guiHelper, var10002, var10003, var10004, textCenterMargin, var10006 * 9);
                    guiHelper.drawString(font, title, centerX - font.width(title) / 2, (int)((double)centerY - (this.ringOuterEdge + (double)textHeight)), 16777215, true);
                    double var49 = (double)centerY - (this.ringOuterEdge + (double)textHeight);
                    Objects.requireNonNull(font);
                    int infoHeight = (int)(var49 + (double)9.0F + (double)textTitleMargin);
                    guiHelper.drawString(font, level, centerX - font.width(level) - textCenterMargin, infoHeight, 16777215, true);
                    if (spell.getManaCost(spellLevel) > 0) {
                        Objects.requireNonNull(font);
                        infoHeight += 9;
                        guiHelper.drawString(font, cost, centerX - font.width(cost) - textCenterMargin, infoHeight, 16777215, true);
                    }

                    if (cooldownTicks > 0) {
                        Objects.requireNonNull(font);
                        infoHeight += 9;
                        guiHelper.drawString(font, cooldownTime, centerX - font.width(cooldownTime) - textCenterMargin, infoHeight, 16777215, true);
                    }

                    for(int i = 0; i < info.size(); ++i) {
                        MutableComponent line = (MutableComponent)info.get(i);
                        int var50 = centerX + textCenterMargin;
                        var10004 = (double)centerY - ((double)80.0F + (double)textHeight);
                        Objects.requireNonNull(font);
                        guiHelper.drawString(font, line, var50, (int)(var10004 + (double)(9 * (i + 1)) + (double)textTitleMargin), 3924795, true);
                    }

                    float scale = Mth.lerp((float)totalSpellsAvailable / 15.0F, 2.0F, 1.25F) * 0.65F;
                    double radius = (double)(3.0F / scale) * (double)40.0F * (double)0.5F * (double)(0.85F + 0.25F * ((float)totalSpellsAvailable / 15.0F));
                    Vec2[] locations = new Vec2[totalSpellsAvailable];

                    for(int i = 0; i < locations.length; ++i) {
                        locations[i] = new Vec2((float)(Math.sin(radiansPerSpell * (double)i) * radius), (float)(-Math.cos(radiansPerSpell * (double)i) * radius));
                    }

                    for(int i = 0; i < locations.length; ++i) {
                        SpellData currentSpell = this.swsm.getSpellData(i);
                        if (currentSpell != null) {
                            ResourceLocation texture = currentSpell.getSpell().getSpellIconResource();
                            poseStack.pushPose();
                            poseStack.translate((float)centerX, (float)centerY, 0.0F);
                            poseStack.scale(scale, scale, scale);
                            int iconWidth = 8;
                            int borderWidth = 16;
                            int cdWidth = 8;
                            guiHelper.blit(texture, (int)locations[i].x - iconWidth, (int)locations[i].y - iconWidth, 0.0F, 0.0F, 16, 16, 16, 16);
                            guiHelper.blit(TEXTURE, (int)locations[i].x - borderWidth, (int)locations[i].y - borderWidth, this.swsm.getSelectionIndex() == i ? 32 : 0, 106, 32, 32);
                            float f = ClientMagicData.getCooldownPercent(currentSpell.getSpell());
                            if (f > 0.0F) {
                                RenderSystem.enableBlend();
                                int pixels = (int)(16.0F * f + 1.0F);
                                guiHelper.blit(TEXTURE, (int)locations[i].x - cdWidth, (int)locations[i].y + cdWidth - pixels, 47, 87, 16, pixels);
                            }

                            poseStack.popPose();
                        }
                    }

                    poseStack.popPose();
                }
            } else {
                this.close();
            }
        }
    }
}

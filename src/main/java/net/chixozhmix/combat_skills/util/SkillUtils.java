package net.chixozhmix.combat_skills.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class SkillUtils {

    public static boolean holdItem(LivingEntity entity, TagKey<Item> tagKey) {
        return  (entity.isHolding(stack -> stack.is(tagKey)));
    }

    public static boolean holdItem(LivingEntity entity, TagKey<Item> tagKey, @Nullable InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND)
            return entity.getMainHandItem().is(tagKey);
        if (hand == InteractionHand.OFF_HAND)
            return entity.getOffhandItem().is(tagKey);

        return holdItem(entity, tagKey);
    }

    public static void lossWaponMessage(Player player) {
        player.displayClientMessage(Component.translatable("ui.combat_skills.no_component").withStyle(ChatFormatting.RED), true);
    }

    //Raycast
    public static HitResult checkEntityIntersecting(Entity entity, Vec3 start, Vec3 end, float bbInflation) {
        if (entity.isMultipartEntity()) {
            for (PartEntity<?> p : entity.getParts()) {
                var hit = p == null ? null : p.getBoundingBox().inflate(bbInflation).clip(start, end).orElse(null);
                if (hit != null) {
                    return new EntityHitResult(entity, hit);
                }
            }
        } else {
            var hit = entity.getBoundingBox().inflate(bbInflation).clip(start, end).orElse(null);
            if (hit != null) {
                return new EntityHitResult(entity, hit);
            }
        }
        Vec3 vector = start.subtract(end);
        return BlockHitResult.miss(end, Direction.getNearest(vector.x, vector.y, vector.z), BlockPos.containing(end));
    }

    public static boolean canHitWithRaycast(Entity entity) {
        return entity.isPickable() && entity.isAlive() && !entity.isSpectator();
    }
}

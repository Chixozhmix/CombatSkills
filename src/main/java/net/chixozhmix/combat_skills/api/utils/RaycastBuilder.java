package net.chixozhmix.combat_skills.api.utils;

import net.chixozhmix.combat_skills.util.SkillUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class RaycastBuilder {
    private final Level level;
    private final Entity originEntity;
    private Vec3 start;
    private Vec3 end;
    private boolean checkForBlocks = false;
    private float bbInflation = 0;
    private Predicate<? super Entity> filter = SkillUtils::canHitWithRaycast;

    public RaycastBuilder(Level level, Entity originEntity) {
        this.level = level;
        this.originEntity = originEntity;
    }

    public static RaycastBuilder begin(Level level, Entity originEntity) {
        return new RaycastBuilder(level, originEntity);
    }

    public RaycastBuilder start(Vec3 start) {
        this.start = start;
        return this;
    }

    public RaycastBuilder end(Vec3 end) {
        this.end = end;
        return this;
    }

    public RaycastBuilder range(float distance) {
        if (start == null) {
            start = originEntity.getEyePosition();
        }
        this.end = originEntity.getLookAngle().normalize().scale(distance).add(start);
        return this;
    }

    public RaycastBuilder checkForBlocks(boolean checkForBlocks) {
        this.checkForBlocks = checkForBlocks;
        return this;
    }

    public RaycastBuilder bbInflation(float bbInflation) {
        this.bbInflation = bbInflation;
        return this;
    }

    public RaycastBuilder filter(Predicate<? super Entity> filter) {
        this.filter = filter;
        return this;
    }
    public HitResult build() {
        return performRaycast();
    }

    public HitResult performRaycast() {
        Objects.requireNonNull(start, "Start must be set to perform raycast");
        Objects.requireNonNull(end, "End must be set to perform raycast");

        BlockHitResult blockHitResult = null;
        Vec3 rayEnd = end;

        if (checkForBlocks) {
            blockHitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, originEntity));
            rayEnd = blockHitResult.getLocation();
        }

        AABB range = originEntity.getBoundingBox().expandTowards(rayEnd.subtract(start));
        List<HitResult> hits = new ArrayList<>();
        List<? extends Entity> entities = level.getEntities(originEntity, range, filter);

        for (Entity target : entities) {
            HitResult hit = SkillUtils.checkEntityIntersecting(target, start, rayEnd, bbInflation);
            if (hit.getType() != HitResult.Type.MISS) {
                hits.add(hit);
            }
        }

        if (!hits.isEmpty()) {
            hits.sort(Comparator.comparingDouble(o -> o.getLocation().distanceToSqr(start)));
            return hits.get(0);
        }
        if (checkForBlocks) {
            return blockHitResult;
        } else {
            return BlockHitResult.miss(rayEnd, Direction.UP, BlockPos.containing(rayEnd));
        }
    }
}

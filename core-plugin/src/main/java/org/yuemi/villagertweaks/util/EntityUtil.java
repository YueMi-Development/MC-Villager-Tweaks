package org.yuemi.villagertweaks.util;

import org.bukkit.entity.Entity;

public final class EntityUtil {

    private EntityUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Copies essential attributes, persistent data, and scoreboard tags from
     * the source entity to the target entity during transformation.
     *
     * @param source The source entity to copy traits from.
     * @param target The target entity to paste traits to.
     */
    public static void copyAttributes(Entity source, Entity target) {
        // Copy custom name and its visibility
        if (source.customName() != null) {
            target.customName(source.customName());
            target.setCustomNameVisible(source.isCustomNameVisible());
        }

        // Copy persistent data container keys
        source.getPersistentDataContainer().copyTo(target.getPersistentDataContainer(), true);

        // Copy scoreboard tags
        for (String tag : source.getScoreboardTags()) {
            target.addScoreboardTag(tag);
        }

        // Copy generic entity states
        target.setSilent(source.isSilent());
        target.setGravity(source.hasGravity());
        target.setGlowing(source.isGlowing());
        target.setFireTicks(source.getFireTicks());
        target.setFallDistance(source.getFallDistance());
        target.setTicksLived(source.getTicksLived());
        target.setVisualFire(source.isVisualFire());
        target.setInvulnerable(source.isInvulnerable());
    }
}

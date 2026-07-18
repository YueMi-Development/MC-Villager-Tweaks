package org.yuemi.villagertweaks.plugin.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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

        // Copy equipment if both are living entities
        if (source instanceof LivingEntity sourceLiving && target instanceof LivingEntity targetLiving) {
            var sourceEquip = sourceLiving.getEquipment();
            var targetEquip = targetLiving.getEquipment();
            if (sourceEquip != null && targetEquip != null) {
                targetEquip.setItemInMainHand(sourceEquip.getItemInMainHand());
                targetEquip.setItemInMainHandDropChance(sourceEquip.getItemInMainHandDropChance());
                targetEquip.setItemInOffHand(sourceEquip.getItemInOffHand());
                targetEquip.setItemInOffHandDropChance(sourceEquip.getItemInOffHandDropChance());
                targetEquip.setHelmet(sourceEquip.getHelmet());
                targetEquip.setHelmetDropChance(sourceEquip.getHelmetDropChance());
                targetEquip.setChestplate(sourceEquip.getChestplate());
                targetEquip.setChestplateDropChance(sourceEquip.getChestplateDropChance());
                targetEquip.setLeggings(sourceEquip.getLeggings());
                targetEquip.setLeggingsDropChance(sourceEquip.getLeggingsDropChance());
                targetEquip.setBoots(sourceEquip.getBoots());
                targetEquip.setBootsDropChance(sourceEquip.getBootsDropChance());
            }
        }
    }
}

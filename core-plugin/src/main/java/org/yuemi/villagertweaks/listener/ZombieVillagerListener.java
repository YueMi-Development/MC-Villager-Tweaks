package org.yuemi.villagertweaks.listener;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.yuemi.villagertweaks.VillagerTweaksPlugin;
import org.yuemi.villagertweaks.util.EntityUtil;

public final class ZombieVillagerListener implements Listener {

    private final VillagerTweaksPlugin plugin;
    private final Set<UUID> cancelledInfections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ZombieVillagerListener(VillagerTweaksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityTransform(EntityTransformEvent event) {
        var reason = event.getTransformReason();

        // Handle Curing Control
        if (reason == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntity() instanceof ZombieVillager) {
                var config = plugin.getConfig();
                if (!config.getBoolean("zombie-villagers.allow-curing", true)) {
                    event.setCancelled(true);
                }
            }
            return;
        }

        // Handle Infection Control
        if (reason == EntityTransformEvent.TransformReason.INFECTION) {
            if (!(event.getEntity() instanceof Villager villager)) {
                return;
            }

            var config = plugin.getConfig();
            String infectionType = config.getString("zombie-villagers.infection-type", "vanilla").toLowerCase();

            if (infectionType.equals("none")) {
                event.setCancelled(true);
                cancelledInfections.add(villager.getUniqueId());
            } else if (infectionType.equals("random")) {
                // Roll 50% chance
                if (Math.random() >= 0.5) {
                    event.setCancelled(true);
                    cancelledInfections.add(villager.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }

        UUID uuid = villager.getUniqueId();
        if (cancelledInfections.remove(uuid)) {
            // Infection was explicitly cancelled, let them die normally
            return;
        }

        var config = plugin.getConfig();
        String infectionType = config.getString("zombie-villagers.infection-type", "vanilla").toLowerCase();

        if (infectionType.equals("vanilla") || infectionType.equals("none")) {
            return;
        }

        // Check if killed by a Zombie
        var lastDamage = villager.getLastDamageCause();
        if (!(lastDamage instanceof EntityDamageByEntityEvent damageEvent)) {
            return;
        }

        var damager = damageEvent.getDamager();
        if (!(damager instanceof Zombie) && !(damager instanceof ZombieVillager)) {
            return;
        }

        // Force infect
        if (infectionType.equals("always-zombie")) {
            infectManually(villager, event);
        } else if (infectionType.equals("random")) {
            if (Math.random() < 0.5) {
                infectManually(villager, event);
            }
        }
    }

    private void infectManually(Villager villager, EntityDeathEvent event) {
        Location loc = villager.getLocation();
        var world = loc.getWorld();

        // Clear vanilla death drops & exp since entity is transforming
        event.getDrops().clear();
        event.setDroppedExp(0);

        // Spawn Zombie Villager
        var zombieVillager = (ZombieVillager) world.spawnEntity(loc, EntityType.ZOMBIE_VILLAGER);

        // Copy attributes, metadata, tags, PDC from source villager
        EntityUtil.copyAttributes(villager, zombieVillager);

        // Copy profession and biome type
        zombieVillager.setVillagerProfession(villager.getProfession());
        zombieVillager.setVillagerType(villager.getVillagerType());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ZombieVillager zombieVillager)) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItem(event.getHand());

        if (item.getType() != Material.GOLDEN_APPLE) {
            return;
        }

        var config = plugin.getConfig();
        if (!config.getBoolean("zombie-villagers.allow-curing", true)) {
            if (zombieVillager.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                event.setCancelled(true);
            }
        }
    }
}

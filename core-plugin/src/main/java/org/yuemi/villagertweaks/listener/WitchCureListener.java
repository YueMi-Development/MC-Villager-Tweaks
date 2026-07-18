package org.yuemi.villagertweaks.listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuemi.villagertweaks.VillagerTweaksPlugin;

public final class WitchCureListener implements Listener {

    private final VillagerTweaksPlugin plugin;
    private final Map<UUID, WitchCureTask> curingTasks = new ConcurrentHashMap<>();

    public WitchCureListener(VillagerTweaksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Witch witch)) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItem(event.getHand());

        if (item.getType() != Material.GOLDEN_APPLE) {
            return;
        }

        var config = plugin.getConfig();
        if (!config.getBoolean("enable-witch-curing", true)) {
            return;
        }

        if (curingTasks.containsKey(witch.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!witch.hasPotionEffect(PotionEffectType.WEAKNESS)) {
            return;
        }

        event.setCancelled(true);

        if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        witch.removePotionEffect(PotionEffectType.WEAKNESS);
        startCuring(witch);
    }

    private void startCuring(Witch witch) {
        var witchId = witch.getUniqueId();
        var task = new WitchCureTask(witch);
        curingTasks.put(witchId, task);
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Witch witch) {
            cancelCure(witch.getUniqueId());
        }
    }

    /**
     * Cancels all active curing tasks.
     */
    public void cancelAll() {
        for (var task : curingTasks.values()) {
            task.cancel();
        }
        curingTasks.clear();
    }

    private void cancelCure(UUID witchId) {
        var task = curingTasks.remove(witchId);
        if (task != null) {
            task.cancel();
        }
    }

    private final class WitchCureTask extends BukkitRunnable {
        private final Witch witch;
        private int secondsRemaining = 60;

        public WitchCureTask(Witch witch) {
            this.witch = witch;
        }

        @Override
        public void run() {
            if (!witch.isValid() || witch.isDead()) {
                cancelCure(witch.getUniqueId());
                return;
            }

            var loc = witch.getLocation();
            loc.getWorld().spawnParticle(Particle.WITCH, loc.clone().add(0, 1, 0), 5, 0.25, 0.5, 0.25, 0.05);

            if (secondsRemaining % 10 == 0 || secondsRemaining == 60) {
                loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 0.8f + (float) Math.random() * 0.4f);
            }

            secondsRemaining--;

            if (secondsRemaining <= 0) {
                convert(witch);
                cancelCure(witch.getUniqueId());
            }
        }
    }

    private void convert(Witch witch) {
        var loc = witch.getLocation();
        var world = loc.getWorld();

        var villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);

        if (witch.customName() != null) {
            villager.customName(witch.customName());
            villager.setCustomNameVisible(witch.isCustomNameVisible());
        }

        world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);
        world.spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.1);

        witch.remove();
    }
}

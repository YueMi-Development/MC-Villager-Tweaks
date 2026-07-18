package org.yuemi.villagertweaks;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuemi.villagertweaks.api.VillagerTweaksApi;
import org.yuemi.villagertweaks.api.VillagerTweaksApiProvider;
import org.yuemi.villagertweaks.commands.CommandHandler;
import org.yuemi.villagertweaks.config.ConfigManager;
import org.yuemi.villagertweaks.listener.WitchCureListener;
import org.yuemi.villagertweaks.listener.ZombieVillagerListener;
import org.yuemi.villagertweaks.bstats.BStatsService;

public final class VillagerTweaksPlugin extends JavaPlugin {

    private VillagerTweaksApi api;
    private WitchCureListener witchCureListener;
    private ZombieVillagerListener zombieVillagerListener;
    private boolean tweaksEnabled;

    @Override
    public void onEnable() {
        BStatsService.initialize(this);
        // Initialize and load configuration migrations
        new ConfigManager(this).loadAndMigrate();

        this.tweaksEnabled = getConfig().getBoolean("enable-tweaks", true);

        if (!tweaksEnabled) {
            getLogger().info("VillagerTweaks is disabled globally via configuration.");
            return;
        }

        this.api = new VillagerTweaksApiImpl();

        // Register to Bukkit ServicesManager
        getServer().getServicesManager().register(
                VillagerTweaksApi.class,
                this.api,
                this,
                ServicePriority.Normal
        );

        // Register to public provider entry point
        VillagerTweaksApiProvider.register(this.api);

        // Register gameplay listeners
        this.witchCureListener = new WitchCureListener(this);
        getServer().getPluginManager().registerEvents(witchCureListener, this);

        this.zombieVillagerListener = new ZombieVillagerListener(this);
        getServer().getPluginManager().registerEvents(zombieVillagerListener, this);

        // Register commands
        CommandHandler commandHandler = new CommandHandler(this);
        var cmd = getCommand("villagertweaks");
        if (cmd != null) {
            cmd.setExecutor(commandHandler);
            cmd.setTabCompleter(commandHandler);
        }

        getLogger().info("VillagerTweaks has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (!tweaksEnabled) {
            return;
        }

        // Cancel any active tasks
        if (this.witchCureListener != null) {
            this.witchCureListener.cancelAll();
        }

        // Unregister from services
        if (this.api != null) {
            getServer().getServicesManager().unregister(VillagerTweaksApi.class, this.api);
            VillagerTweaksApiProvider.unregister();
        }

        getLogger().info("VillagerTweaks has been disabled.");
    }

    /**
     * Reloads the plugin configuration.
     */
    public boolean reloadPlugin() {
        reloadConfig();
        boolean newTweaksEnabled = getConfig().getBoolean("enable-tweaks", true);
        boolean changed = newTweaksEnabled != this.tweaksEnabled;
        if (changed) {
            getLogger().warning("The 'enable-tweaks' setting has changed. A server restart is required to toggle plugin features globally.");
        }

        // Re-run configuration migrations if any upgrades occurred
        new ConfigManager(this).loadAndMigrate();
        return changed;
    }
}

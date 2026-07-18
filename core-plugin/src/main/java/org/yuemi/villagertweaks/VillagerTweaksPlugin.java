package org.yuemi.villagertweaks;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuemi.villagertweaks.api.VillagerTweaksApi;
import org.yuemi.villagertweaks.api.VillagerTweaksApiProvider;

public final class VillagerTweaksPlugin extends JavaPlugin {

    private VillagerTweaksApi api;

    @Override
    public void onEnable() {
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

        getLogger().info("VillagerTweaks has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Unregister from services
        getServer().getServicesManager().unregister(VillagerTweaksApi.class, this.api);

        // Unregister from provider
        VillagerTweaksApiProvider.unregister();

        getLogger().info("VillagerTweaks has been disabled.");
    }
}

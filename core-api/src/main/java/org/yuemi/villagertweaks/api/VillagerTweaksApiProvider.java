package org.yuemi.villagertweaks.api;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

/**
 * Entry point for accessing the VillagerTweaks API.
 */
public final class VillagerTweaksApiProvider {

    private static VillagerTweaksApi api;

    private VillagerTweaksApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets the active instance of the VillagerTweaks API.
     *
     * @return the VillagerTweaksApi instance, or null if the plugin is not loaded yet
     */
    public static @Nullable VillagerTweaksApi get() {
        if (api == null) {
            var registered = Bukkit.getServicesManager().load(VillagerTweaksApi.class);
            if (registered != null) {
                api = registered;
            }
        }
        return api;
    }

    /**
     * Registers the active instance of the VillagerTweaks API.
     *
     * @param instance the active API instance
     */
    public static void register(VillagerTweaksApi instance) {
        api = instance;
    }

    /**
     * Unregisters the active instance of the VillagerTweaks API.
     */
    public static void unregister() {
        api = null;
    }
}

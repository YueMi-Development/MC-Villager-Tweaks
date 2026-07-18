package org.yuemi.villagertweaks.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private final JavaPlugin plugin;
    private static final int LATEST_VERSION = 3;
    private static final String MIGRATION_PACKAGE = "org.yuemi.villagertweaks.config.migrations";

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads the plugin configuration and applies any pending migrations.
     */
    public void loadAndMigrate() {
        // Copies default config.yml from jar if it doesn't exist
        plugin.saveDefaultConfig();

        // Load current configuration
        FileConfiguration config = plugin.getConfig();
        int currentVersion = config.getInt("config-version", 1);
        int migratedVersion = currentVersion;

        plugin.getLogger().info("Current configuration version: " + currentVersion + " (Latest: " + LATEST_VERSION + ")");

        while (true) {
            int nextVersion = migratedVersion + 1;
            String migrationClassName = MIGRATION_PACKAGE + ".MigrationV" + migratedVersion + "ToV" + nextVersion;

            try {
                Class<?> clazz = Class.forName(migrationClassName);
                if (ConfigMigration.class.isAssignableFrom(clazz)) {
                    ConfigMigration migration = (ConfigMigration) clazz.getDeclaredConstructor().newInstance();
                    plugin.getLogger().info("Applying configuration migration from version " + migratedVersion + " to " + nextVersion + "...");
                    migration.migrate(config);
                    migratedVersion = nextVersion;
                    config.set("config-version", migratedVersion);
                } else {
                    plugin.getLogger().warning("Class " + migrationClassName + " does not implement ConfigMigration.");
                    break;
                }
            } catch (ClassNotFoundException e) {
                // No more migrations found for the next step, we are up-to-date
                break;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to apply configuration migration " + migrationClassName + ": " + e.getMessage());
                break;
            }
        }

        if (migratedVersion > currentVersion) {
            plugin.saveConfig();
            plugin.getLogger().info("Configuration successfully migrated to version " + migratedVersion + ".");
        } else {
            plugin.getLogger().info("Configuration is up to date.");
        }

        if (migratedVersion != LATEST_VERSION) {
            plugin.getLogger().warning("Configuration version mismatch! Migrated version: " + migratedVersion + ", Expected latest version: " + LATEST_VERSION);
        }
    }
}

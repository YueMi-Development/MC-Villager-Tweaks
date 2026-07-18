package org.yuemi.villagertweaks.config.migrations;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.yuemi.villagertweaks.config.ConfigMigration;

public final class MigrationV2ToV3 implements ConfigMigration {

    @Override
    public void migrate(FileConfiguration config) {
        // Read old configuration
        boolean oldEnable = config.getBoolean("enable-witch-curing", true);

        // Remove old configuration key
        config.set("enable-witch-curing", null);

        // Write new nested configuration
        config.set("witch-curing.enable", oldEnable);
        config.set("witch-curing.cure-time", 60);
        config.set("witch-curing.enable-particles", true);
        config.set("witch-curing.enable-sound", true);

        // Set comments
        config.setComments("witch-curing", List.of(
                "Settings for curing Witches into Villagers."
        ));
        config.setComments("witch-curing.cure-time", List.of(
                "Time in seconds it takes to cure the Witch."
        ));
        config.setComments("witch-curing.enable-particles", List.of(
                "Whether particle effects shake/appear around the Witch while curing."
        ));
        config.setComments("witch-curing.enable-sound", List.of(
                "Whether sound effects play while curing."
        ));
    }
}

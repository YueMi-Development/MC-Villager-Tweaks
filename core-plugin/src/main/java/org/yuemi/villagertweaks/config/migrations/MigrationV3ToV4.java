package org.yuemi.villagertweaks.config.migrations;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.yuemi.villagertweaks.config.ConfigMigration;

public final class MigrationV3ToV4 implements ConfigMigration {

    @Override
    public void migrate(FileConfiguration config) {
        // Set new settings for zombie villager tweaks
        config.set("zombie-villagers.infection-type", "vanilla");
        config.set("zombie-villagers.allow-curing", true);

        // Set comments
        config.setComments("zombie-villagers", List.of(
                "Settings for zombie villager features."
        ));
        config.setComments("zombie-villagers.infection-type", List.of(
                "How villagers are infected by zombies.",
                "Options: vanilla, always-zombie, random, none"
        ));
        config.setComments("zombie-villagers.allow-curing", List.of(
                "Whether zombie villagers can be cured back into villagers."
        ));
    }
}

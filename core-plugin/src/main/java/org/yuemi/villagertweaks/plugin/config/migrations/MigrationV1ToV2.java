package org.yuemi.villagertweaks.plugin.config.migrations;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.yuemi.villagertweaks.plugin.config.ConfigMigration;

public final class MigrationV1ToV2 implements ConfigMigration {

    @Override
    public void migrate(FileConfiguration config) {
        // Add new config setting
        config.set("enable-witch-curing", true);
        config.setComments("enable-witch-curing", List.of(
                "Enable or disable curing Witches into Villagers using weakness potion and golden apple."
        ));
    }
}

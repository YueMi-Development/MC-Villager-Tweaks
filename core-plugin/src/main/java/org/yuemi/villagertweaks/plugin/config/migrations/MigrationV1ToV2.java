package org.yuemi.villagertweaks.plugin.config.migrations;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yuemi.config.api.MigrationStep;

public final class MigrationV1ToV2 implements MigrationStep {

    @Override
    public int getTargetVersion() {
        return 2;
    }

    @Override
    public void migrate(@NotNull FileConfiguration config) {
        // Add new config setting
        config.set("enable-witch-curing", true);
        config.setComments("enable-witch-curing", List.of(
                "Enable or disable curing Witches into Villagers using weakness potion and golden apple."
        ));
    }
}

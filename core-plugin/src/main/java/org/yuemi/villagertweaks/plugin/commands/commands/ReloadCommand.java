package org.yuemi.villagertweaks.plugin.commands.commands;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.yuemi.villagertweaks.plugin.VillagerTweaksPlugin;
import org.yuemi.villagertweaks.plugin.commands.Command;

public final class ReloadCommand extends Command {

    private final VillagerTweaksPlugin plugin;

    public ReloadCommand(VillagerTweaksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration.";
    }

    @Override
    public String getSyntax() {
        return "/vt reload";
    }

    @Override
    public String getPermission() {
        return "villagertweaks.command.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean restartRequired = plugin.reloadPlugin();
        sender.sendMessage(Component.text("VillagerTweaks configuration has been reloaded.", NamedTextColor.GREEN));
        if (restartRequired) {
            sender.sendMessage(Component.text("Warning: The 'enable-tweaks' setting has changed. A server restart is required to apply this change.", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

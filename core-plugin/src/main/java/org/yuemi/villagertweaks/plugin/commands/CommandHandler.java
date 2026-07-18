package org.yuemi.villagertweaks.plugin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuemi.villagertweaks.plugin.VillagerTweaksPlugin;
import org.yuemi.villagertweaks.plugin.commands.commands.HelpCommand;
import org.yuemi.villagertweaks.plugin.commands.commands.ReloadCommand;

public final class CommandHandler implements CommandExecutor, TabCompleter {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler(VillagerTweaksPlugin plugin) {
        registerCommand(new HelpCommand(this));
        registerCommand(new ReloadCommand(plugin));
    }

    private void registerCommand(Command cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }

    /**
     * Gets all registered subcommands.
     *
     * @return map of subcommands
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull org.bukkit.command.Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            Command help = commands.get("help");
            if (help != null) {
                help.execute(sender, args);
            }
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        Command cmd = commands.get(subCommandName);

        if (cmd == null) {
            sender.sendMessage(Component.text("Unknown subcommand. Type '/vt help' for a list of commands.", NamedTextColor.RED));
            return true;
        }

        if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        cmd.execute(sender, subArgs);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull org.bukkit.command.Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String search = args[0].toLowerCase();
            for (Command cmd : commands.values()) {
                if (cmd.getName().toLowerCase().startsWith(search)) {
                    if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                        completions.add(cmd.getName());
                    }
                }
            }
            Collections.sort(completions);
            return completions;
        }

        if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            Command cmd = commands.get(subCommandName);
            if (cmd != null) {
                if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                    String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                    return cmd.tabComplete(sender, subArgs);
                }
            }
        }

        return new ArrayList<>();
    }
}

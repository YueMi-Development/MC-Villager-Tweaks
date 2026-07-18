package org.yuemi.villagertweaks.plugin.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * Base abstract class for plugin subcommands.
 */
public abstract class Command {

    /**
     * Gets the name of the subcommand.
     *
     * @return subcommand name
     */
    public abstract String getName();

    /**
     * Gets the description of the subcommand.
     *
     * @return subcommand description
     */
    public abstract String getDescription();

    /**
     * Gets the syntax of the subcommand.
     *
     * @return subcommand syntax
     */
    public abstract String getSyntax();

    /**
     * Gets the permission node required for the subcommand.
     *
     * @return permission node, or null
     */
    public abstract String getPermission();

    /**
     * Executes the subcommand.
     *
     * @param sender command sender
     * @param args   arguments
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Handles tab completion for the subcommand.
     *
     * @param sender command sender
     * @param args   arguments
     * @return list of completions
     */
    public abstract List<String> tabComplete(CommandSender sender, String[] args);
}

package org.yuemi.villagertweaks.commands.commands;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.yuemi.villagertweaks.commands.Command;
import org.yuemi.villagertweaks.commands.CommandHandler;

public final class HelpCommand extends Command {

    private final CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of available subcommands.";
    }

    @Override
    public String getSyntax() {
        return "/vt help";
    }

    @Override
    public String getPermission() {
        return "villagertweaks.command.use";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("--- VillagerTweaks Commands ---", NamedTextColor.GOLD));
        for (Command cmd : commandHandler.getCommands().values()) {
            if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage(Component.text(cmd.getSyntax() + " - " + cmd.getDescription(), NamedTextColor.YELLOW));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandMap;
import cn.nukkit.command.PluginIdentifiableCommand;
import net.llamadevelopment.bansystem.BanSystem;

public abstract class CommandManager extends Command implements PluginIdentifiableCommand {
    private BanSystem plugin;

    public CommandManager(BanSystem plugin, String name, String desc, String usage) {
        super(name, desc, usage);

        this.plugin = plugin;
    }

    public CommandManager(BanSystem plugin, String name, String desc, String usage, String[] aliases) {
        super(name, desc, usage, aliases);

        this.plugin = plugin;
    }

    public CommandManager(BanSystem plugin, Boolean override, String name, String desc, String usage, String[] aliases) {
        super(name, desc, usage, aliases);

        this.plugin = plugin;

        CommandMap map = plugin.getServer().getCommandMap();
        Command command = map.getCommand(name);
        command.setLabel(name + "_disabled");
        command.unregister(map);
    }

    public BanSystem getPlugin() {
        return plugin;
    }
}
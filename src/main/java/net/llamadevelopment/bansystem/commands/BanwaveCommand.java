package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class BanwaveCommand extends PluginCommand<BanSystem> {

    public BanwaveCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Banwave.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Banwave.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Banwave.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Banwave.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(this.getPermission()) && sender instanceof Player) {
            this.getPlugin().getFormWindows().openWaveDashboard((Player) sender);
        } else sender.sendMessage(Language.get("NoPermission"));
        return true;
    }
}

package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class ClearwarningsCommand extends PluginCommand<BanSystem> {

    public ClearwarningsCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Clearwarnings.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Clearwarnings.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Clearwarnings.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Clearwarnings.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.getWarnLog(player, warnlog -> {
                    int i = warnlog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("NoDataFound"));
                        return;
                    }
                    this.getPlugin().provider.clearWarns(player);
                    sender.sendMessage(Language.get("Clearwarnings", i));
                });
            } else sender.sendMessage(Language.get("ClearwarningsCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class ClearbanlogCommand extends PluginCommand<BanSystem> {

    public ClearbanlogCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Clearbanlog.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Clearbanlog.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Clearbanlog.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Clearbanlog.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.getBanLog(player, banlog -> {
                    int i = banlog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("NoDataFound"));
                        return;
                    }
                    this.getPlugin().provider.clearBanlog(player, sender.getName());
                    sender.sendMessage(Language.get("Clearbanlog", i));
                });
            } else sender.sendMessage(Language.get("ClearbanlogCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

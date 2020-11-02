package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class WarnlogCommand extends PluginCommand<BanSystem> {

    public WarnlogCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Warnlog.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Warnlog.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Warnlog.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Warnlog.Aliases").toArray(new String[]{}));
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
                    sender.sendMessage(Language.get("WarnlogInfo", player, i));
                    warnlog.forEach(warn -> {
                        sender.sendMessage(Language.get("WarnlogPlaceholder"));
                        sender.sendMessage(Language.get("WarnlogReason", warn.getReason()));
                        sender.sendMessage(Language.get("WarnlogID", warn.getWarnID()));
                        sender.sendMessage(Language.get("WarnlogCreator", warn.getCreator()));
                        sender.sendMessage(Language.get("WarnlogDate", warn.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("WarnlogCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

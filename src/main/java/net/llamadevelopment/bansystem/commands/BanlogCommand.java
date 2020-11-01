package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class BanlogCommand extends PluginCommand<BanSystem> {

    public BanlogCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Banlog.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Banlog.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Banlog.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Banlog.Aliases").toArray(new String[]{}));
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
                    sender.sendMessage(Language.get("BanlogInfo", player, i));
                    banlog.forEach(ban -> {
                        sender.sendMessage(Language.get("BanlogPlaceholder"));
                        sender.sendMessage(Language.get("BanlogReason", ban.getReason()));
                        sender.sendMessage(Language.get("BanlogID", ban.getBanID()));
                        sender.sendMessage(Language.get("BanlogBanner", ban.getBanner()));
                        sender.sendMessage(Language.get("BanlogDate", ban.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("BanlogCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

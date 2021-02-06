package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class HistoryCommand extends PluginCommand<BanSystem> {

    public HistoryCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.History.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
        });
        this.setDescription(owner.getConfig().getString("Commands.History.Description"));
        this.setPermission(owner.getConfig().getString("Commands.History.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.History.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String target = args[0];
                this.getPlugin().provider.getBanLog(target, bans -> this.getPlugin().provider.getMuteLog(target, mutes -> this.getPlugin().provider.getWarnLog(target, warns -> {
                    sender.sendMessage(Language.get("HistoryInfo", target));
                    sender.sendMessage(Language.get("HistoryBans", bans.size()));
                    sender.sendMessage(Language.get("HistoryMutes", mutes.size()));
                    sender.sendMessage(Language.get("HistoryWarns", warns.size()));
                })));
            } else sender.sendMessage(Language.get("HistoryCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return true;
    }

}

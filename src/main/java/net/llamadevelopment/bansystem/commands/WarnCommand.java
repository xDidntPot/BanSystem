package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class WarnCommand extends PluginCommand<BanSystem> {

    public WarnCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Warn.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Warn.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Warn.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Warn.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 2) {
                String player = args[0];
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                this.getPlugin().provider.warnPlayer(player, reason, sender.getName());
                sender.sendMessage(Language.get("PlayerWarned", player));
                this.getPlugin().provider.getWarnLog(player, warnlog -> {
                    int i = warnlog.size();
                    Config c = this.getPlugin().getConfig();
                    if (c.getBoolean("WarnSystem.EnableBan")) {
                        if (i >= c.getInt("WarnSystem.MaxWarningCount")) {
                            this.getPlugin().provider.banPlayer(player, c.getString("WarnSystem.BanReason"), "System", c.getInt("WarnSystem.BanSeconds"));
                        }
                    }
                });
            } else sender.sendMessage(Language.get("WarnCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

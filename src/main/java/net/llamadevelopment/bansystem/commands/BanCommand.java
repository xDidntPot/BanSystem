package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.data.BanReason;

public class BanCommand extends PluginCommand<BanSystem> {

    public BanCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Ban.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Ban.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Ban.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Ban.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 2) {
                String player = args[0];
                String reason = args[1];
                this.getPlugin().provider.playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        sender.sendMessage(Language.get("PlayerIsBanned"));
                        return;
                    }
                    if (this.getPlugin().provider.banReasons.get(reason) == null) {
                        sender.sendMessage(Language.get("ReasonNotFound"));
                        return;
                    }
                    BanReason banReason = this.getPlugin().provider.banReasons.get(reason);
                    this.getPlugin().provider.banPlayer(player, banReason.getReason(), sender.getName(), banReason.getSeconds());
                    sender.sendMessage(Language.get("PlayerBanned", player));
                });
            } else {
                this.getPlugin().provider.banReasons.values().forEach(reason -> sender.sendMessage(Language.get("ReasonFormat", reason.getId(), reason.getReason())));
                sender.sendMessage(Language.get("BanCommandUsage", this.getName()));
            }
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

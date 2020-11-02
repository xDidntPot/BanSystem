package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class UnbanCommand extends PluginCommand<BanSystem> {

    public UnbanCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Unban.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Unban.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Unban.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Unban.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        this.getPlugin().provider.unbanPlayer(player);
                        sender.sendMessage(Language.get("PlayerUnbanned", player));
                    } else sender.sendMessage(Language.get("PlayerIsNotBanned"));
                });
            } else sender.sendMessage(Language.get("UnbanCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

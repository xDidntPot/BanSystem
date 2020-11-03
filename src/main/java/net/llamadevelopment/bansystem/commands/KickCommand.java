package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.event.PlayerKickEvent;
import net.llamadevelopment.bansystem.components.language.Language;

public class KickCommand extends PluginCommand<BanSystem> {

    public KickCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Kick.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Kick.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Kick.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Kick.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 2) {
                String player = args[0];
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                Player onlinePlayer = Server.getInstance().getPlayer(player);
                if (onlinePlayer != null) {
                    onlinePlayer.kick(Language.getNP("KickScreen", reason, sender.getName()), false);
                    Server.getInstance().getPluginManager().callEvent(new PlayerKickEvent(player, sender.getName()));
                    sender.sendMessage(Language.get("PlayerKicked", player));
                } else sender.sendMessage(Language.get("PlayerNotOnline"));
            } else sender.sendMessage(Language.get("KickCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

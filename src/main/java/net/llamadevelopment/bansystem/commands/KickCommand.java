package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;

public class KickCommand extends Command {

    public KickCommand(String name) {
        super(name, "Kick a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(getPermission())) {
            if (args.length >= 2) {
                String player = args[0];
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                Player onlinePlayer = Server.getInstance().getPlayer(player);
                if (onlinePlayer != null) {
                    onlinePlayer.kick(Configuration.getAndReplaceNP("KickScreen", reason, sender.getName()), false);
                    sender.sendMessage(Configuration.getAndReplace("PlayerKicked", player));
                } else sender.sendMessage(Configuration.getAndReplace("PlayerNotOnline", player));
            } else sender.sendMessage(Configuration.getAndReplace("KickCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

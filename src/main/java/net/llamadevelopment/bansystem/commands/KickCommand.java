package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.llamadevelopment.bansystem.BanSystem;

public class KickCommand extends CommandManager {


    private BanSystem plugin;

    public KickCommand(BanSystem plugin) {
        super(plugin, plugin.getConfig().getString("Commands.Kick"), "Kick a player.", "/kick");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission("bansystem.command.kick")) {
            if (args.length >= 2) {
                Player player = plugin.getServer().getPlayer(args[0]);
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                String kickMessage = plugin.getConfig().getString("KickScreen").replace("&", "§").replace("%reason%", reason);
                if (player != null) {
                    player.kick(kickMessage, false);
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("KickSuccess").replace("%player%", player.getName()).replace("&", "§"));
                } else {
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("PlayerNotOnline").replace("&", "§"));
                }
            } else {
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.KickCommand").replace("&", "§").replace("%command%", "/" + plugin.getConfig().getString("Commands.Kick")));
            }
        } else {
            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("NoPermission").replace("&", "§"));
        }
        return false;
    }
}

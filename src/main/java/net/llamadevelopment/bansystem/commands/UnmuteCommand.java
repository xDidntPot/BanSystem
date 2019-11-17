package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.managers.MuteManager;
import org.bson.Document;

public class UnmuteCommand extends CommandManager {

    private BanSystem plugin;

    public UnmuteCommand(BanSystem plugin) {
        super(plugin, "unmute", "Unmute a player.", "/unmute");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("bansystem.command.unmute")) {
            if (args.length == 1) {
                String player = args[0];
                if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
                    Document document = new Document("name", player);
                    Document found = (Document) BanSystem.getInstance().getMuteCollection().find(document).first();
                    if (found == null) {
                        sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("PlayerNotMuted").replace("&", "§"));
                        return true;
                    }
                } else {
                    Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
                    if (!bans.exists("Player." + player)) {
                        sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("PlayerNotMuted").replace("&", "§"));
                        return true;
                    }
                }
                MuteManager.unMute(player);
                Player online = BanSystem.getInstance().getServer().getPlayer(player);
                if (online != null) {
                    BanSystem.getInstance().mutedCache.remove(online);
                }
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("UnmuteSuccess").replace("&", "§").replace("%player%", player));
            } else {
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.UnmuteCommand").replace("&", "§"));
            }
        } else {
            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("NoPermission").replace("&", "§"));
        }
        return false;
    }
}
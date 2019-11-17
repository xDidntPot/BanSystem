package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.managers.BanManager;
import org.bson.Document;

public class UnbanCommand extends CommandManager {

    private BanSystem plugin;

    public UnbanCommand(BanSystem plugin) {
        super(plugin, "unban", "Unban a player.", "/unban");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("bansystem.command.unban")) {
            if (args.length == 1) {
                String player = args[0];
                if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
                    Document document = new Document("name", player);
                    Document found = (Document) BanSystem.getInstance().getBanCollection().find(document).first();
                    if (found == null) {
                        sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("PlayerNotBanned").replace("&", "§"));
                        return true;
                    }
                } else {
                    Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
                    if (!bans.exists("Player." + player)) {
                        sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("PlayerNotBanned").replace("&", "§"));
                        return true;
                    }
                }
                BanManager.unBan(player);
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("UnbanSuccess").replace("&", "§").replace("%player%", player));
            } else {
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.UnbanCommand").replace("&", "§"));
            }
        } else {
            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("NoPermission").replace("&", "§"));
        }
        return false;
    }
}
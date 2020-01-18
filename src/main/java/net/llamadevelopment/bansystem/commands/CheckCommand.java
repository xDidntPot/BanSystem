package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.managers.BanManager;
import net.llamadevelopment.bansystem.managers.MuteManager;
import net.llamadevelopment.bansystem.utils.BanUtil;
import net.llamadevelopment.bansystem.utils.MuteUtil;
import org.bson.Document;

public class CheckCommand extends CommandManager {

    private BanSystem plugin;

    public CheckCommand(BanSystem plugin) {
        super(plugin, plugin.getConfig().getString("Commands.Check"), "Check if a player is banned or muted.", "/check");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("bansystem.command.check")) {
            if (args.length == 2) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("ban")) {
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
                    BanUtil banUtil = plugin.banManager.getPlayer(player);
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.Info").replace("&", "§"));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.Player").replace("&", "§").replace("%player%", banUtil.getPlayer()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.Reason").replace("&", "§").replace("%reason%", banUtil.getReason()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.ID").replace("&", "§").replace("%id%", banUtil.getId()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.Banner").replace("&", "§").replace("%banner%", banUtil.getBanner()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.Date").replace("&", "§").replace("%date%", banUtil.getDate()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Ban.RemainingTime").replace("&", "§").replace("%time%", BanManager.getRemainingTime(banUtil.getEnd())));
                } else if (args[1].equalsIgnoreCase("mute")) {
                    if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
                        Document document = new Document("name", player);
                        Document found = (Document) BanSystem.getInstance().getBanCollection().find(document).first();
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
                    MuteUtil muteUtil = BanSystem.getInstance().muteManager.getPlayer(player);
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.Info").replace("&", "§"));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.Player").replace("&", "§").replace("%player%", muteUtil.getPlayer()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.Reason").replace("&", "§").replace("%reason%", muteUtil.getReason()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.ID").replace("&", "§").replace("%id%", muteUtil.getId()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.Muter").replace("&", "§").replace("%muter%", muteUtil.getBanner()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.Date").replace("&", "§").replace("%date%", muteUtil.getDate()));
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Check.Mute.RemainingTime").replace("&", "§").replace("%time%", MuteManager.getRemainingTime(muteUtil.getEnd())));
                } else {
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.CheckCommand").replace("&", "§").replace("%command%", "/" + plugin.getConfig().getString("Commands.Ban")));
                }
            } else {
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.CheckCommand").replace("&", "§").replace("%command%", "/" + plugin.getConfig().getString("Commands.Check")));
            }
        } else {
            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("NoPermission").replace("&", "§"));
        }
        return false;
    }
}

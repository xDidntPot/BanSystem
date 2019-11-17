package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.managers.MuteManager;
import net.llamadevelopment.bansystem.utils.MuteUtil;
import net.llamadevelopment.bansystem.utils.MutedPlayer;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MuteCommand extends CommandManager {

    private BanSystem plugin;

    public MuteCommand(BanSystem plugin) {
        super(plugin, "mute", "Mute a player.", "/mute");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("bansystem.command.mute")) {
            if (args.length == 2) {
                String player = args[0];
                String number = args[1];
                try {
                    Integer n = Integer.parseInt(args[1]);
                    int seconds = plugin.getConfig().getInt("MuteReasons." + number + ".Seconds");
                    String reason = plugin.getConfig().getString("MuteReasons." + number + ".Reason");
                    Integer max = plugin.getConfig().getInt("MuteReasons.Count");
                    if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
                        Document document = new Document("name", player);
                        Document found = (Document) BanSystem.getInstance().getMuteCollection().find(document).first();
                        if (found != null) {
                            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("AlreadyMuted").replace("&", "§"));
                            return true;
                        }
                    } else {
                        Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
                        if (bans.exists("Player." + player)) {
                            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("AlreadyMuted").replace("&", "§"));
                            return true;
                        }
                    }
                    if (!(n > max)) {
                        if (n >= 1) {
                            MuteManager.setMuted(player, reason, getBanID(), sender.getName(), getDate(), seconds);
                            MuteUtil muteUtil = BanSystem.getInstance().muteManager.getPlayer(player);
                            long end = muteUtil.getEnd();
                            String reason1 = muteUtil.getReason();
                            String id = muteUtil.getId();
                            String banner = muteUtil.getBanner();
                            MutedPlayer mutedPlayer = new MutedPlayer(end, reason1, id, banner);
                            Player player1 = BanSystem.getInstance().getServer().getPlayer(player);
                            if (player1 != null) {
                                BanSystem.getInstance().mutedCache.put(player1, mutedPlayer);
                            }
                            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("MuteSuccess").replace("%player%", player).replace("&", "§"));
                        } else {
                            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Reasonlimit").replace("&", "§"));
                        }
                    } else {
                        sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Reasonlimit").replace("&", "§"));
                    }
                } catch (NumberFormatException var1) {
                    sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("MustNumber").replace("&", "§").replace("%max%", plugin.getConfig().getString("MuteReasons.Count")));
                    var1.getMessage();
                }
            } else {
                sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("Usage.MuteCommand").replace("&", "§"));
            }
        } else {
            sender.sendMessage(plugin.getConfig().getString("Prefix").replace("&", "§") + plugin.getConfig().getString("NoPermission").replace("&", "§"));
        }
        return false;
    }

    private String getBanID() {
        String string = "";
        int lastrandom = 0;
        for (int i = 0; i < 6; i++) {
            Random random = new Random();
            int rand = random.nextInt(9);
            while (rand == lastrandom) {
                rand = random.nextInt(9);
            }
            lastrandom = rand;
            string = string + rand;
        }
        return string;
    }

    private String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        String now1 = dateFormat.format(now);
        return now1;
    }
}
package net.llamadevelopment.bansystem.managers;

import cn.nukkit.utils.Config;
import com.mongodb.client.MongoCollection;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.utils.MuteUtil;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Map;

public class MuteManager {

    private BanSystem plugin;

    public MuteManager(BanSystem plugin) {
        this.plugin = plugin;
    }

    public static void setMuted(String player, String reason, String id, String banner, String date, int seconds) {
        if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) {
                end = -1L;
            }

            Document document = new Document("name", player);
            Document found = (Document) BanSystem.getInstance().getMuteCollection().find(document).first();
            if (found == null) {
                document.append("reason", reason);
                document.append("id", id);
                document.append("banner", banner);
                document.append("date", date);
                document.append("end", end);
            }
            BanSystem.getInstance().getMuteCollection().insertOne(document);
        } else {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) {
                end = -1L;
            }
            Config mutes = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
            mutes.set("Player." + player, player);
            mutes.set("Player." + player + ".Reason", reason);
            mutes.set("Player." + player + ".ID", id);
            mutes.set("Player." + player + ".Banner", banner);
            mutes.set("Player." + player + ".Date", date);
            mutes.set("Player." + player + ".End", end);
            mutes.save();
            mutes.reload();
        }

    }

    public static void unMute(String player) {
        if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
            MongoCollection<Document> collection = BanSystem.getInstance().getMuteCollection();
            collection.deleteOne(new Document("name", player));
        } else {
            Config mutes = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
            Map<String, Object> map = mutes.getSection("Player").getAllMap();
            map.remove(player);
            mutes.set("Player", map);
            mutes.save();
            mutes.reload();
        }
    }

    public MuteUtil getPlayer(String player) {
        String name = "";
        String reason = "";
        String id = "";
        String banner = "";
        String date = "";
        long end = 0;

        if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
            Document found = BanSystem.getInstance().getMuteCollection().find(new Document("name", player)).first();
            if (found != null) {
                name = found.getString("name");
                reason = found.getString("reason");
                id = found.getString("id");
                banner = found.getString("banner");
                date = found.getString("date");
                end = found.getLong("end");
            }
        } else {
            Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
            name = player;
            reason = bans.getString("Player." + player + ".Reason");
            id = bans.getString("Player." + player + ".ID");
            banner = bans.getString("Player." + player + ".Banner");
            date = bans.getString("Player." + player + ".Date");
            end = bans.getLong("Player." + player + ".End");
        }

        return new MuteUtil(name, reason, id, banner, date, end);
    }

    public static String getRemainingTime(Long duration) {
        if (duration == -1L) {
            return BanSystem.getInstance().getConfig().getString("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = BanSystem.getInstance().getConfig().getString("Days");
            if (days == 1) {
                day = BanSystem.getInstance().getConfig().getString("Day");
            }

            String hour = BanSystem.getInstance().getConfig().getString("Hours");
            if (hours == 1) {
                hour = BanSystem.getInstance().getConfig().getString("Hour");
            }

            String minute = BanSystem.getInstance().getConfig().getString("Minutes");
            if (minutes == 2) {
                minute = BanSystem.getInstance().getConfig().getString("Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return BanSystem.getInstance().getConfig().getString("Seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }
}
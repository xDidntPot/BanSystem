package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.utils.Config;
import com.mongodb.client.MongoCollection;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.database.MongoDBProvider;
import net.llamadevelopment.bansystem.components.managers.database.MySqlProvider;
import net.llamadevelopment.bansystem.components.utils.BanUtil;
import net.llamadevelopment.bansystem.components.utils.MuteUtil;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Map;

public class MuteManager {

    private static BanSystem instance = BanSystem.getInstance();

    public static void setMuted(String player, String reason, String id, String banner, String date, int seconds) {
        if (instance.isMongodb()) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) {
                end = -1L;
            }

            Document document = new Document("name", player);
            Document found = (Document) MongoDBProvider.getMuteCollection().find(document).first();
            if (found == null) {
                document.append("reason", reason);
                document.append("id", id);
                document.append("banner", banner);
                document.append("date", date);
                document.append("end", end);
            }
            MongoDBProvider.getMuteCollection().insertOne(document);
            createMutelog(getPlayer(player));
        } else if (instance.isMysql()) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) end = -1L;
            try {
                MySqlProvider.update("INSERT INTO mutes (NAME, REASON, ID, BANNER, DATE, END) VALUES ('" + player + "', '" + reason + "', '" + id + "', '" + banner + "', '" + date + "','" + end + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
            createMutelog(getPlayer(player));
        } else if (instance.isYaml()) {
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

    public static void createMutelog(MuteUtil muteUtil) {
        if (instance.isMongodb()) {
            Document document = new Document("player", muteUtil.getPlayer())
                    .append("reason", muteUtil.getReason())
                    .append("id", muteUtil.getId())
                    .append("banner", muteUtil.getBanner())
                    .append("date", muteUtil.getDate());
            MongoDBProvider.getMutelogCollection().insertOne(document);
        } else if (instance.isMysql()) {
            try {
                MySqlProvider.update("INSERT INTO mutelogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + muteUtil.getPlayer() + "', '" + muteUtil.getReason() + "', '" + muteUtil.getId() + "', '" + muteUtil.getBanner() + "', '" + muteUtil.getDate() + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unMute(String player) {
        if (instance.isMongodb()) {
            MongoCollection<Document> collection = MongoDBProvider.getMuteCollection();
            collection.deleteOne(new Document("name", player));
        } else if (instance.isMysql()) {
            try {
                PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("DELETE FROM mutes WHERE NAME = ?");
                preparedStatement.setString(1, player);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (instance.isYaml()) {
            Config mutes = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
            Map<String, Object> map = mutes.getSection("Player").getAllMap();
            map.remove(player);
            mutes.set("Player", map);
            mutes.save();
            mutes.reload();
        }
    }

    public static MuteUtil getPlayer(String player) {
        String name = "";
        String reason = "";
        String id = "";
        String banner = "";
        String date = "";
        long end = 0;

        if (instance.isMongodb()) {
            Document found = MongoDBProvider.getMuteCollection().find(new Document("name", player)).first();
            if (found != null) {
                name = found.getString("name");
                reason = found.getString("reason");
                id = found.getString("id");
                banner = found.getString("banner");
                date = found.getString("date");
                end = found.getLong("end");
            }
        } else if (instance.isMysql()) {
            try {
                PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM mutes WHERE NAME = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    name = rs.getString("NAME");
                    reason = rs.getString("REASON");
                    reason = rs.getString("REASON");
                    id = rs.getString("ID");
                    banner = rs.getString("BANNER");
                    date = rs.getString("DATE");
                    end = rs.getLong("END");
                }
                rs.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (instance.isYaml()) {
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
            return BanSystem.getInstance().getConfig().getString("Unit.Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = BanSystem.getInstance().getConfig().getString("Unit.Days");
            if (days == 1) {
                day = BanSystem.getInstance().getConfig().getString("Unit.Day");
            }

            String hour = BanSystem.getInstance().getConfig().getString("Unit.Hours");
            if (hours == 1) {
                hour = BanSystem.getInstance().getConfig().getString("Unit.Hour");
            }

            String minute = BanSystem.getInstance().getConfig().getString("Unit.Minutes");
            if (minutes == 2) {
                minute = BanSystem.getInstance().getConfig().getString("Unit.Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return BanSystem.getInstance().getConfig().getString("Unit.Seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }
}
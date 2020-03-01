package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.utils.Config;
import com.mongodb.client.MongoCollection;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.database.MongoDBProvider;
import net.llamadevelopment.bansystem.components.managers.database.MySqlProvider;
import net.llamadevelopment.bansystem.components.utils.BanUtil;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Map;

public class BanManager {

    private static BanSystem instance = BanSystem.getInstance();

    public static void setBanned(String player, String reason, String id, String banner, String date, int seconds) {
        if (instance.isMongodb()) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) {
                end = -1L;
            }

            Document document = new Document("name", player);
            Document found = (Document) MongoDBProvider.getBanCollection().find(document).first();
            if (found == null) {
                document.append("reason", reason);
                document.append("id", id);
                document.append("banner", banner);
                document.append("date", date);
                document.append("end", end);
            }
            MongoDBProvider.getBanCollection().insertOne(document);
            createBanlog(getPlayer(player));
        } else if (instance.isMysql()) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) end = -1L;
            try {
                MySqlProvider.update("INSERT INTO bans (NAME, REASON, ID, BANNER, DATE, END) VALUES ('" + player + "', '" + reason + "', '" + id + "', '" + banner + "', '" + date + "', '" + end + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
            createBanlog(getPlayer(player));
        } else if (instance.isYaml()) {
            long current = System.currentTimeMillis();
            long end = current + seconds * 1000L;
            if (seconds == -1) {
                end = -1L;
            }
            Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
            bans.set("Player." + player, player);
            bans.set("Player." + player + ".Reason", reason);
            bans.set("Player." + player + ".ID", id);
            bans.set("Player." + player + ".Banner", banner);
            bans.set("Player." + player + ".Date", date);
            bans.set("Player." + player + ".End", end);
            bans.save();
            bans.reload();
        }
    }

    public static void createBanlog(BanUtil banUtil) {
        if (instance.isMongodb()) {
            Document document = new Document("player", banUtil.getPlayer())
                    .append("reason", banUtil.getReason())
                    .append("id", banUtil.getId())
                    .append("banner", banUtil.getBanner())
                    .append("date", banUtil.getDate());
            MongoDBProvider.getBanlogCollection().insertOne(document);
        } else if (instance.isMysql()) {
            try {
                MySqlProvider.update("INSERT INTO banlogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + banUtil.getPlayer() + "', '" + banUtil.getReason() + "', '" + banUtil.getId() + "', '" + banUtil.getBanner() + "', '" + banUtil.getDate() + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unBan(String player) {
        if (instance.isMongodb()) {
            MongoCollection<Document> collection = MongoDBProvider.getBanCollection();
            collection.deleteOne(new Document("name", player));
        } else if (instance.isMysql()) {
            try {
                PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("DELETE FROM bans WHERE NAME = ?");
                preparedStatement.setString(1, player);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (instance.isYaml()){
            Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
            Map<String, Object> map = bans.getSection("Player").getAllMap();
            map.remove(player);
            bans.set("Player", map);
            bans.save();
            bans.reload();
        }

    }

    public static BanUtil getPlayer(String player) {
        String name = "";
        String reason = "";
        String id = "";
        String banner = "";
        String date = "";
        long end = 0;

        if (instance.isMongodb()) {
            Document found = MongoDBProvider.getBanCollection().find(new Document("name", player)).first();
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
                PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM bans WHERE NAME = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    name = rs.getString("NAME");
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
        } else if (instance.isYaml()){
            Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
            name = player;
            reason = bans.getString("Player." + player + ".Reason");
            id = bans.getString("Player." + player + ".ID");
            banner = bans.getString("Player." + player + ".Banner");
            date = bans.getString("Player." + player + ".Date");
            end = bans.getLong("Player." + player + ".End");
        }
        return new BanUtil(name, reason, id, banner, date, end);
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

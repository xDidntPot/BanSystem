package net.llamadevelopment.bansystem.components.managers;

import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.database.MongoDBProvider;
import net.llamadevelopment.bansystem.components.managers.database.MySqlProvider;
import net.llamadevelopment.bansystem.components.utils.WarnUtil;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class WarnManager {

    private static BanSystem instance = BanSystem.getInstance();

    public static void createWarning(String player, String reason, String creator) {
        if (instance.isMongodb()) {
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", getID())
                    .append("creator", creator)
                    .append("date", getDate());
            MongoDBProvider.getWarnCollection().insertOne(document);
        } else if (instance.isMysql()) {
            try {
                MySqlProvider.update("INSERT INTO warnings (PLAYER, REASON, ID, CREATOR, DATE) VALUES ('" + player + "', '" + reason + "', '" + getID() + "', '" + creator + "', '" + getDate() + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updatePlayer(String player) {
        if (instance.getConfig().getBoolean("Warning.EnableBan")) {
            int r = instance.getConfig().getInt("Warning.BanByAmount");
            int a = 0;
            if (instance.isMongodb()) {
                for (Document doc : MongoDBProvider.getWarnCollection().find(new Document("player", player))) {
                    a++;
                }
                String reason = instance.getConfig().getString("Warning.BanReason");
                int e = instance.getConfig().getInt("Warning.BanTime");
                if (a >= r) BanManager.setBanned(player, reason, getID(), "System", getDate(), e);
            } else if (instance.isMysql()) {
                try {
                    PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM warnings WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        a++;
                    }
                    rs.close();
                    preparedStatement.close();
                } catch (Exception ignored) {
                }
                String reason = instance.getConfig().getString("Warning.BanReason");
                int e = instance.getConfig().getInt("Warning.BanTime");
                if (a >= r) BanManager.setBanned(player, reason, getID(), "System", getDate(), e);
            }
        }
    }

    public static WarnUtil getPlayer(String player) {
        String name = "";
        String reason = "";
        String id = "";
        String creator = "";
        String date = "";

        if (instance.isMongodb()) {
            Document document = MongoDBProvider.getWarnCollection().find(new Document("player", player)).first();
            if (document != null) {
                name = document.getString("player");
                reason = document.getString("reason");
                id = document.getString("id");
                creator = document.getString("creator");
                date = document.getString("date");
            }
        } else if (instance.isMysql()) {
            try {
                PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM warnings WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    name = rs.getString("PLAYER");
                    reason = rs.getString("REASON");
                    id = rs.getString("ID");
                    creator = rs.getString("CREATOR");
                    date = rs.getString("DATE");
                }
                rs.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new WarnUtil(name, reason, id, creator, date);
    }

    private static String getID() {
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

    private static String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        String now1 = dateFormat.format(now);
        return now1;
    }

}

package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MysqlProvider extends Provider {

    private final SystemSettings settings = BanSystemAPI.getSystemSettings();
    private final Config config = BanSystem.getInstance().getConfig();

    private Connection connection;

    @Override
    public void connect(BanSystem server) {
        CompletableFuture.runAsync(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.config.getString("MySql.Host") + ":" + this.config.getString("MySql.Port") + "/" + this.config.getString("MySql.Database") + "?autoReconnect=true&useGmtMillisForDatetimes=true&serverTimezone=GMT", this.config.getString("MySql.User"), this.config.getString("MySql.Password"));
                this.update("CREATE TABLE IF NOT EXISTS bans(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), time BIGINT(255), PRIMARY KEY (id));");
                this.update("CREATE TABLE IF NOT EXISTS mutes(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), time BIGINT(255), PRIMARY KEY (id));");
                this.update("CREATE TABLE IF NOT EXISTS warns(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), creator VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                this.update("CREATE TABLE IF NOT EXISTS banlogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                this.update("CREATE TABLE IF NOT EXISTS mutelogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                server.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    @Override
    public void disconnect(BanSystem server) {
        if (this.connection != null) {
            try {
                this.connection.close();
                server.getLogger().info("[MySqlClient] Connection closed.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to close connection.");
            }
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void update(String query) {
        CompletableFuture.runAsync(() -> {
            if (this.connection != null) {
                try {
                    PreparedStatement preparedStatement = this.connection.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean playerIsBanned(String player) {
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM bans WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getString("PLAYER") != null;
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean playerIsMuted(String player) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM mutes WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getString("PLAYER") != null;
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void banPlayer(String player, String reason, String banner, int seconds) {
        long current = System.currentTimeMillis();
        long end = current + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        try {
            this.update("INSERT INTO bans (PLAYER, REASON, ID, BANNER, DATE, TIME) VALUES ('" + player + "', '" + reason + "', '" + id + "', '" + banner + "', '" + date + "', '" + end + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.createBanlog(new Ban(player, reason, id, banner, date, end));
        Player player1 = Server.getInstance().getPlayer(banner);
        if (this.settings.isWaterdog() && player1.isOnline()) {
            Ban ban = this.getBan(player);
            ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            try {
                dataOutputStream.writeUTF("banplayer");
                dataOutputStream.writeUTF(player);
                dataOutputStream.writeUTF(ban.getReason());
                dataOutputStream.writeUTF(ban.getBanID());
                dataOutputStream.writeUTF(this.getRemainingTime(ban.getTime()));
                customEventPacket.eventName = "bansystembridge:main";
                customEventPacket.eventData = outputStream.toByteArray();
                player1.dataPacket(customEventPacket);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Player onlinePlayer = Server.getInstance().getPlayer(player);
        if (onlinePlayer != null) {
            Ban ban = this.getBan(player);
            onlinePlayer.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), this.getRemainingTime(ban.getTime())), false);
        }
    }

    @Override
    public void mutePlayer(String player, String reason, String banner, int seconds) {
        long current = System.currentTimeMillis();
        long end = current + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        try {
            this.update("INSERT INTO mutes (PLAYER, REASON, ID, BANNER, DATE, TIME) VALUES ('" + player + "', '" + reason + "', '" + id + "', '" + banner + "', '" + date + "', '" + end + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.createMutelog(new Mute(player, reason, id, banner, date, end));
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        try {
            this.update("INSERT INTO warns (PLAYER, REASON, ID, CREATOR, DATE) VALUES ('" + player + "', '" + reason + "', '" + BanSystemAPI.getRandomIDCode() + "', '" + creator + "', '" + BanSystemAPI.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Player player1 = Server.getInstance().getPlayer(creator);
        if (this.settings.isWaterdog() && player1.isOnline()) {
            ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            try {
                dataOutputStream.writeUTF("warnplayer");
                dataOutputStream.writeUTF(player);
                dataOutputStream.writeUTF(reason);
                dataOutputStream.writeUTF(creator);
                customEventPacket.eventName = "bansystembridge:main";
                customEventPacket.eventData = outputStream.toByteArray();
                player1.dataPacket(customEventPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Player onlinePlayer = Server.getInstance().getPlayer(player);
        if (onlinePlayer != null) onlinePlayer.kick(Language.getNP("WarnScreen", reason, creator), false);
    }

    @Override
    public void unbanPlayer(String player) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement("DELETE FROM bans WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void unmutePlayer(String player) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement("DELETE FROM mutes WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Ban getBan(String player) {
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM bans WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Ban(player, rs.getString("REASON"), rs.getString("ID"), rs.getString("BANNER"), rs.getString("DATE"), rs.getLong("TIME"));
            }
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Mute getMute(String player) {
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM mutes WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Mute(player, rs.getString("REASON"), rs.getString("ID"), rs.getString("BANNER"), rs.getString("DATE"), rs.getLong("TIME"));
            }
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createBanlog(Ban ban) {
        try {
            this.update("INSERT INTO banlogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + ban.getPlayer() + "', '" + ban.getReason() + "', '" + ban.getBanID() + "', '" + ban.getBanner() + "', '" + ban.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createMutelog(Mute mute) {
        try {
            this.update("INSERT INTO mutelogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + mute.getPlayer() + "', '" + mute.getReason() + "', '" + mute.getMuteID() + "', '" + mute.getMuter() + "', '" + mute.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Ban> getBanlog(String player) {
        List<Ban> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM banlogs WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Ban ban = new Ban(player, rs.getString("REASON"), rs.getString("ID"), rs.getString("BANNER"), rs.getString("DATE"), 0);
                list.add(ban);
            }
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Mute> getMutelog(String player) {
        List<Mute> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM mutelogs WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Mute mute = new Mute(player, rs.getString("REASON"), rs.getString("ID"), rs.getString("BANNER"), rs.getString("DATE"), 0);
                list.add(mute);
            }
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Warn> getWarnings(String player) {
        List<Warn> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM warns WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Warn warn = new Warn(player, rs.getString("REASON"), rs.getString("ID"), rs.getString("CREATOR"), rs.getString("DATE"));
                list.add(warn);
            }
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void clearBanlog(String player) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM banlogs WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    try {
                        PreparedStatement preparedStatement2 = this.getConnection().prepareStatement("DELETE FROM banlogs WHERE ID = ?");
                        preparedStatement2.setString(1, rs.getString("ID"));
                        preparedStatement2.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rs.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void clearMutelog(String player) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM mutelogs WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    try {
                        PreparedStatement preparedStatement2 = this.getConnection().prepareStatement("DELETE FROM mutelogs WHERE ID = ?");
                        preparedStatement2.setString(1, rs.getString("ID"));
                        preparedStatement2.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rs.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void clearWarns(String player) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM warns WHERE PLAYER = ?");
                preparedStatement.setString(1, player);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    try {
                        PreparedStatement preparedStatement2 = this.getConnection().prepareStatement("DELETE FROM warns WHERE ID = ?");
                        preparedStatement2.setString(1, rs.getString("ID"));
                        preparedStatement2.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rs.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setBanReason(String player, String reason) {
        this.update("UPDATE bans SET REASON= '" + reason + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setMuteReason(String player, String reason) {
        this.update("UPDATE mutes SET REASON= '" + reason + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setBanTime(String player, long time) {
        this.update("UPDATE bans SET TIME= '" + time + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setMuteTime(String player, long time) {
        this.update("UPDATE mutes SET TIME= '" + time + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public String getRemainingTime(long duration) {
        if (duration == -1L) {
            return Language.getNP("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = Language.getNP("Days");
            if (days == 1) {
                day = Language.getNP("Day");
            }

            String hour = Language.getNP("Hours");
            if (hours == 1) {
                hour = Language.getNP("Hour");
            }

            String minute = Language.getNP("Minutes");
            if (minutes == 2) {
                minute = Language.getNP("Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Language.getNP("Seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }

    @Override
    public String getProvider() {
        return "MySql";
    }
}

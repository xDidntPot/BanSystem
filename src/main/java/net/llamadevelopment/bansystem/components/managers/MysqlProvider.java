package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MysqlProvider extends Provider {

    Config config = BanSystem.getInstance().getConfig();
    BanSystem instance = BanSystem.getInstance();
    boolean debug = BanSystemAPI.getSystemSettings().isDebugMode();
    Connection connection;

    @Override
    public void setUp(BanSystem server) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("MySql.Host") + ":" + config.getString("MySql.Port") + "/" + config.getString("MySql.Database") + "?autoReconnect=true", config.getString("MySql.User"), config.getString("MySql.Password"));
                    update("CREATE TABLE IF NOT EXISTS bans(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), time BIGINT(255), PRIMARY KEY (id));");
                    update("CREATE TABLE IF NOT EXISTS mutes(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), time BIGINT(255), PRIMARY KEY (id));");
                    update("CREATE TABLE IF NOT EXISTS warns(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), creator VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                    update("CREATE TABLE IF NOT EXISTS banlogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                    update("CREATE TABLE IF NOT EXISTS mutelogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disconnect(BanSystem server) {
        super.disconnect(server);
    }

    public Connection getConnection() {
        return connection;
    }

    public void update(String query) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                if (connection != null) {
                    try {
                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.executeUpdate();
                        ps.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean playerIsBanned(String player) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM bans WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getString("PLAYER") == null;
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
            if (rs.next()) return rs.getString("PLAYER") == null;
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
        try {
            update("INSERT INTO bans (PLAYER, REASON, ID, BANNER, DATE, TIME) VALUES ('" + player + "', '" + reason + "', '" + BanSystemAPI.getRandomIDCode() + "', '" + banner + "', '" + BanSystemAPI.getDate() + "', '" + end + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mutePlayer(String player, String reason, String banner, int seconds) {
        long current = System.currentTimeMillis();
        long end = current + seconds * 1000L;
        if (seconds == -1) end = -1L;
        try {
            update("INSERT INTO mutes (PLAYER, REASON, ID, BANNER, DATE, TIME) VALUES ('" + player + "', '" + reason + "', '" + BanSystemAPI.getRandomIDCode() + "', '" + banner + "', '" + BanSystemAPI.getDate() + "', '" + end + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        try {
            update("INSERT INTO warns (PLAYER, REASON, ID, CREATOR, DATE) VALUES ('" + player + "', '" + reason + "', '" + BanSystemAPI.getRandomIDCode() + "', '" + creator + "', '" + BanSystemAPI.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unbanPlayer(String player) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM bans WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void unmutePlayer(String player) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM mutes WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Ban getBan(String player) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM bans WHERE PLAYER = ?");
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
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM mutes WHERE PLAYER = ?");
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
    public Warn getWarn(String warnID) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM warns WHERE ID = ?");
            preparedStatement.setString(1, warnID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Warn(rs.getString("PLAYER"), rs.getString("REASON"), warnID, rs.getString("CREATOR"), rs.getString("DATE"));
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
            update("INSERT INTO banlogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + ban.getPlayer() + "', '" + ban.getReason() + "', '" + ban.getBanID() + "', '" + ban.getBanner() + "', '" + ban.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createMutelog(Mute mute) {
        try {
            update("INSERT INTO mutelogs (PLAYER, REASON, ID, BANNER, DATE) VALUES ('" + mute.getPlayer() + "', '" + mute.getReason() + "', '" + mute.getMuteID() + "', '" + mute.getMuter() + "', '" + mute.getDate() + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Ban> getBanlog(String player) {
        List<Ban> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM banlogs WHERE PLAYER = ?");
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
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM mutelogs WHERE PLAYER = ?");
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
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM warns WHERE PLAYER = ?");
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
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM banlogs WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        try {
                            PreparedStatement preparedStatement2 = getConnection().prepareStatement("DELETE FROM banlogs WHERE ID = ?");
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
            }
        });
    }

    @Override
    public void clearMutelog(String player) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM mutelogs WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        try {
                            PreparedStatement preparedStatement2 = getConnection().prepareStatement("DELETE FROM mutelogs WHERE ID = ?");
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
            }
        });
    }

    @Override
    public void clearWarns(String player) {
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM warns WHERE PLAYER = ?");
                    preparedStatement.setString(1, player);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        try {
                            PreparedStatement preparedStatement2 = getConnection().prepareStatement("DELETE FROM warns WHERE ID = ?");
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
            }
        });
    }

    @Override
    public void setBanReason(String player, String reason) {
        update("UPDATE bans SET REASON= '" + reason + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setMuteReason(String player, String reason) {
        update("UPDATE mutes SET REASON= '" + reason + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setBanTime(String player, long time) {
        update("UPDATE bans SET TIME= '" + time + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public void setMuteTime(String player, long time) {
        update("UPDATE mutes SET TIME= '" + time + "' WHERE PLAYER= '" + player + "';");
    }

    @Override
    public String getRemainingTime(long duration) {
        if (duration == -1L) {
            return Configuration.getAndReplaceNP("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = Configuration.getAndReplaceNP("Days");
            if (days == 1) {
                day = Configuration.getAndReplaceNP("Day");
            }

            String hour = Configuration.getAndReplaceNP("Hours");
            if (hours == 1) {
                hour = Configuration.getAndReplaceNP("Hour");
            }

            String minute = Configuration.getAndReplaceNP("Minutes");
            if (minutes == 2) {
                minute = Configuration.getAndReplaceNP("Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Configuration.getAndReplaceNP("Seconds");
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

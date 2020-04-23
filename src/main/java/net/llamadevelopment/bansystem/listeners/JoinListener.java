package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.BanManager;
import net.llamadevelopment.bansystem.components.managers.MuteManager;
import net.llamadevelopment.bansystem.components.managers.database.MongoDBProvider;
import net.llamadevelopment.bansystem.components.managers.database.MySqlProvider;
import net.llamadevelopment.bansystem.components.utils.BanUtil;
import net.llamadevelopment.bansystem.components.utils.MessageUtil;
import net.llamadevelopment.bansystem.components.utils.MuteUtil;
import net.llamadevelopment.bansystem.components.utils.MutedPlayer;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JoinListener implements Listener {

    private static BanSystem instance = BanSystem.getInstance();

    @EventHandler
    public void onJoin(final PlayerLoginEvent event) {
        BanSystem.getInstance().getServer().getScheduler().scheduleDelayedTask(BanSystem.getInstance(), new Runnable() {
            public void run() {
                Player player = event.getPlayer();
                if (instance.isMongodb()) {
                    Document document = new Document("name", player.getName());
                    Document found = (Document) MongoDBProvider.getBanCollection().find(document).first();
                    if (found != null) {
                        BanUtil banUtil = BanManager.getPlayer(player.getName());
                        long current = System.currentTimeMillis();
                        long end = banUtil.getEnd();
                        if (!(end == -1L)) {
                            if (end < current) {
                                BanManager.unBan(player.getName());
                            } else {
                                player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                            }
                        } else {
                            player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                        }
                    }
                } else if (instance.isMysql()) {
                    try {
                        PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM bans WHERE NAME = ?");
                        preparedStatement.setString(1, player.getName());
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            if (rs.getString("NAME") != null) {
                                BanUtil banUtil = BanManager.getPlayer(player.getName());
                                long current = System.currentTimeMillis();
                                long end = banUtil.getEnd();
                                if (!(end == -1L)) {
                                    if (end < current) {
                                        BanManager.unBan(player.getName());
                                    } else {
                                        player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                                    }
                                } else {
                                    player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                                }
                            }
                        }
                        rs.close();
                        preparedStatement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (instance.isYaml()) {
                    Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
                    if (bans.exists("Player." + player.getName())) {
                        BanUtil banUtil = BanManager.getPlayer(player.getName());
                        long current = System.currentTimeMillis();
                        long end = banUtil.getEnd();
                        if (!(end == -1L)) {
                            if (end < current) {
                                BanManager.unBan(player.getName());
                            } else {
                                player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                            }
                        } else {
                            player.kick(MessageUtil.banScreen(banUtil.getReason(), banUtil.getId(), BanManager.getRemainingTime(banUtil.getEnd()), banUtil.getBanner()), false);
                        }
                    }
                }
                if (instance.isMongodb()) {
                    Document mute = MongoDBProvider.getMuteCollection().find(new Document("name", player.getName())).first();
                    MuteUtil muteUtil = MuteManager.getPlayer(player.getName());
                    if (mute != null) {
                        long current = System.currentTimeMillis();
                        long end = muteUtil.getEnd();
                        String reason = muteUtil.getReason();
                        String id = muteUtil.getId();
                        String banner = muteUtil.getBanner();
                        MutedPlayer mutedPlayer = new MutedPlayer(end, reason, id, banner);
                        Player player1 = event.getPlayer();
                        BanSystem.getInstance().mutedCache.put(player1, mutedPlayer);
                        if (end < current) {
                            BanSystem.getInstance().mutedCache.remove(player1);
                        }
                    }
                } else if (instance.isMysql()) {
                    MuteUtil muteUtil = MuteManager.getPlayer(player.getName());
                    try {
                        PreparedStatement preparedStatement = MySqlProvider.getConnection().prepareStatement("SELECT * FROM mutes WHERE NAME = ?");
                        preparedStatement.setString(1, player.getName());
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            if (rs.getString("NAME") != null) {
                                long current = System.currentTimeMillis();
                                long end = muteUtil.getEnd();
                                String reason = muteUtil.getReason();
                                String id = muteUtil.getId();
                                String banner = muteUtil.getBanner();
                                MutedPlayer mutedPlayer = new MutedPlayer(end, reason, id, banner);
                                Player player1 = event.getPlayer();
                                BanSystem.getInstance().mutedCache.put(player1, mutedPlayer);
                                if (end < current) {
                                    BanSystem.getInstance().mutedCache.remove(player1);
                                }
                            }
                        }
                        rs.close();
                        preparedStatement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (instance.isYaml()) {
                    Config mutes = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
                    MuteUtil muteUtil = MuteManager.getPlayer(player.getName());
                    if (mutes.exists("Player." + player.getName())) {
                        long current = System.currentTimeMillis();
                        long end = muteUtil.getEnd();
                        String reason = muteUtil.getReason();
                        String id = muteUtil.getId();
                        String banner = muteUtil.getBanner();
                        MutedPlayer mutedPlayer = new MutedPlayer(end, reason, id, banner);
                        Player player1 = event.getPlayer();
                        BanSystem.getInstance().mutedCache.put(player1, mutedPlayer);
                        if (end < current) {
                            BanSystem.getInstance().mutedCache.remove(player1);
                        }
                    }
                }
            }
        }, instance.getConfig().getInt("Settings.JoinDelay"));
    }
}

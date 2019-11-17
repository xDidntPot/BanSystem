package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.managers.BanManager;
import net.llamadevelopment.bansystem.utils.BanUtil;
import net.llamadevelopment.bansystem.utils.MessageUtil;
import net.llamadevelopment.bansystem.utils.MuteUtil;
import net.llamadevelopment.bansystem.utils.MutedPlayer;
import org.bson.Document;

public class JoinListener implements Listener {

    public JoinListener() {
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
            Document document = new Document("name", player.getName());
            Document found = (Document) BanSystem.getInstance().getBanCollection().find(document).first();
            if (found != null) {
                BanUtil banUtil = BanSystem.getInstance().banManager.getPlayer(player.getName());
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
        } else {
            Config bans = new Config(BanSystem.getInstance().getDataFolder() + "/bans.yml", Config.YAML);
            if (bans.exists("Player." + player.getName())) {
                BanUtil banUtil = BanSystem.getInstance().banManager.getPlayer(player.getName());
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
        if (BanSystem.getInstance().getConfig().getBoolean("MongoDB")) {
            Document mute = BanSystem.getInstance().getMuteCollection().find(new Document("name", player.getName())).first();
            MuteUtil muteUtil = BanSystem.getInstance().muteManager.getPlayer(player.getName());
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
        } else {
            Config mutes = new Config(BanSystem.getInstance().getDataFolder() + "/mutes.yml", Config.YAML);
            MuteUtil muteUtil = BanSystem.getInstance().muteManager.getPlayer(player.getName());
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
}

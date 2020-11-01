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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YamlProvider extends Provider {

    private final SystemSettings settings = BanSystemAPI.getSystemSettings();
    private Config bans, banlog, mutes, mutelog, warns;

    @Override
    public void connect(BanSystem server) {
        CompletableFuture.runAsync(() -> {
            server.saveResource("/data/bans.yml");
            server.saveResource("/data/banlog.yml");
            server.saveResource("/data/mutes.yml");
            server.saveResource("/data/mutelog.yml");
            server.saveResource("/data/warns.yml");
            this.bans = new Config(server.getDataFolder() + "/data/bans.yml", Config.YAML);
            this.banlog = new Config(server.getDataFolder() + "/data/banlog.yml", Config.YAML);
            this.mutes = new Config(server.getDataFolder() + "/data/mutes.yml", Config.YAML);
            this.mutelog = new Config(server.getDataFolder() + "/data/mutelog.yml", Config.YAML);
            this.warns = new Config(server.getDataFolder() + "/data/warns.yml", Config.YAML);
        });
    }

    @Override
    public boolean playerIsBanned(String player) {
        return this.bans.exists("Ban." + player);
    }

    @Override
    public boolean playerIsMuted(String player) {
        return this.mutes.exists("Mute." + player);
    }

    @Override
    public void banPlayer(String player, String reason, String banner, int seconds) {
        long end = System.currentTimeMillis() + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        this.bans.set("Ban." + player + ".Reason", reason);
        this.bans.set("Ban." + player + ".ID", id);
        this.bans.set("Ban." + player + ".Banner", banner);
        this.bans.set("Ban." + player + ".Date", date);
        this.bans.set("Ban." + player + ".Time", end);
        this.bans.save();
        this.bans.reload();
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
        long end = System.currentTimeMillis() + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        this.mutes.set("Mute." + player + ".Reason", reason);
        this.mutes.set("Mute." + player + ".ID", id);
        this.mutes.set("Mute." + player + ".Banner", banner);
        this.mutes.set("Mute." + player + ".Date", date);
        this.mutes.set("Mute." + player + ".Time", end);
        this.mutes.save();
        this.mutes.reload();
        this.createMutelog(new Mute(player, reason, id, banner, date, end));
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        this.warns.set("Warn." + player + "." + id + ".Reason", reason);
        this.warns.set("Warn." + player + "." + id + ".Creator", creator);
        this.warns.set("Warn." + player + "." + id + ".Date", date);
        this.warns.save();
        this.warns.reload();
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
        Map<String, Object> map = this.bans.getSection("Ban").getAllMap();
        map.remove(player);
        this.bans.set("Ban", map);
        this.bans.save();
        this.bans.reload();
    }

    @Override
    public void unmutePlayer(String player) {
        Map<String, Object> map = this.mutes.getSection("Mute").getAllMap();
        map.remove(player);
        this.mutes.set("Mute", map);
        this.mutes.save();
        this.mutes.reload();
    }

    @Override
    public Ban getBan(String player) {
        String reason = this.bans.getString("Ban." + player + ".Reason");
        String banID = this.bans.getString("Ban." + player + ".ID");
        String banner = this.bans.getString("Ban." + player + ".Banner");
        String date = this.bans.getString("Ban." + player + ".Date");
        long time = this.bans.getLong("Ban." + player + ".Time");
        return new Ban(player, reason, banID, banner, date, time);
    }

    @Override
    public Mute getMute(String player) {
        String reason = this.mutes.getString("Mute." + player + ".Reason");
        String banID = this.mutes.getString("Mute." + player + ".ID");
        String banner = this.mutes.getString("Mute." + player + ".Banner");
        String date = this.mutes.getString("Mute." + player + ".Date");
        long time = this.mutes.getLong("Mute." + player + ".Time");
        return new Mute(player, reason, banID, banner, date, time);
    }

    @Override
    public void createBanlog(Ban ban) {
        this.banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Reason", ban.getReason());
        this.banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Banner", ban.getBanner());
        this.banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Date", ban.getDate());
        this.banlog.save();
        this.banlog.reload();
    }

    @Override
    public void createMutelog(Mute mute) {
        this.mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Reason", mute.getReason());
        this.mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Muter", mute.getMuter());
        this.mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Date", mute.getDate());
        this.mutelog.save();
        this.mutelog.reload();
    }

    @Override
    public List<Ban> getBanlog(String player) {
        List<Ban> list = new ArrayList<>();
        for (String s : this.banlog.getSection("Banlog." + player).getAll().getKeys(false)) {
            String reason = this.banlog.getString("Banlog." + player + "." + s + ".Reason");
            String banner = this.banlog.getString("Banlog." + player + "." + s + ".Banner");
            String date = this.banlog.getString("Banlog." + player + "." + s + ".Date");
            list.add(new Ban(player, reason, s, banner, date, 0));
        }
        return list;
    }

    @Override
    public List<Mute> getMutelog(String player) {
        List<Mute> list = new ArrayList<>();
        for (String s : this.mutelog.getSection("Mutelog." + player).getAll().getKeys(false)) {
            String reason = this.mutelog.getString("Mutelog." + player + "." + s + ".Reason");
            String banner = this.mutelog.getString("Mutelog." + player + "." + s + ".Banner");
            String date = this.mutelog.getString("Mutelog." + player + "." + s + ".Date");
            list.add(new Mute(player, reason, s, banner, date, 0));
        }
        return list;
    }

    @Override
    public List<Warn> getWarnings(String player) {
        List<Warn> list = new ArrayList<>();
        for (String s : this.warns.getSection("Warn." + player).getAll().getKeys(false)) {
            String reason = this.warns.getString("Warn." + player + "." + s + ".Reason");
            String creator = this.warns.getString("Warn." + player + "." + s + ".Creator");
            String date = this.warns.getString("Warn." + player + "." + s + ".Date");
            list.add(new Warn(player, reason, s, creator, date));
        }
        return list;
    }

    @Override
    public void clearBanlog(String player) {
        CompletableFuture.runAsync(() -> {
            Map<String, Object> map = this.banlog.getSection("Banlog").getAllMap();
            map.remove(player);
            this.banlog.set("Banlog", map);
            this.banlog.save();
            this.banlog.reload();
        });
    }

    @Override
    public void clearMutelog(String player) {
        CompletableFuture.runAsync(() -> {
            Map<String, Object> map = this.mutelog.getSection("Mutelog").getAllMap();
            map.remove(player);
            this.mutelog.set("Mutelog", map);
            this.mutelog.save();
            this.mutelog.reload();
        });
    }

    @Override
    public void clearWarns(String player) {
        CompletableFuture.runAsync(() -> {
            Map<String, Object> map = this.warns.getSection("Warn").getAllMap();
            map.remove(player);
            this.warns.set("Warn", map);
            this.warns.save();
            this.warns.reload();
        });
    }

    @Override
    public void setBanReason(String player, String reason) {
        this.bans.set("Ban." + player + ".Reason", reason);
        this.bans.save();
        this.bans.reload();
    }

    @Override
    public void setMuteReason(String player, String reason) {
        this.mutes.set("Mute." + player + ".Reason", reason);
        this.mutes.save();
        this.mutes.reload();
    }

    @Override
    public void setBanTime(String player, long time) {
        this.bans.set("Ban." + player + ".Time", time);
        this.bans.save();
        this.bans.reload();
    }

    @Override
    public void setMuteTime(String player, long time) {
        this.mutes.set("Mute." + player + ".Time", time);
        this.mutes.save();
        this.mutes.reload();
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
            if (minutes == 1) {
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
        return "Yaml";
    }
}

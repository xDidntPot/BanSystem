package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlProvider extends Provider {

    Config bans, banlog, mutes, mutelog, warns;

    @Override
    public void connect(BanSystem server) {
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
    }

    @Override
    public boolean playerIsBanned(String player) {
        return bans.exists("Ban." + player);
    }

    @Override
    public boolean playerIsMuted(String player) {
        return mutes.exists("Mute." + player);
    }

    @Override
    public void banPlayer(String player, String reason, String banner, int seconds) {
        long end = System.currentTimeMillis() + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        bans.set("Ban." + player + ".Reason", reason);
        bans.set("Ban." + player + ".ID", id);
        bans.set("Ban." + player + ".Banner", banner);
        bans.set("Ban." + player + ".Date", date);
        bans.set("Ban." + player + ".Time", end);
        bans.save();
        bans.reload();
        createBanlog(new Ban(player, reason, id, banner, date, end));
    }

    @Override
    public void mutePlayer(String player, String reason, String banner, int seconds) {
        long end = System.currentTimeMillis() + seconds * 1000L;
        if (seconds == -1) end = -1L;
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        mutes.set("Mute." + player + ".Reason", reason);
        mutes.set("Mute." + player + ".ID", id);
        mutes.set("Mute." + player + ".Banner", banner);
        mutes.set("Mute." + player + ".Date", date);
        mutes.set("Mute." + player + ".Time", end);
        mutes.save();
        mutes.reload();
        createMutelog(new Mute(player, reason, id, banner, date, end));
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        String id = BanSystemAPI.getRandomIDCode();
        String date = BanSystemAPI.getDate();
        warns.set("Warn." + player + "." + id + ".Reason", reason);
        warns.set("Warn." + player + "." + id + ".Creator", creator);
        warns.set("Warn." + player + "." + id + ".Date", date);
        warns.save();
        warns.reload();
    }

    @Override
    public void unbanPlayer(String player) {
        Map<String, Object> map = bans.getSection("Ban").getAllMap();
        map.remove(player);
        bans.set("Ban", map);
        bans.save();
        bans.reload();
    }

    @Override
    public void unmutePlayer(String player) {
        Map<String, Object> map = mutes.getSection("Mute").getAllMap();
        map.remove(player);
        mutes.set("Mute", map);
        mutes.save();
        mutes.reload();
    }

    @Override
    public Ban getBan(String player) {
        String reason = bans.getString("Ban." + player + ".Reason");
        String banID = bans.getString("Ban." + player + ".ID");
        String banner = bans.getString("Ban." + player + ".Banner");
        String date = bans.getString("Ban." + player + ".Date");
        long time = bans.getLong("Ban." + player + ".Time");
        return new Ban(player, reason, banID, banner, date, time);
    }

    @Override
    public Mute getMute(String player) {
        String reason = mutes.getString("Mute." + player + ".Reason");
        String banID = mutes.getString("Mute." + player + ".ID");
        String banner = mutes.getString("Mute." + player + ".Banner");
        String date = mutes.getString("Mute." + player + ".Date");
        long time = mutes.getLong("Mute." + player + ".Time");
        return new Mute(player, reason, banID, banner, date, time);
    }

    @Override
    public void createBanlog(Ban ban) {
        banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Reason", ban.getReason());
        banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Banner", ban.getBanner());
        banlog.set("Banlog." + ban.getPlayer() + "." + ban.getBanID() + ".Date", ban.getDate());
        banlog.save();
        banlog.reload();
    }

    @Override
    public void createMutelog(Mute mute) {
        mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Reason", mute.getReason());
        mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Muter", mute.getMuter());
        mutelog.set("Mutelog." + mute.getPlayer() + "." + mute.getMuteID() + ".Date", mute.getDate());
        mutelog.save();
        mutelog.reload();
    }

    @Override
    public List<Ban> getBanlog(String player) {
        List<Ban> list = new ArrayList<>();
        for (String s : banlog.getSection("Banlog." + player).getAll().getKeys(false)) {
            String reason = banlog.getString("Banlog." + player + "." + s + ".Reason");
            String banner = banlog.getString("Banlog." + player + "." + s + ".Banner");
            String date = banlog.getString("Banlog." + player + "." + s + ".Date");
            list.add(new Ban(player, reason, s, banner, date, 0));
        }
        return list;
    }

    @Override
    public List<Mute> getMutelog(String player) {
        List<Mute> list = new ArrayList<>();
        for (String s : mutelog.getSection("Mutelog." + player).getAll().getKeys(false)) {
            String reason = mutelog.getString("Mutelog." + player + "." + s + ".Reason");
            String banner = mutelog.getString("Mutelog." + player + "." + s + ".Banner");
            String date = mutelog.getString("Mutelog." + player + "." + s + ".Date");
            list.add(new Mute(player, reason, s, banner, date, 0));
        }
        return list;
    }

    @Override
    public List<Warn> getWarnings(String player) {
        List<Warn> list = new ArrayList<>();
        for (String s : warns.getSection("Warn." + player).getAll().getKeys(false)) {
            String reason = warns.getString("Warn." + player + "." + s + ".Reason");
            String creator = warns.getString("Warn." + player + "." + s + ".Creator");
            String date = warns.getString("Warn." + player + "." + s + ".Date");
            list.add(new Warn(player, reason, s, creator, date));
        }
        return list;
    }

    @Override
    public void clearBanlog(String player) {
        Map<String, Object> map = banlog.getSection("Banlog").getAllMap();
        map.remove(player);
        banlog.set("Banlog", map);
        banlog.save();
        banlog.reload();
    }

    @Override
    public void clearMutelog(String player) {
        Map<String, Object> map = mutelog.getSection("Mutelog").getAllMap();
        map.remove(player);
        mutelog.set("Mutelog", map);
        mutelog.save();
        mutelog.reload();
    }

    @Override
    public void clearWarns(String player) {
        Map<String, Object> map = warns.getSection("Warn").getAllMap();
        map.remove(player);
        warns.set("Warn", map);
        warns.save();
        warns.reload();
    }

    @Override
    public void setBanReason(String player, String reason) {
        bans.set("Ban." + player + ".Reason", reason);
        bans.save();
        bans.reload();
    }

    @Override
    public void setMuteReason(String player, String reason) {
        mutes.set("Mute." + player + ".Reason", reason);
        mutes.save();
        mutes.reload();
    }

    @Override
    public void setBanTime(String player, long time) {
        bans.set("Ban." + player + ".Time", time);
        bans.save();
        bans.reload();
    }

    @Override
    public void setMuteTime(String player, long time) {
        mutes.set("Mute." + player + ".Reason", time);
        mutes.save();
        mutes.reload();
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
            if (minutes == 1) {
                minute = Configuration.getAndReplaceNP("Minute");
            }

            if (minutes > 1 && days == 0 && hours == 0) {
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
        return "Yaml";
    }
}

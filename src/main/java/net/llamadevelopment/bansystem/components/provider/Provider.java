package net.llamadevelopment.bansystem.components.provider;

import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.data.*;
import net.llamadevelopment.bansystem.components.language.Language;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class Provider {

    public final LinkedHashMap<String, BanReason> banReasons = new LinkedHashMap<>();
    public final LinkedHashMap<String, MuteReason> muteReasons = new LinkedHashMap<>();
    public final Map<String, Mute> cachedMutes = new HashMap<>();

    public void connect(BanSystem server) {

    }

    public void disconnect(BanSystem server) {

    }

    public void playerIsBanned(String player, Consumer<Boolean> isBanned) {

    }

    public void playerIsMuted(String player, Consumer<Boolean> isMuted) {

    }

    public void banIdExists(String id, boolean history, Consumer<Boolean> exists) {

    }

    public void muteIdExists(String id, boolean history, Consumer<Boolean> exists) {

    }

    public void warnIdExists(String id, Consumer<Boolean> exists) {

    }

    public void banPlayer(String player, String reason, String banner, int seconds) {

    }

    public void mutePlayer(String player, String reason, String banner, int seconds) {

    }

    public void warnPlayer(String player, String reason, String creator) {

    }

    @Deprecated
    public void unbanPlayer(String player) {

    }

    @Deprecated
    public void unmutePlayer(String player) {

    }

    public void unbanPlayer(String player, String executor) {

    }

    public void unmutePlayer(String player, String executor) {

    }

    public void getBan(String player, Consumer<Ban> ban) {

    }

    public void getMute(String player, Consumer<Mute> mute) {

    }

    public void getBanById(String id, boolean history, Consumer<Ban> ban) {

    }

    public void getMuteById(String id, boolean history, Consumer<Mute> mute) {

    }

    public void createBanlog(Ban ban) {

    }

    public void createMutelog(Mute mute) {

    }

    public void getBanLog(String player, Consumer<Set<Ban>> banlog) {

    }

    public void getMuteLog(String player, Consumer<Set<Mute>> mutelog) {

    }

    public void getWarnLog(String player, Consumer<Set<Warn>> warnlog) {

    }

    @Deprecated
    public void clearBanlog(String player) {

    }

    @Deprecated
    public void clearMutelog(String player) {

    }

    @Deprecated
    public void clearWarns(String player) {

    }

    public void clearBanlog(String player, String executor) {

    }

    public void clearMutelog(String player, String executor) {

    }

    public void clearWarns(String player, String executor) {

    }

    @Deprecated
    public void setBanReason(String player, String reason) {

    }

    @Deprecated
    public void setMuteReason(String player, String reason) {

    }

    @Deprecated
    public void setBanTime(String player, long time) {

    }

    @Deprecated
    public void setMuteTime(String player, long time) {

    }

    public void setBanReason(String player, String reason, String executor) {

    }

    public void setMuteReason(String player, String reason, String executor) {

    }

    public void setBanTime(String player, long time, String executor) {

    }

    public void setMuteTime(String player, long time, String executor) {

    }

    @Deprecated
    public void deleteBan(String id) {

    }

    @Deprecated
    public void deleteMute(String id) {

    }

    @Deprecated
    public void deleteWarn(String id) {

    }

    public void deleteBan(String id, String executor) {

    }

    public void deleteMute(String id, String executor) {

    }

    public void deleteWarn(String id, String executor) {

    }

    public String getProvider() {
        return null;
    }

    public String getRandomIDCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 5) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

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

}

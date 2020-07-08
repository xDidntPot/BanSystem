package net.llamadevelopment.bansystem.components.managers.database;

import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;

import java.util.List;

public class Provider {

    public void connect(BanSystem server) {
    }

    public void disconnect(BanSystem server) {
    }

    public boolean playerIsBanned(String player) {
        return false;
    }

    public boolean playerIsMuted(String player) {
        return false;
    }

    public void banPlayer(String player, String reason, String banner, int seconds) {
    }

    public void mutePlayer(String player, String reason, String banner, int seconds) {
    }

    public void warnPlayer(String player, String reason, String creator) {

    }

    public void unbanPlayer(String player) {
    }

    public void unmutePlayer(String player) {
    }

    public Ban getBan(String player) {
        return null;
    }

    public Mute getMute(String player) {
        return null;
    }

    public void createBanlog(Ban ban) {
    }

    public void createMutelog(Mute mute) {
    }

    public List<Ban> getBanlog(String player) {
        return null;
    }

    public List<Mute> getMutelog(String player) {
        return null;
    }

    public List<Warn> getWarnings(String player) {
        return null;
    }

    public void clearBanlog(String player) {

    }

    public void clearMutelog(String player) {

    }

    public void clearWarns(String player) {

    }

    public void setBanReason(String player, String reason) {

    }

    public void setMuteReason(String player, String reason) {

    }

    public void setBanTime(String player, long time) {

    }

    public void setMuteTime(String player, long time) {

    }

    public String getRemainingTime(long duration) {
        return null;
    }

    public String getProvider() {
        return null;
    }

}

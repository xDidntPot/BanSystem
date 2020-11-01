package net.llamadevelopment.bansystem.components.provider;

import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Provider {

    public void connect(BanSystem server) {
    }

    public void disconnect(BanSystem server) {
    }

    public void playerIsBanned(String player, Consumer<Boolean> isBanned) {

    }

    public void playerIsMuted(String player, Consumer<Boolean> isMuted) {

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

    public void getBan(String player, Consumer<Ban> ban) {

    }

    public void getMute(String player, Consumer<Mute> mute) {

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

    public void getBanLog(String player, Consumer<Set<Ban>> banlog) {

    }

    public void getMuteLog(String player, Consumer<Set<Mute>> mutelog) {

    }

    public void getWarnLog(String player, Consumer<Set<Warn>> warnlog) {

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

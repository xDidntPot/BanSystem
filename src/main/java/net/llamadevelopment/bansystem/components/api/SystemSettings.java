package net.llamadevelopment.bansystem.components.api;

import net.llamadevelopment.bansystem.components.data.BanReason;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.MuteReason;

import java.util.HashMap;

public class SystemSettings {

    public HashMap<String, BanReason> banReasons = new HashMap<>();
    public HashMap<String, MuteReason> muteReasons = new HashMap<>();
    public HashMap<String, Mute> cachedMute = new HashMap<>();
    private boolean isDebugMode;
    private String version;
    private int joinDelay;
    private boolean waterdog;

    public SystemSettings(boolean debugMode, String version, int joinDelay, boolean waterdog) {
        setDebugMode(debugMode);
        setVersion(version);
        setJoinDelay(joinDelay);
        setWaterdog(waterdog);
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public String getVersion() {
        return version;
    }

    public int getJoinDelay() {
        return joinDelay;
    }

    public boolean isWaterdog() {
        return waterdog;
    }

    public void setWaterdog(boolean waterdog) {
        this.waterdog = waterdog;
    }

    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setJoinDelay(int joinDelay) {
        this.joinDelay = joinDelay;
    }
}

package net.llamadevelopment.bansystem.components.utils;

public class WarnUtil {

    private String player;
    private String reason;
    private String id;
    private String creator;
    private String date;

    public WarnUtil(String player, String reason, String id, String creator, String date) {
        this.player = player;
        this.reason = reason;
        this.id = id;
        this.creator = creator;
        this.date = date;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public String getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public String getDate() {
        return date;
    }
}

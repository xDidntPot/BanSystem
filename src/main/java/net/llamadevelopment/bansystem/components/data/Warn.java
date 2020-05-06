package net.llamadevelopment.bansystem.components.data;

public class Warn {

    private final String player;
    private final String reason;
    private final String warnID;
    private final String creator;
    private final String date;

    public Warn(String player, String reason, String warnID, String creator, String date) {
        this.player = player;
        this.reason = reason;
        this.warnID = warnID;
        this.creator = creator;
        this.date = date;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public String getWarnID() {
        return warnID;
    }

    public String getCreator() {
        return creator;
    }

    public String getDate() {
        return date;
    }
}

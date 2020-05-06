package net.llamadevelopment.bansystem.components.data;

public class Mute {

    private final String player;
    private final String reason;
    private final String muteID;
    private final String muter;
    private final String date;
    private final long time;

    public Mute(String player, String reason, String muteID, String muter, String date, long time) {
        this.player = player;
        this.reason = reason;
        this.muteID = muteID;
        this.muter = muter;
        this.date = date;
        this.time = time;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public String getMuteID() {
        return muteID;
    }

    public String getMuter() {
        return muter;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

}

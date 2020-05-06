package net.llamadevelopment.bansystem.components.data;

public class Ban {

    private final String player;
    private final String reason;
    private final String banID;
    private final String banner;
    private final String date;
    private final long time;

    public Ban(String player, String reason, String banID, String banner, String date, long time) {
        this.player = player;
        this.reason = reason;
        this.banID = banID;
        this.banner = banner;
        this.date = date;
        this.time = time;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public String getBanID() {
        return banID;
    }

    public String getBanner() {
        return banner;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }
}

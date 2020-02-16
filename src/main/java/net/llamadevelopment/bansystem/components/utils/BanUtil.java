package net.llamadevelopment.bansystem.components.utils;

public class BanUtil {

    private String player;
    private String reason;
    private String id;
    private String banner;
    private String date;
    private Long end;

    public BanUtil(String player, String reason, String id, String banner, String date, Long end) {
        this.player = player;
        this.reason = reason;
        this.id = id;
        this.banner = banner;
        this.date = date;
        this.end = end;
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

    public String getBanner() {
        return banner;
    }

    public String getDate() {
        return date;
    }

    public Long getEnd() {
        return end;
    }
}

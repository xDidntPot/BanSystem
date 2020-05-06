package net.llamadevelopment.bansystem.components.data;

public class MuteReason {

    private final String reason;
    private final String id;
    private final int seconds;

    public MuteReason(String reason, String id, int seconds) {
        this.reason = reason;
        this.id = id;
        this.seconds = seconds;
    }

    public String getReason() {
        return reason;
    }

    public String getId() {
        return id;
    }

    public int getSeconds() {
        return seconds;
    }
}

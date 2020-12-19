package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class BanSystemJoinEvent extends PlayerEvent {

    private final String joiner;
    private final boolean isBanned;
    private static final HandlerList handlers = new HandlerList();

    public BanSystemJoinEvent(String joiner, boolean isBanned) {
        this.joiner = joiner;
        this.isBanned = isBanned;
    }

    public String getJoiner() {
        return joiner;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
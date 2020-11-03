package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class PlayerKickEvent extends PlayerEvent {

    private final String target;
    private final String executor;
    private static final HandlerList handlers = new HandlerList();

    public PlayerKickEvent(String target, String executor) {
        this.target = target;
        this.executor = executor;
    }

    public String getTarget() {
        return target;
    }

    public String getExecutor() {
        return executor;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}

package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class ClearBanlogEvent extends PlayerEvent {

    private final String target;
    private static final HandlerList handlers = new HandlerList();

    public ClearBanlogEvent(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}

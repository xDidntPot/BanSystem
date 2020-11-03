package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import net.llamadevelopment.bansystem.components.data.Warn;

public class PlayerWarnEvent extends PlayerEvent {

    private final Warn warn;
    private static final HandlerList handlers = new HandlerList();

    public PlayerWarnEvent(Warn warn) {
        this.warn = warn;
    }

    public Warn getWarn() {
        return warn;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}

package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import net.llamadevelopment.bansystem.components.data.Ban;

public class PlayerBanEvent extends PlayerEvent {

    private final Ban ban;
    private static final HandlerList handlers = new HandlerList();

    public PlayerBanEvent(Ban ban) {
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}

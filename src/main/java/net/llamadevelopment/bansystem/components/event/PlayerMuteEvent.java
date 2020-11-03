package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import net.llamadevelopment.bansystem.components.data.Mute;

public class PlayerMuteEvent extends PlayerEvent {

    private final Mute mute;
    private static final HandlerList handlers = new HandlerList();

    public PlayerMuteEvent(Mute mute) {
        this.mute = mute;
    }

    public Mute getMute() {
        return mute;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}

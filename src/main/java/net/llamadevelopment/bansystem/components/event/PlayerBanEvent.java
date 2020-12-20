package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystem.components.data.Ban;

@AllArgsConstructor
@Getter
public class PlayerBanEvent extends PlayerEvent {

    private final Ban ban;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

}

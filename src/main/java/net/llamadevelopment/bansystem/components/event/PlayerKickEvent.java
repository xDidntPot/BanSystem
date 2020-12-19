package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerKickEvent extends PlayerEvent {

    private final String target;
    private final String executor;
    private static final HandlerList handlers = new HandlerList();

}

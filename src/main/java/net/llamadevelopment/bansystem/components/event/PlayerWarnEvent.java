package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystem.components.data.Warn;

@AllArgsConstructor
@Getter
public class PlayerWarnEvent extends PlayerEvent {

    private final Warn warn;
    private static final HandlerList handlers = new HandlerList();

}

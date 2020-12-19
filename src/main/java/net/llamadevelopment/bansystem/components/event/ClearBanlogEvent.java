package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClearBanlogEvent extends Event {

    private final String target;
    private static final HandlerList handlers = new HandlerList();

}

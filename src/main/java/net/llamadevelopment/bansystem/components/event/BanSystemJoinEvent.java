package net.llamadevelopment.bansystem.components.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BanSystemJoinEvent extends PlayerEvent {

    private final Player joiner;
    private final boolean isBanned;
    private static final HandlerList handlers = new HandlerList();

}
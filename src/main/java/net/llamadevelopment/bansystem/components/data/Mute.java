package net.llamadevelopment.bansystem.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Mute {

    private final String player;
    private final String reason;
    private final String muteID;
    private final String muter;
    private final String date;
    private final long time;

}

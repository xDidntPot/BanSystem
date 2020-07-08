package net.llamadevelopment.bansystem.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Ban {

    private final String player;
    private final String reason;
    private final String banID;
    private final String banner;
    private final String date;
    private final long time;

}

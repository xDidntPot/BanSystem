package net.llamadevelopment.bansystem.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Warn {

    private final String player;
    private final String reason;
    private final String warnID;
    private final String creator;
    private final String date;

}

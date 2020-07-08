package net.llamadevelopment.bansystem.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BanReason {

    private final String reason;
    private final String id;
    private final int seconds;

}

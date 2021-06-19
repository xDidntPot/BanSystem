package net.llamadevelopment.bansystem.components.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.llamadevelopment.bansystem.components.forms.FormWindows;
import net.llamadevelopment.bansystem.components.provider.Provider;

@Getter
@RequiredArgsConstructor
public class API {

    private final Provider provider;
    private final FormWindows formWindows;
    private final int joinDelay;

}

package net.llamadevelopment.bansystem.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import lombok.RequiredArgsConstructor;
import net.llamadevelopment.bansystem.components.forms.simple.SimpleForm;
import net.llamadevelopment.bansystem.components.provider.Provider;

@RequiredArgsConstructor
public class FormWindows {

    private final Provider provider;

    public void openWaveDashboard(Player player) {
        SimpleForm form = new SimpleForm.Builder("", "")
                .addButton(new ElementButton(""), this::openAddWavePlayer)
                .addButton(new ElementButton(""), this::openRemoveWavePlayer)
                .addButton(new ElementButton(""), e -> {

                })
                .build();
        form.send(player);
    }

    public void openAddWavePlayer(Player player) {

    }

    public void openRemoveWavePlayer(Player player) {

    }

}

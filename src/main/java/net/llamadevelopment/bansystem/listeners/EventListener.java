package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Mute;

public class EventListener implements Listener {

    private final BanSystem instance = BanSystem.getInstance();
    private final SystemSettings settings = BanSystemAPI.getSystemSettings();

    @EventHandler
    public void on(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        this.instance.provider.playerIsBanned(player.getName(), isBanned -> {
            if (isBanned) {
                this.instance.getServer().getScheduler().scheduleDelayedTask(this.instance, () -> {
                    this.instance.provider.getBan(player.getName(), ban -> {
                        if (ban.getTime() != -1) {
                            if (ban.getTime() < System.currentTimeMillis()) {
                                this.instance.provider.unbanPlayer(player.getName());
                                return;
                            }
                        }
                        player.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), BanSystemAPI.getRemainingTime(ban.getTime())), false);
                    });
                }, this.settings.getJoinDelay());
            } else {
                this.instance.provider.playerIsMuted(player.getName(), isMuted -> {
                    if (isMuted) {
                        this.instance.provider.getMute(player.getName(), mute -> this.settings.cachedMute.put(player.getName(), mute));
                    }
                });
            }
        });
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        if (this.settings.cachedMute.get(event.getPlayer().getName()) != null) {
            Mute mute = this.settings.cachedMute.get(event.getPlayer().getName());
            if (mute.getTime() < System.currentTimeMillis()) {
                this.instance.provider.unmutePlayer(mute.getPlayer());
                this.settings.cachedMute.remove(mute.getPlayer());
                return;
            }
            event.getPlayer().sendMessage(Language.getNP("MuteScreen", mute.getReason(), mute.getMuteID(), BanSystemAPI.getRemainingTime(mute.getTime())));
            event.setCancelled(true);
        }
    }

}

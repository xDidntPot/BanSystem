package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import lombok.RequiredArgsConstructor;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.event.BanSystemJoinEvent;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.data.Mute;


@RequiredArgsConstructor
public class EventListener implements Listener {

    private final BanSystem instance;

    @EventHandler
    public void on(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        this.instance.provider.playerIsBanned(player.getName(), isBanned -> {
            if (isBanned) {
                this.instance.getServer().getScheduler().scheduleDelayedTask(this.instance, () -> this.instance.provider.getBan(player.getName(), ban -> {
                    if (ban.getTime() != -1) {
                        if (ban.getTime() < System.currentTimeMillis()) {
                            this.instance.provider.unbanPlayer(player.getName(), "BanSystem");
                            return;
                        }
                    }
                    Server.getInstance().getPluginManager().callEvent(new BanSystemJoinEvent(player, true));
                    player.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), this.instance.provider.getRemainingTime(ban.getTime())), false);
                }), BanSystem.getApi().getJoinDelay());
            } else {
                Server.getInstance().getPluginManager().callEvent(new BanSystemJoinEvent(player, false));
                this.instance.provider.playerIsMuted(player.getName(), isMuted -> {
                    if (isMuted) {
                        this.instance.provider.getMute(player.getName(), mute -> this.instance.provider.cachedMutes.put(player.getName(), mute));
                    }
                });
            }
        });
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        if (this.instance.provider.cachedMutes.get(event.getPlayer().getName()) != null) {
            Mute mute = this.instance.provider.cachedMutes.get(event.getPlayer().getName());
            if (mute.getTime() < System.currentTimeMillis()) {
                this.instance.provider.unmutePlayer(mute.getPlayer(), "BanSystem");
                this.instance.provider.cachedMutes.remove(mute.getPlayer());
                return;
            }
            event.getPlayer().sendMessage(Language.getNP("MuteScreen", mute.getReason(), mute.getMuteID(), this.instance.provider.getRemainingTime(mute.getTime())));
            event.setCancelled(true);
        }
    }

}

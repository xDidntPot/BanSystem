package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.scheduler.AsyncTask;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class EventListener implements Listener {

    BanSystem instance = BanSystem.getInstance();
    SystemSettings settings = BanSystemAPI.getSystemSettings();
    Provider api = BanSystemAPI.getProvider();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        String player = event.getPlayer().getName();
        instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
            @Override
            public void onRun() {
                if (api.playerIsBanned(player)) {
                    instance.getServer().getScheduler().scheduleDelayedTask(instance, () -> {
                        Ban ban = api.getBan(player);
                        event.getPlayer().kick(Configuration.getAndReplaceNP("BanScreen", ban.getBanID(), ban.getReason(), api.getRemainingTime(ban.getTime())), false);
                    }, settings.getJoinDelay());
                } else if (api.playerIsMuted(player)) {
                    Mute mute = api.getMute(player);
                    settings.cachedMute.remove(player);
                    settings.cachedMute.put(player, mute);
                }
            }
        });
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        if (settings.cachedMute.get(event.getPlayer().getName()) != null) {
            Mute mute = settings.cachedMute.get(event.getPlayer().getName());
            if (mute.getTime() < System.currentTimeMillis()) {
                api.unmutePlayer(mute.getPlayer());
                settings.cachedMute.remove(mute.getPlayer());
                return;
            }
            event.getPlayer().sendMessage(Configuration.getAndReplaceNP("MuteScreen", mute.getReason(), mute.getMuteID(), BanSystemAPI.getProvider().getRemainingTime(mute.getTime())));
            event.setCancelled(true);
        }
    }

}

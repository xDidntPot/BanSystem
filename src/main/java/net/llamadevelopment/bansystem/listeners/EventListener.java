package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    BanSystem instance = BanSystem.getInstance();
    SystemSettings settings = BanSystemAPI.getSystemSettings();
    Provider api = BanSystemAPI.getProvider();

    @EventHandler
    public void on(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> {
            if (api.playerIsBanned(player.getName())) {
                instance.getServer().getScheduler().scheduleDelayedTask(instance, () -> {
                    Ban ban = api.getBan(player.getName());
                    if (ban.getTime() != -1) {
                        if (ban.getTime() < System.currentTimeMillis()) {
                            api.unbanPlayer(player.getName());
                            return;
                        }
                    }
                    if (settings.isWaterdog()) {
                        ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        try {
                            dataOutputStream.writeUTF("banplayer");
                            dataOutputStream.writeUTF(player.getName());
                            dataOutputStream.writeUTF(ban.getReason());
                            dataOutputStream.writeUTF(ban.getBanID());
                            dataOutputStream.writeUTF(api.getRemainingTime(ban.getTime()));
                            customEventPacket.eventName = "bansystembridge:main";
                            customEventPacket.eventData = outputStream.toByteArray();
                            player.dataPacket(customEventPacket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else player.kick(Configuration.getAndReplaceNP("BanScreen", ban.getReason(), ban.getBanID(), api.getRemainingTime(ban.getTime())), false);
                }, settings.getJoinDelay());
            } else if (api.playerIsMuted(player.getName())) {
                Mute mute = api.getMute(player.getName());
                settings.cachedMute.put(player.getName(), mute);
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

package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.provider.Provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    private final BanSystem instance = BanSystem.getInstance();
    private final SystemSettings settings = BanSystemAPI.getSystemSettings();
    private final Provider api = BanSystemAPI.getProvider();

    @EventHandler
    public void on(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> {
            if (this.api.playerIsBanned(player.getName())) {
                this.instance.getServer().getScheduler().scheduleDelayedTask(this.instance, () -> {
                    Ban ban = this.api.getBan(player.getName());
                    if (ban.getTime() != -1) {
                        if (ban.getTime() < System.currentTimeMillis()) {
                            this.api.unbanPlayer(player.getName());
                            return;
                        }
                    }
                    if (this.settings.isWaterdog()) {
                        ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        try {
                            dataOutputStream.writeUTF("banplayer");
                            dataOutputStream.writeUTF(player.getName());
                            dataOutputStream.writeUTF(ban.getReason());
                            dataOutputStream.writeUTF(ban.getBanID());
                            dataOutputStream.writeUTF(this.api.getRemainingTime(ban.getTime()));
                            customEventPacket.eventName = "bansystembridge:main";
                            customEventPacket.eventData = outputStream.toByteArray();
                            player.dataPacket(customEventPacket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else player.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), this.api.getRemainingTime(ban.getTime())), false);
                }, this.settings.getJoinDelay());
            } else if (this.api.playerIsMuted(player.getName())) {
                Mute mute = this.api.getMute(player.getName());
                this.settings.cachedMute.put(player.getName(), mute);
            }
        });
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        if (this.settings.cachedMute.get(event.getPlayer().getName()) != null) {
            Mute mute = this.settings.cachedMute.get(event.getPlayer().getName());
            if (mute.getTime() < System.currentTimeMillis()) {
                this.api.unmutePlayer(mute.getPlayer());
                this.settings.cachedMute.remove(mute.getPlayer());
                return;
            }
            event.getPlayer().sendMessage(Language.getNP("MuteScreen", mute.getReason(), mute.getMuteID(), BanSystemAPI.getProvider().getRemainingTime(mute.getTime())));
            event.setCancelled(true);
        }
    }

}

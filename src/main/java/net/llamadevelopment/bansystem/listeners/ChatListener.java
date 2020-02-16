package net.llamadevelopment.bansystem.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.MuteManager;
import net.llamadevelopment.bansystem.components.utils.MessageUtil;
import net.llamadevelopment.bansystem.components.utils.MutedPlayer;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (BanSystem.getInstance().mutedCache.containsKey(player)) {
            event.setCancelled(true);
            MutedPlayer mutedPlayer = BanSystem.getInstance().mutedCache.get(player);
            player.sendMessage(MessageUtil.muteScreen(mutedPlayer.getReason(), mutedPlayer.getId(), MuteManager.getRemainingTime(mutedPlayer.getEnd()), mutedPlayer.getBanner()));
            long current = System.currentTimeMillis();
            long end = mutedPlayer.getEnd();
            if (end < current) {
                BanSystem.getInstance().mutedCache.remove(player);
                MuteManager.unMute(player.getName());
            }
        }
    }
}

package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.provider.Provider;

import java.util.concurrent.CompletableFuture;

public class EditmuteCommand extends Command {

    public EditmuteCommand(String name) {
        super(name, "Edit the mute of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("editType", false, new String[] {"reason", "time"})
        });
        setPermission("bansystem.command.editmute");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(getPermission())) {
            if (args.length >= 3) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("reason")) {
                    CompletableFuture.runAsync(() -> {
                        if (api.playerIsMuted(player)) {
                            String reason = "";
                            for (int i = 2; i < args.length; ++i) reason = reason + args[i] + " ";
                            api.setMuteReason(player, reason);
                            Player onlinePlayer = Server.getInstance().getPlayer(player);
                            if (onlinePlayer != null) {
                                settings.cachedMute.remove(player);
                                settings.cachedMute.put(player, api.getMute(player));
                            }
                            sender.sendMessage(Language.get("ReasonSet"));
                        } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
                    });
                } else if (args.length == 4 && args[1].equalsIgnoreCase("time")) {
                    CompletableFuture.runAsync(() -> {
                        if (api.playerIsMuted(player)) {
                            try {
                                String type = args[2];
                                int time = Integer.parseInt(args[3]);
                                int seconds = 0;
                                if (type.equalsIgnoreCase("days")) seconds = time * 86400;
                                else if (type.equalsIgnoreCase("hours")) seconds = time * 3600;
                                else {
                                    sender.sendMessage(Language.get("EditmuteCommandUsage1", getName()));
                                    sender.sendMessage(Language.get("EditmuteCommandUsage2", getName()));
                                    return;
                                }
                                long end = System.currentTimeMillis() + seconds * 1000L;
                                api.setMuteTime(player, end);
                                Player onlinePlayer = Server.getInstance().getPlayer(player);
                                if (onlinePlayer != null) {
                                    settings.cachedMute.remove(player);
                                    settings.cachedMute.put(player, api.getMute(player));
                                }
                                sender.sendMessage(Language.get("TimeSet"));
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(Language.get("InvalidNumber"));
                            }
                        } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
                    });
                } else {
                    sender.sendMessage(Language.get("EditmuteCommandUsage1", getName()));
                    sender.sendMessage(Language.get("EditmuteCommandUsage2", getName()));
                }
            } else {
                sender.sendMessage(Language.get("EditmuteCommandUsage1", getName()));
                sender.sendMessage(Language.get("EditmuteCommandUsage2", getName()));
            }
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}

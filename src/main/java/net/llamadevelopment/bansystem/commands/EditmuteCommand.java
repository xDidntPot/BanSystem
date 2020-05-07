package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class EditmuteCommand extends Command {

    public EditmuteCommand(String name) {
        super(name, "Edit the mute of a player.");
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
                    if (api.playerIsMuted(player)) {
                        String reason = "";
                        for (int i = 2; i < args.length; ++i) reason = reason + args[i] + " ";
                        api.setMuteReason(player, reason);
                        Player onlinePlayer = Server.getInstance().getPlayer(player);
                        if (onlinePlayer != null) {
                            settings.cachedMute.remove(player);
                            settings.cachedMute.put(player, api.getMute(player));
                        }
                        sender.sendMessage(Configuration.getAndReplace("ReasonSet"));
                    } else sender.sendMessage(Configuration.getAndReplace("PlayerIsNotMuted"));
                } else if (args.length == 4 && args[1].equalsIgnoreCase("time")) {
                    if (api.playerIsMuted(player)) {
                        try {
                            String type = args[2];
                            int time = Integer.parseInt(args[3]);
                            int seconds = 0;
                            if (type.equalsIgnoreCase("days")) seconds = time * 86400;
                            else if (type.equalsIgnoreCase("hours")) seconds = time * 3600;
                            else {
                                sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage1", getName()));
                                sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage2", getName()));
                                return true;
                            }
                            long end = System.currentTimeMillis() + seconds * 1000L;
                            api.setMuteTime(player, end);
                            Player onlinePlayer = Server.getInstance().getPlayer(player);
                            if (onlinePlayer != null) {
                                settings.cachedMute.remove(player);
                                settings.cachedMute.put(player, api.getMute(player));
                            }
                            sender.sendMessage(Configuration.getAndReplace("TimeSet"));
                        } catch (NumberFormatException exception) {
                            sender.sendMessage(Configuration.getAndReplace("InvalidNumber"));
                        }
                    } else sender.sendMessage(Configuration.getAndReplace("PlayerIsNotMuted"));
                } else {
                    sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage1", getName()));
                    sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage2", getName()));
                }
            } else {
                sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage1", getName()));
                sender.sendMessage(Configuration.getAndReplace("EditmuteCommandUsage2", getName()));
            }
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

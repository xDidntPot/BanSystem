package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class TempmuteCommand extends Command {

    public TempmuteCommand(String name) {
        super(name, "Mute a player temporary.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("timeType", false, new String[]{"days", "hours"}),
                new CommandParameter("time", CommandParamType.INT, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.tempmute");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(getPermission())) {
            if (args.length >= 4) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("days") || args[1].equalsIgnoreCase("hours")) {
                    String timeString = args[1];
                    try {
                        int time = Integer.parseInt(args[2]);
                        int seconds = 0;
                        String reason = "";
                        for (int i = 3; i < args.length; ++i) reason = reason + args[i] + " ";
                        if (timeString.equalsIgnoreCase("days")) seconds = time * 86400;
                        if (timeString.equalsIgnoreCase("hours")) seconds = time * 3600;
                        if (api.playerIsMuted(player)) {
                            sender.sendMessage(Language.get("PlayerIsMuted"));
                            return true;
                        }
                        String finalReason = reason;
                        int finalSeconds = seconds;
                        api.mutePlayer(player, finalReason, sender.getName(), finalSeconds);
                        sender.sendMessage(Language.get("PlayerMuted", player));
                        Player onlinePlayer = Server.getInstance().getPlayer(player);
                        if (onlinePlayer != null) {
                            Mute mute = api.getMute(player);
                            settings.cachedMute.put(player, mute);
                        }
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(Language.get("InvalidNumber"));
                    }
                } else sender.sendMessage(Language.get("TempmuteCommandUsage", getName()));
            } else sender.sendMessage(Language.get("TempmuteCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}

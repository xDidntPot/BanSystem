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
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.MuteReason;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class MuteCommand extends Command {

    public MuteCommand(String name) {
        super(name, "Mute a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.mute");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 2) {
                String player = args[0];
                String reason = args[1];
                if (api.playerIsMuted(player)) {
                    sender.sendMessage(Configuration.getAndReplace("PlayerIsMuted"));
                    return true;
                }
                if (settings.muteReasons.get(reason) == null) {
                    sender.sendMessage(Configuration.getAndReplace("ReasonNotFound"));
                    return true;
                }
                MuteReason muteReason = settings.muteReasons.get(reason);
                api.mutePlayer(player, muteReason.getReason(), sender.getName(), muteReason.getSeconds());
                sender.sendMessage(Configuration.getAndReplace("PlayerMuted", player));
                Player onlinePlayer = Server.getInstance().getPlayer(player);
                if (onlinePlayer != null) {
                    Mute mute = api.getMute(player);
                    settings.cachedMute.put(player, mute);
                }
            } else {
                settings.muteReasons.values().forEach(reason -> sender.sendMessage(Configuration.getAndReplace("ReasonFormat", reason.getId(), reason.getReason())));
                sender.sendMessage(Configuration.getAndReplace("MuteCommandUsage", getName()));
            }
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

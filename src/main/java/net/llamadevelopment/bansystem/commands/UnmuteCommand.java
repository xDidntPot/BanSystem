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
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class UnmuteCommand extends Command {

    public UnmuteCommand(String name) {
        super(name, "Cancel an active mute.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.unmute");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                if (api.playerIsMuted(player)) {
                    api.unmutePlayer(player);
                    Player onlinePlayer = Server.getInstance().getPlayer(player);
                    if (onlinePlayer != null) {
                        SystemSettings settings = BanSystemAPI.getSystemSettings();
                        settings.cachedMute.remove(onlinePlayer.getName());
                    }
                    sender.sendMessage(Language.get("PlayerUnmuted", player));
                } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
            } else sender.sendMessage(Language.get("UnmuteCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}

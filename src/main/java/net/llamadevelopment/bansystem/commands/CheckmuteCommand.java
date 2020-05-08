package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class CheckmuteCommand extends Command {

    public CheckmuteCommand(String name) {
        super(name, "Check if a player is muted.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.checkmute");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                if (api.playerIsMuted(player)) {
                    Mute mute = api.getMute(player);
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteInfo", player));
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteReason", mute.getReason()));
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteID", mute.getMuteID()));
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteMuter", mute.getMuter()));
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteDate", mute.getDate()));
                    sender.sendMessage(Configuration.getAndReplace("CheckmuteTime", api.getRemainingTime(mute.getTime())));
                } else sender.sendMessage(Configuration.getAndReplace("PlayerNotMuted"));
            } else sender.sendMessage(Configuration.getAndReplace("CheckmuteCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

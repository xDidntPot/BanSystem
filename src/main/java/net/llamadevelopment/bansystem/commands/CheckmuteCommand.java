package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.util.concurrent.CompletableFuture;

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
                CompletableFuture.runAsync(() -> {
                    if (api.playerIsMuted(player)) {
                        Mute mute = api.getMute(player);
                        sender.sendMessage(Language.get("CheckmuteInfo", player));
                        sender.sendMessage(Language.get("CheckmuteReason", mute.getReason()));
                        sender.sendMessage(Language.get("CheckmuteID", mute.getMuteID()));
                        sender.sendMessage(Language.get("CheckmuteMuter", mute.getMuter()));
                        sender.sendMessage(Language.get("CheckmuteDate", mute.getDate()));
                        sender.sendMessage(Language.get("CheckmuteTime", api.getRemainingTime(mute.getTime())));
                    } else sender.sendMessage(Language.get("PlayerNotMuted"));
                });
            } else sender.sendMessage(Language.get("CheckmuteCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}

package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.util.concurrent.CompletableFuture;

public class MutelogCommand extends Command {

    public MutelogCommand(String name) {
        super(name, "Get the mute history of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.mutelog");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                CompletableFuture.runAsync(() -> {
                    int i = api.getMutelog(player).size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("NoDataFound"));
                        return;
                    }
                    sender.sendMessage(Language.get("MutelogInfo", player, i));
                    api.getMutelog(player).forEach(mute -> {
                        sender.sendMessage(Language.get("MutelogPlaceholder"));
                        sender.sendMessage(Language.get("MutelogReason", mute.getReason()));
                        sender.sendMessage(Language.get("MutelogID", mute.getMuteID()));
                        sender.sendMessage(Language.get("MutelogMuter", mute.getMuter()));
                        sender.sendMessage(Language.get("MutelogDate", mute.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("MutelogCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}

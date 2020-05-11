package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;
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
                        sender.sendMessage(Configuration.getAndReplace("NoDataFound"));
                        return;
                    }
                    sender.sendMessage(Configuration.getAndReplace("MutelogInfo", player, i));
                    api.getMutelog(player).forEach(mute -> {
                        sender.sendMessage(Configuration.getAndReplace("MutelogPlaceholder"));
                        sender.sendMessage(Configuration.getAndReplace("MutelogReason", mute.getReason()));
                        sender.sendMessage(Configuration.getAndReplace("MutelogID", mute.getMuteID()));
                        sender.sendMessage(Configuration.getAndReplace("MutelogMuter", mute.getMuter()));
                        sender.sendMessage(Configuration.getAndReplace("MutelogDate", mute.getDate()));
                    });
                });
            } else sender.sendMessage(Configuration.getAndReplace("MutelogCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

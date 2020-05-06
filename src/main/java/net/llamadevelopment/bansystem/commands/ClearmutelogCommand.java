package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class ClearmutelogCommand extends Command {

    public ClearmutelogCommand(String name) {
        super(name, "Clear all mute entries of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.clearmutelog");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                int i = api.getMutelog(player).size();
                if (i == 0) {
                    sender.sendMessage(Configuration.getAndReplace("NoDataFound"));
                    return true;
                }
                api.clearMutelog(player);
                sender.sendMessage(Configuration.getAndReplace("Clearmutelog", i));
            } else sender.sendMessage(Configuration.getAndReplace("ClearmutelogCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

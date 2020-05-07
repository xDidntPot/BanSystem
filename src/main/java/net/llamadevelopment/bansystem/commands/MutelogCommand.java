package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.AsyncTask;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

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
        BanSystem instance = BanSystem.getInstance();
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                instance.getServer().getScheduler().scheduleAsyncTask(instance, new AsyncTask() {
                    @Override
                    public void onRun() {
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
                    }
                });
            } else sender.sendMessage(Configuration.getAndReplace("MutelogCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

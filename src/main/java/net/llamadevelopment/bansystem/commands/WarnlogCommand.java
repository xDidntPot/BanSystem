package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.AsyncTask;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class WarnlogCommand extends Command {

    public WarnlogCommand(String name) {
        super(name, "Get the warn history of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.warnlog");
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
                        int i = api.getWarnings(player).size();
                        if (i == 0) {
                            sender.sendMessage(Configuration.getAndReplace("NoDataFound"));
                            return;
                        }
                        sender.sendMessage(Configuration.getAndReplace("WarnlogInfo", player, i));
                        api.getWarnings(player).forEach(warn -> {
                            sender.sendMessage(Configuration.getAndReplace("WarnlogPlaceholder"));
                            sender.sendMessage(Configuration.getAndReplace("WarnlogReason", warn.getReason()));
                            sender.sendMessage(Configuration.getAndReplace("WarnlogID", warn.getWarnID()));
                            sender.sendMessage(Configuration.getAndReplace("WarnlogCreator", warn.getCreator()));
                            sender.sendMessage(Configuration.getAndReplace("WarnlogDate", warn.getDate()));
                        });
                    }
                });
            } else sender.sendMessage(Configuration.getAndReplace("WarnlogCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}

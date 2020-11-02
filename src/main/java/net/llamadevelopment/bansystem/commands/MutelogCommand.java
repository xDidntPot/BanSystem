package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class MutelogCommand extends PluginCommand<BanSystem> {

    public MutelogCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Mutelog.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Mutelog.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Mutelog.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Mutelog.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.getMuteLog(player, mutelog -> {
                    int i = mutelog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("NoDataFound"));
                        return;
                    }
                    sender.sendMessage(Language.get("MutelogInfo", player, i));
                    mutelog.forEach(mute -> {
                        sender.sendMessage(Language.get("MutelogPlaceholder"));
                        sender.sendMessage(Language.get("MutelogReason", mute.getReason()));
                        sender.sendMessage(Language.get("MutelogID", mute.getMuteID()));
                        sender.sendMessage(Language.get("MutelogMuter", mute.getMuter()));
                        sender.sendMessage(Language.get("MutelogDate", mute.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("MutelogCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

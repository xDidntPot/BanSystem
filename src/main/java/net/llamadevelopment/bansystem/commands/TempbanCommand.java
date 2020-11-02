package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class TempbanCommand extends PluginCommand<BanSystem> {

    public TempbanCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Tempban.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("timeType", false, new String[]{"days", "hours"}),
                new CommandParameter("time", CommandParamType.INT, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Tempban.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Tempban.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Tempban.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 4) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("days") || args[1].equalsIgnoreCase("hours")) {
                    String timeString = args[1];
                    try {
                        int time = Integer.parseInt(args[2]);
                        int seconds = 0;
                        String reason = "";
                        for (int i = 3; i < args.length; ++i) reason = reason + args[i] + " ";
                        if (timeString.equalsIgnoreCase("days")) seconds = time * 86400;
                        if (timeString.equalsIgnoreCase("hours")) seconds = time * 3600;
                        String finalReason1 = reason;
                        int finalSeconds1 = seconds;
                        this.getPlugin().provider.playerIsBanned(player, isBanned -> {
                            if (isBanned) {
                                sender.sendMessage(Language.get("PlayerIsBanned"));
                                return;
                            }
                            this.getPlugin().provider.banPlayer(player, finalReason1, sender.getName(), finalSeconds1);
                            sender.sendMessage(Language.get("PlayerBanned", player));
                        });
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(Language.get("InvalidNumber"));
                    }
                } else sender.sendMessage(Language.get("TempbanCommandUsage", this.getName()));
            } else sender.sendMessage(Language.get("TempbanCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

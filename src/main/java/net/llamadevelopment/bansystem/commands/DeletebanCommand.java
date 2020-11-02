package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class DeletebanCommand extends PluginCommand<BanSystem> {

    public DeletebanCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Deleteban.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("id", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Deleteban.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Deleteban.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Deleteban.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String id = args[0];
                this.getPlugin().provider.banIdExists(id, true, exists -> {
                    if (exists) {
                        this.getPlugin().provider.deleteBan(id);
                        sender.sendMessage(Language.get("BanDeleted", id));
                        return;
                    }
                    sender.sendMessage(Language.get("IdNotFound", id));
                });
            } else sender.sendMessage(Language.get("DeletebanCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

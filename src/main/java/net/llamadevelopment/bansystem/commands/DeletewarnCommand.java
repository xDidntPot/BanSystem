package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class DeletewarnCommand extends PluginCommand<BanSystem> {

    public DeletewarnCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Deletewarn.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("id", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Deletewarn.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Deletewarn.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Deletewarn.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String id = args[0];
                this.getPlugin().provider.warnIdExists(id, exists -> {
                    if (exists) {
                        this.getPlugin().provider.deleteWarn(id, sender.getName());
                        sender.sendMessage(Language.get("WarnDeleted", id));
                        return;
                    }
                    sender.sendMessage(Language.get("IdNotFound", id));
                });
            } else sender.sendMessage(Language.get("DeletewarnCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}

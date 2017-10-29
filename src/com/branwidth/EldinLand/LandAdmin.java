package com.branwidth.EldinLand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LandAdmin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        String noPermMessage = Main.getPlugin().getConfig().getString("MessagesConfig.NoPermission");

        if (player.hasPermission("EldinLand.Admin")) {
            player.sendMessage("§6|-- /LandAdmin Help --|");
            player.sendMessage("§aList of commands:");
            player.sendMessage("Placeholder");
        } else {
            player.sendMessage(noPermMessage);
        }

        return true;
    }
}

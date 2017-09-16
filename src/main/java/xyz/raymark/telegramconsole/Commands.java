package xyz.raymark.telegramconsole;

import net.masfik.plugins.TelegramCore.Spigot.Events.TelegramMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class Commands implements Listener {
    private final TelegramConsole plugin;

    Commands(TelegramConsole plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTelegramCommand(TelegramMessageEvent e) {
        if (e.getMessage().getChat().getID().equalsIgnoreCase(String.valueOf(plugin.config.get("chatid"))) && e.getBot().equals(plugin.bot)) {
            String message = e.getMessage().getText();
            if (message.charAt(0) == '/' && message.length() > 1) {
                if(message.equalsIgnoreCase("/help") || message.equalsIgnoreCase("/?")) {
                    plugin.bot.sendMessage("The /help command is restricted because it generates a long response.");
                } else if(message.equalsIgnoreCase("/rl") || message.equalsIgnoreCase("/reload")){
                    plugin.bot.sendMessage("The reload command is disabled due to potential problems it may cause with multi-threading (it breaks TelegramCore).\n" +
                            "Please use /restart.");
                } else {
                    plugin.server.dispatchCommand(plugin.console, message.substring(1));
                }
            }
        }
    }
}

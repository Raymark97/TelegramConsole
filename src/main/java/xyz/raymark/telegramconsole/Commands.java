package xyz.raymark.telegramconsole;

import net.masfik.plugins.TelegramCore.Events.TelegramMessageEvent;
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
                plugin.server.dispatchCommand(plugin.console, message.substring(1));
            }
        }
    }
}

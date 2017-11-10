package xyz.raymark.telegramconsole;

import net.masfik.plugins.TelegramCore.Exceptions.InvalidIDException;
import net.masfik.plugins.TelegramCore.Exceptions.Markup.ButtonException;
import net.masfik.plugins.TelegramCore.Exceptions.Markup.InvalidMarkupException;
import net.masfik.plugins.TelegramCore.Spigot.Events.TelegramMessageEvent;
import net.masfik.plugins.TelegramCore.Types.Message.ReplyMarkup.InlineKeyboard.InlineKeyboardButton;
import net.masfik.plugins.TelegramCore.Types.Message.ReplyMarkup.InlineKeyboard.InlineKeyboardMarkup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class Commands implements Listener {
    private final TelegramConsole plugin;
    private  String chatid;

    Commands(TelegramConsole plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.chatid = plugin.config.getString("chatid");
    }

    @EventHandler
    public void onTelegramCommand(TelegramMessageEvent e) throws InvalidIDException, InvalidMarkupException, ButtonException {
        if (e.getMessage().getChat().getID().equalsIgnoreCase(String.valueOf(chatid)) && e.getBot().equals(plugin.bot)) {
            String message = e.getMessage().getText();
            if (message.charAt(0) == '/' && message.length() > 1) {
                String cmd = message.substring(1);
                if(cmd.equalsIgnoreCase("tgmenu")) {
                    plugin.bot.sendMessage(chatid, new InlineKeyboardMarkup()
                            .addRow(new InlineKeyboardButton("bottone 1"), new InlineKeyboardButton("bottone 2"))
                            .addRow(new InlineKeyboardButton("bottone 3"), new InlineKeyboardButton("bottone 4"))
                            .addRow(new InlineKeyboardButton("bottone 5"), new InlineKeyboardButton("bottone 6"))
                            , "Menu: ");
                }
                plugin.server.dispatchCommand(plugin.console, message.substring(1));
                plugin.console.sendMessage("[Telegram] Command: "+message.substring(1));
            }
        }
    }
}

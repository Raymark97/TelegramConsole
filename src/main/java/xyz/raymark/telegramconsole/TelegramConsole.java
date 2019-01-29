package xyz.raymark.telegramconsole;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.raymark.tgsuite.tgcore.BotNotFoundException;
import xyz.raymark.tgsuite.tgcore.TelegramCore;

@SuppressWarnings("unused")
public class TelegramConsole extends JavaPlugin {
    private Server server = getServer();
    private ConsoleCommandSender console = server.getConsoleSender();
    private FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        //config
        config.addDefault("token", "insert_here_your_bot_token.");
        config.addDefault("chatid", "insert here your chat id");
        config.addDefault("separed-exceptions", true);
        config.addDefault("separed-exceptions.chat-id", "insert here your chat id");
        config.addDefault("show-date", false);
        config.options().copyDefaults(true);
        saveConfig();

        try {
            TelegramCore.getBot().getEvents().registerTextMessageHandler((textMessage)->{
                if (textMessage.getChat().getId() == config.getLong("chatid")) {
                    String message = textMessage.getText().toHtmlString();
                    if (message.charAt(0) == '/' && message.length() > 1) {
                        Bukkit.getScheduler().callSyncMethod( this, () -> Bukkit.dispatchCommand(console, message.substring(1)));
                    }
                }
            });
        } catch (BotNotFoundException e) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Thread th = new Thread(new ConsoleReader(config.getString("chatid"), config.getBoolean("show-date"), config.getBoolean("separed-exception"), config.getString("separed-exception.chat-id")));
        th.setDaemon(true);
        th.start();
        th.setName("TelegramConsole");
    }
}

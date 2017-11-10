package xyz.raymark.telegramconsole;

import net.masfik.plugins.TelegramCore.Bot;
import net.masfik.plugins.TelegramCore.Exceptions.Bot.WebhookException;
import net.masfik.plugins.TelegramCore.Exceptions.InvalidIDException;
import net.masfik.plugins.TelegramCore.Exceptions.InvalidTokenException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

public class TelegramConsole extends JavaPlugin {
    Server server = getServer();
    ConsoleCommandSender console = server.getConsoleSender();
    FileConfiguration config = getConfig();
    Bot bot;

    @Override
    public void onEnable() {

        //config
        config.addDefault("token", "insert_here_your_bot_token.");
        config.addDefault("chatid", "insert here your chat id");
        config.addDefault("separed-exceptions", true);
        config.addDefault("separed-exceptions-chatid", "insert here your chat id");
        config.addDefault("show-date", false);
        config.options().copyDefaults(true);
        saveConfig();

        new Commands(this);

        String token = config.getString("token");

        try {
            bot = new Bot(token);
            Thread th = new Thread(() -> {
                String last = "";

                File file = new File("logs/latest.log");
                boolean starting = true;
                int count = 0;
                String chatid = String.valueOf(config.get("chatid"));
                List<LogMessage> startingSend = new ArrayList<>();
                List<LogMessage> send = new ArrayList<>();
                boolean showDate = config.getBoolean("show-date");
                server.getScheduler().scheduleSyncRepeatingTask(this, ()-> {
                    try {
                        if(send.size() > 0) {
                            StringBuilder message = new StringBuilder();
                            int i = 1;
                            for (LogMessage logMessage : send) {
                                message.append("\n").append(logMessage.toFormattedString(showDate));
                                if (send.size() >= 30) {
                                    if (i % 30 == 0) {
                                        bot.sendMessage(chatid, message.toString());
                                        message = new StringBuilder();
                                    }
                                    i++;
                                }
                            }
                            bot.sendMessage(chatid, message.toString());
                            send.clear();
                        }
                    } catch (InvalidIDException e) {
                        e.printStackTrace();
                    }

                },0L, 5L);

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while (true) {
                        String s = reader.readLine();
                        if (s != null) {
                            if (!s.equals(last)) {

                                LogMessage log = LogMessage.parse(s);

                                //Collapse starting log
                                if (log.getLevel().equalsIgnoreCase("info") && log.getMsg().contains("Done")) {
                                    starting = false;
                                    String message = "";
                                    for(LogMessage logMessage : startingSend) {
                                        message += "\n"+logMessage.toFormattedString(showDate);
                                    }
                                    bot.sendMessage(chatid, message);
                                    count = 0;
                                    startingSend.clear();
                                }

                                //Message handler
                                if (starting) {
                                    startingSend.add(log);
                                    count++;
                                    if (count == 30) {
                                        String message = "";
                                        for(LogMessage logMessage : startingSend) {
                                            message += "\n"+logMessage.toFormattedString(showDate);
                                        }
                                        bot.sendMessage(chatid, message);
                                        startingSend.clear();
                                        count = 0;
                                    }
                                } else {
                                    send.add(log);
                                }
                                last = s;
                            }
                        }
                    }
                } catch (IOException | InvalidIDException e) {
                    e.printStackTrace();
                }
            });
            th.setDaemon(true);
            th.start();
            th.setName("TelegramConsole");
        } catch (InvalidTokenException e) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                console.sendMessage(RED + "[TelegramConsole] Your token is invalid. Please change the token value in the config.yml!");
                Bukkit.getPluginManager().disablePlugin(this);
            }, 100);
        } catch (WebhookException e) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                console.sendMessage(RED + "[TelegramConsole] " + bot.getName() + " already has a webhook set, please remove it by visiting the following link:\n" +
                        DARK_RED + "https://api.telegram.org/bot" + token + "/setWebHook?url=");
                Bukkit.getPluginManager().disablePlugin(this);
            }, 100);
        }
    }
}

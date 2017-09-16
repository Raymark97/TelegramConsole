package xyz.raymark.telegramconsole;

import net.masfik.plugins.TelegramCore.Bot;
import net.masfik.plugins.TelegramCore.InvalidTokenException;
import net.masfik.plugins.TelegramCore.WebhookException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                boolean error = false;
                int count = 0;
                String chatid = String.valueOf(config.get("chatid"));
                List<String> send = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while (true) {
                        String s = reader.readLine();
                        if (s != null) {
                            if (!s.equals(last)) {

                                LogMessage log = LogMessage.parse(s);

                                String date = log.getDate();
                                String thread = log.getThread();
                                String level = log.getLevel();
                                String msg = log.getMsg();

                                //Hide date
                                if (!config.getBoolean("show-date") || error) date = "";

                                //Collapse starting log
                                if (level.equalsIgnoreCase("info") && msg.contains("Done")) {
                                    starting = false;
                                    bot.sendMessage(chatid, String.join("\n", send));
                                    count = 0;
                                    send.clear();
                                }

                                //Collapse errors
                                Matcher errorMatcher = Pattern.compile("\\.java:\\d").matcher(s);
                                if (error && !errorMatcher.find() && !s.toLowerCase().contains("exception") && !s.contains("at ") && !s.contains(" ~[")) {
                                    error = false;
                                    bot.sendMessage(chatid, String.join("\n", send));
                                    if (config.getBoolean("separed-exception"))
                                        bot.sendMessage(config.getString("separed-exception-chatid"), String.join("\n", send));
                                    send.clear();
                                }

                                if (level.equalsIgnoreCase("error") || msg.contains("Exception") || msg.contains("exception"))
                                    error = true;

                                String output = date + "<b>[" + thread + "/" + level + "]</b> <code>" + msg + "</code>";

                                if (thread.equals("") && level.equals("")) output = date + msg;

                                //Message handler
                                if (starting) {
                                    send.add(output);
                                    count++;
                                    if (count == 30) {
                                        bot.sendMessage(chatid, String.join("\n", send));
                                        send.clear();
                                        count = 0;
                                    }
                                } else if (error) {
                                    send.add("<code>" + s + "</code>");
                                } else {
                                    //Do not send this useless message
                                    if (!msg.contains("CONSOLE issued server command:"))
                                        bot.sendMessage(chatid, output);
                                }
                                last = s;
                            }
                        }
                    }
                } catch (IOException e) {
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

package xyz.raymark.telegramconsole;

import io.github.ageofwar.telejam.methods.SendMessage;
import xyz.raymark.tgsuite.tgcore.BotNotFoundException;
import xyz.raymark.tgsuite.tgcore.TelegramCore;
import xyz.raymark.tgsuite.tgcore.TelegramCoreBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleReader implements Runnable {
    private String chatid;
    private boolean showDate;
    private boolean separedExceptions;
    private String separedExceptionChatId;

    public ConsoleReader(String chatid, boolean showDate, boolean separedExceptions, String separedExceptionsChat) {
        this.chatid = chatid;
        this.showDate = showDate;
        this.separedExceptions = separedExceptions;
        this.separedExceptionChatId = separedExceptionsChat;
    }

    @Override
    public void run() {
        String last = "";

        File file = new File("logs/latest.log");
        boolean starting = true;
        boolean error = false;
        int count = 0;
        List<String> send = new ArrayList<>();
        try {
            TelegramCoreBot bot = TelegramCore.getBot();
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
                        if (!showDate || error) {
                            date = "";
                        }

                        //Collapse starting log
                        if (level.equalsIgnoreCase("info") && msg.contains("Done")) {
                            starting = false;
                            SendMessage sendMessage = new SendMessage().chat(chatid).text(String.join("\n", send));
                            bot.execute(sendMessage);
                            count = 0;
                            send.clear();
                        }

                        //Collapse errors
                        Matcher errorMatcher = Pattern.compile("\\.java:\\d").matcher(s);
                        if (error && !errorMatcher.find() && !s.toLowerCase().contains("exception") && !s.contains("at ") && !s.contains(" ~[")) {
                            error = false;
                            SendMessage sendMessage = new SendMessage().chat(chatid).text(String.join("\n", send));
                            bot.execute(sendMessage);
                            if (separedExceptions) {
                                SendMessage sendmsg = new SendMessage().chat(separedExceptionChatId).text(String.join("\n", send));
                                bot.execute(sendmsg);
                            }
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
                                SendMessage sendMessage = new SendMessage().chat(chatid).text(String.join("\n", send));
                                bot.execute(sendMessage);
                                send.clear();
                                count = 0;
                            }
                        } else if (error) {
                            send.add("<code>" + s + "</code>");
                        } else if (msg.contains("CONSOLE issued server command:")) {
                            //Do not send this useless message
                        } else {
                            SendMessage sendMessage = new SendMessage().chat(chatid).text(output);
                            bot.execute(sendMessage);
                        }
                        last = s;
                    }
                }
            }
        } catch (IOException | BotNotFoundException e) {
            e.printStackTrace();
        }
    }
}

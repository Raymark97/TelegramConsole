package xyz.raymark.telegramconsole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogMessage {
    private String date;
    private String thread;
    private String level;
    private String msg;

    private LogMessage(String date, String thread, String level, String msg) {
        this.date = date;
        this.thread = thread;
        this.level = level;
        this.msg = msg;
    }

    static LogMessage parse(String s) {
        String date = "";
        String thread = "";
        String level = "";
        String msg = "";

        //Hide color codes
        s = s.replaceAll("\\[\\d;\\d{2};\\dm", "").replaceAll("\\[\\d;\\d{2};\\d{2}m", "").replaceAll("§\\d", "");

        Pattern datePattern = Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}] ");
        Matcher matcher = datePattern.matcher(s);
        if (matcher.find()) date = matcher.group(0);

        Pattern threadPattern = Pattern.compile("(?<= \\[)(.*?)(?=/)");
        matcher = threadPattern.matcher(s);
        if (matcher.find()) thread = matcher.group(0);

        Pattern levelPattern = Pattern.compile("(?<=/)(.*?)(?=])");
        matcher = levelPattern.matcher(s);
        if (matcher.find()) level = matcher.group(0);


        if(date.length() + thread.length() + level.length() != 0) msg = s.substring(date.length() + thread.length() + level.length() + 5);

        date = date.replace("[", "").replace("]", "");
        thread = thread.replace("/", "").replace("[", "");
        level = level.replace("/", "").replace("]", "");

        return new LogMessage(date, thread, level, msg);
    }

    String getDate() {
        return date;
    }

    String getLevel() {
        return level;
    }

    String getMsg() {
        return msg;
    }

    String getThread() {
        return thread;
    }

    void setMsg(String msg) {
        this.msg = msg;
    }

    String toFormattedString(boolean showDate) {
        if(!showDate) date = "";
        if(thread.equals("") && level.equals("")) return "<code>" + msg + "</code>";
        return date + "<b>[" + thread + "/" + level + "]</b> <code>" + msg + "</code>";
    }
}

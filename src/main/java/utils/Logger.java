package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }

    private static LogLevel currentLogLevel = LogLevel.INFO;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static String currentLogFilePath;

    private static void log(String message, LogLevel logLevel) {
        if (StringUtils.isBlank(message) || logLevel.ordinal() < currentLogLevel.ordinal()) return;

        String time = dateFormat.format(new Date());
        String thread = Thread.currentThread().getName();
        String log = String.format("%s %s [%s] %s", time, logLevel.name(), thread, message);
        System.out.println(log);

        if (currentLogFilePath == null || !currentLogFilePath.contains(DATE_FORMAT)) {
            currentLogFilePath = "server_" + new SimpleDateFormat(DATE_FORMAT).format(new Date()) + ".log";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentLogFilePath, true))) {
            writer.write(log);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLogLevel(LogLevel level) {
        currentLogLevel = level;
    }

    public static void info(String message) {
        log(message, LogLevel.INFO);
    }

    public static void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    public static void error(String message) {
        log(message, LogLevel.ERROR);
    }

    public static void warning(String message) {
        log(message, LogLevel.WARNING);
    }

    public static void main(String[] args) {
        Logger.setLogLevel(Logger.LogLevel.INFO); // 设置日志级别为INFO
        Logger.info("This is an information message.");
        Logger.debug("This is a debug message."); // 不会被记录，因为级别低于INFO
        Logger.error("This is an error message.");
    }
}
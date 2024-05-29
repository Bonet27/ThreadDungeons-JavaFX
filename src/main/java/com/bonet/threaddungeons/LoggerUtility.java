package com.bonet.threaddungeons;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

public class LoggerUtility {
    public static Logger getLogger(Class<?> clazz, String usernameAndId) {
        Logger logger = Logger.getLogger(clazz);
        try {
            PatternLayout layout = new PatternLayout("%d [%t] %-5p %c - %m%n");
            FileAppender fileAppender = new FileAppender(layout, ".saves/" + usernameAndId + "/" + clazz.getSimpleName() + ".log", true);
            logger.addAppender(fileAppender);
        } catch (IOException e) {
            logger.error("Failed to add appender to logger", e);
        }
        return logger;
    }

    public static Logger getLogger() {
        Logger logger = Logger.getLogger(LoggerUtility.class);
        try {
            PatternLayout layout = new PatternLayout("%d [%t] %-5p %c - %m%n");
            FileAppender fileAppender = new FileAppender(layout, ".saves/server.log", true);
            logger.addAppender(fileAppender);
        } catch (IOException e) {
            logger.error("Failed to add appender to logger", e);
        }
        return logger;
    }
}

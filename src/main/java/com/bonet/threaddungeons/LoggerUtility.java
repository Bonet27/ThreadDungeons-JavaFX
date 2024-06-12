package com.bonet.threaddungeons;

import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

public class LoggerUtility {
    public static Logger getLogger(Class<?> clazz, String usernameAndId) {
        Logger logger = Logger.getLogger(clazz);
        if (!logger.getAllAppenders().hasMoreElements()) {
            try {
                // Configuración para el log en formato HTML
                HTMLLayout htmlLayout = new HTMLLayout();
                FileAppender htmlFileAppender = new FileAppender(htmlLayout, ".logs/" + usernameAndId + "/" + clazz.getSimpleName() + ".html", true);
                logger.addAppender(htmlFileAppender);

                // Configuración para el log en formato de texto
                PatternLayout textLayout = new PatternLayout("%d [%t] %-5p %c - %m%n");
                FileAppender textFileAppender = new FileAppender(textLayout, ".logs/" + usernameAndId + "/" + clazz.getSimpleName() + ".log", true);
                logger.addAppender(textFileAppender);
            } catch (IOException e) {
                logger.error("Failed to add appender to logger", e);
            }
        }
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        if (!logger.getAllAppenders().hasMoreElements()) {
            try {
                HTMLLayout layout = new HTMLLayout();
                FileAppender fileAppender = new FileAppender(layout, ".logs/server.html", true);
                logger.addAppender(fileAppender);
            } catch (IOException e) {
                logger.error("Failed to add appender to logger", e);
            }
        }
        return logger;
    }
}

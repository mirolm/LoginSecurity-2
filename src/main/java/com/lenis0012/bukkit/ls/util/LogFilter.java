package com.lenis0012.bukkit.ls.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.Arrays;
import java.util.List;

public class LogFilter extends AbstractFilter {
    private static final List<String> FILTERED_COMMANDS = Arrays.asList("/register ", "/reg ", "/login ", "/log ", "/l ", "/changepassword ", "/changepass ", "/cp ");

    private boolean filter(String message) {
        for (String word : FILTERED_COMMANDS) {
            if (message.contains(word)) {
                return true;
            }
        }

        return false;
    }

    private Result handle(String message) {
        message = message.toLowerCase();

        return filter(message) ? Result.DENY : Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        return handle(event.getMessage().getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return handle(msg.getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return handle(msg.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return handle(msg);
    }
}

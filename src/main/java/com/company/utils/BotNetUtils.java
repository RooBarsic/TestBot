package main.java.com.company.utils;

import org.jetbrains.annotations.NotNull;

public class BotNetUtils {

    public static boolean isNumber(@NotNull final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.company.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Farrukh Karimov
 */
public class BotNetMail {
    private String userChatId;
    private String message;

    public BotNetMail(@NotNull final String userChatId,
                      @NotNull final String message) {
        this.message = message;
        this.userChatId = userChatId;
    }

    public BotNetMail() {
        this.message = "null";
        this.userChatId = "null";
    }


    public String getUserChatId() {
        return userChatId;
    }

    public void setUserChatId(String userChatId) {
        this.userChatId = userChatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

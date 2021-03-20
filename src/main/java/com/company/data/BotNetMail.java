package com.company.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Farrukh Karimov
 */
public class BotNetMail {
    private String userChatId;
    private String message;
    private int roomId;

    public BotNetMail(@NotNull final String userChatId,
                      @NotNull final String message) {
        this.message = message;
        this.userChatId = userChatId;
        this.roomId = -1;
    }

    public BotNetMail() {
        this.message = "null";
        this.userChatId = "null";
        this.roomId = -1;
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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}

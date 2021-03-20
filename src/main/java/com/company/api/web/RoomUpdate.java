package com.company.api.web;

import org.jetbrains.annotations.NotNull;

/**
 * Class for wending room updates throw web to another bots
 *
 * @author Farrukh Karimov
 */
public class RoomUpdate {
    private int roomId;
    private String message;
    private String secretKey;

    public RoomUpdate(@NotNull final int roomId,
                      @NotNull final String message,
                      @NotNull final String secretKey) {
        this.roomId = roomId;
        this.message = message;
        this.secretKey = secretKey;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}

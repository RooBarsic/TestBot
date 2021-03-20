package com.company.data.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for DB communication
 *
 * @author Farrukh Karimov
 */
public interface BotNetDataBase {

    /**
     * checks is user authorized in that specific bot
     * @param userChatId
     * @return
     */
    boolean checkUserAuthorizationByChatId(@NotNull final String userChatId);

    int getUserRoomIdByChatId(@NotNull final String userChatId);

    List<String> getRoomMembersByRoomId(@NotNull final int roomId);
}

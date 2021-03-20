package com.company.data.database;

import com.company.data.AvailableCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for DB communication
 *
 * @author Farrukh Karimov
 */
public interface BotNetDataBase {


    void updateDb(@NotNull final String userChatId);

    /**
     * checks is user authorized in that specific bot
     * @param userChatId
     * @return
     */
    boolean checkUserAuthorizationByChatId(@NotNull final String userChatId);

    int getUserRoomIdByChatId(@NotNull final String userChatId);

    List<String> getRoomMembersByRoomId(@NotNull final int roomId);

    AvailableCommand getUserPrevCommandByChatId(@NotNull final String userChatId);

    void setUserParsedCommandByChatId(@NotNull final String userChatId, @NotNull final AvailableCommand availableCommand);

    boolean authorizeUserByChatId(@NotNull final String userChatId, @NotNull final String authorizationKey);

    boolean isRoomExist(int roomId);
}

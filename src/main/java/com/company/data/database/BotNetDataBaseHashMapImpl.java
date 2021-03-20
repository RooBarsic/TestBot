package com.company.data.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * HashMap based, implementation of BotNetDB functions
 *
 * @author Farrukh Karimov
 */
public class BotNetDataBaseHashMapImpl implements BotNetDataBase {
    private final Map<String, Boolean> userAuthorizationByChatId;
    private final Map<String, Integer> userRoomIdByChatId;
    private final List<List<String>> roomMembersChatIds;

    public BotNetDataBaseHashMapImpl() {
        userAuthorizationByChatId = new HashMap<>();
        userRoomIdByChatId = new HashMap<>();
        roomMembersChatIds = new ArrayList<>();
    }

    @Override
    public boolean checkUserAuthorizationByChatId(@NotNull String userChatId) {
        if (userAuthorizationByChatId.containsKey(userChatId)) {
            return userAuthorizationByChatId.get(userChatId);
        }
        userAuthorizationByChatId.put(userChatId, false);
        return false;
    }

    @Override
    public int getUserRoomIdByChatId(@NotNull String userChatId) {
        if (userRoomIdByChatId.containsKey(userChatId)) {
            return userRoomIdByChatId.get(userChatId);
        }
        return -1;
    }

    @Override
    public List<String> getRoomMembersByRoomId(@NotNull int roomId) {
        if ((0 <= roomId) && (roomId < roomMembersChatIds.size())) {
            return new LinkedList<>(roomMembersChatIds.get(roomId));
        }
        return null;
    }


}

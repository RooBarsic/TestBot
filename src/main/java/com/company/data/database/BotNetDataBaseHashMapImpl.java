package com.company.data.database;

import com.company.data.AvailableCommand;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * HashMap based, implementation of BotNetDB functions
 *
 * @author Farrukh Karimov
 */
public class BotNetDataBaseHashMapImpl implements BotNetDataBase {
    private final Map<String, Boolean> userAuthorizationStatusByChatId;
    private final Map<String, Integer> userRoomIdByChatId;
    private final List<List<String>> roomMembersChatIds;
    private final Map<String, AvailableCommand> userPreviousCommandByChatId;
    private final Map<String, String> userAuthorizationKeyByChatId;

    public BotNetDataBaseHashMapImpl() {
        userAuthorizationStatusByChatId = new HashMap<>();
        userRoomIdByChatId = new HashMap<>();
        roomMembersChatIds = new ArrayList<>();
        userPreviousCommandByChatId = new HashMap<>();
        userAuthorizationKeyByChatId = new HashMap<>();
    }

    @Override
    public void updateDb(@NotNull String userChatId) {
        userAuthorizationStatusByChatId.putIfAbsent(userChatId, false);
        userRoomIdByChatId.putIfAbsent(userChatId, -1);
        userPreviousCommandByChatId.putIfAbsent(userChatId, AvailableCommand.NONE);
        userAuthorizationKeyByChatId.putIfAbsent(userChatId, "Hello");

    }

    @Override
    public boolean checkUserAuthorizationByChatId(@NotNull String userChatId) {
        if (userAuthorizationStatusByChatId.containsKey(userChatId)) {
            return userAuthorizationStatusByChatId.get(userChatId);
        }
        userAuthorizationStatusByChatId.put(userChatId, false);
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

    @Override
    public AvailableCommand getUserPrevCommandByChatId(@NotNull String userChatId) {
        return userPreviousCommandByChatId.getOrDefault(userChatId, AvailableCommand.NONE);
    }

    @Override
    public void setUserParsedCommandByChatId(@NotNull String userChatId, @NotNull AvailableCommand availableCommand) {
        userPreviousCommandByChatId.put(userChatId, availableCommand);
    }

    @Override
    public boolean authorizeUserByChatId(@NotNull String userChatId, @NotNull String authorizationKey) {
        if (!userAuthorizationKeyByChatId.containsKey(userChatId)) {
            updateDb(userChatId);
        }
        final String appKey = userAuthorizationKeyByChatId.get(userChatId);
        if (appKey.equals(authorizationKey)) {
            return true;
        }
        return false;
    }

    public void authorizeUserByChatId(@NotNull final String chatId) {
        userAuthorizationStatusByChatId.put(chatId, true);
    }

    public void addUserToRoom(@NotNull final String chatId, int roomId) {
        int oldRoomId = getUserRoomIdByChatId(chatId);
        if (oldRoomId != -1) {
            roomMembersChatIds.get(oldRoomId).remove(chatId);
        }
        userRoomIdByChatId.put(chatId, roomId);
        roomMembersChatIds.add(new ArrayList<>());
        roomMembersChatIds.get(roomId).add(chatId);
    }


}

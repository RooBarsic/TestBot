package com.company.api.bots;

import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TransferRoomUpdateWorker {
    @NotNull
    private final ConcurrentLinkedDeque<BotNetMail> groupMessagesQueue;
    private final BotNetDataBase botNetDataBase;
    private final BotRequestSender botRequestSender;

    public TransferRoomUpdateWorker(@NotNull final BotNetDataBase botNetDataBase,
                                    @NotNull final BotRequestSender botRequestSender,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> groupMessagesQueue) {
        this.groupMessagesQueue = groupMessagesQueue;
        this.botNetDataBase = botNetDataBase;
        this.botRequestSender = botRequestSender;
    }

    public void startWorker() {
        new Thread(() -> {
            while (true) {
                // wait while there are no boxes for processing
                while (groupMessagesQueue.isEmpty()) {

                }

                // get and remove first box
                final BotNetMail botNetMail = groupMessagesQueue.removeFirst();

                System.out.println("TransferWorker - got new botMail ( user_id : " + botNetMail.getUserChatId() + "; room = " + botNetMail.getRoomId() + ";  message = " + botNetMail.getMessage());

                sendMessageToRoom(botNetMail);
            }
        }).start();
    }

    private void sendMessageToRoom(@NotNull final BotNetMail botNetMail) {
        final int groupId = botNetMail.getRoomId();

        final BotNetBox botNetBox = new BotNetBox();
        botNetBox.setMessage(botNetMail.getMessage());
        botNetBox.addButton(new BotNetButton("Leave", "/leave"));
        botNetBox.addButton(new BotNetButton("Profile", "/profile"));
        botNetBox.addButton(new BotNetButton("Help", "/help"));

        final List<String> roomMembersChatIds = botNetDataBase.getRoomMembersByRoomId(groupId);
        for (final String receiverChatId : roomMembersChatIds) {
            if (!receiverChatId.equals(botNetMail.getUserChatId())) {
                botNetBox.setReceiverChatId(receiverChatId);
                botRequestSender.sendBotNetBox(botNetBox);
            }
        }
    }
}

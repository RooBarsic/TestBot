package com.company.api.bots;

import com.company.api.bots.commands.*;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RoomBotLogic {
    private final BotRequestSender botRequestSender;
    private final BotNetDataBase botNetDataBase;
    private final ConcurrentLinkedDeque<BotNetMail> receivedMails;
    private final List<BotCommand> botCommandsList;
    private final String webAppUrl;

    public RoomBotLogic(@NotNull final BotRequestSender botRequestSender,
                        @NotNull final BotNetDataBase botNetDataBase,
                        @NotNull final ConcurrentLinkedDeque<BotNetMail> receivedMails,
                        @NotNull final String webAppUrl) {
        this.botRequestSender = botRequestSender;
        this.receivedMails = receivedMails;
        this.botNetDataBase = botNetDataBase;
        this.botCommandsList = new ArrayList<>();
        this.webAppUrl = webAppUrl;

        initCommands();
    }

    private void initCommands() {
        botCommandsList.clear();
        botCommandsList.add(new HelpBotNetCommand(botNetDataBase));
        botCommandsList.add(new RegisterBotNetCommand(botNetDataBase, webAppUrl));
        botCommandsList.add(new StartBotNetCommand(botNetDataBase, webAppUrl));
        botCommandsList.add(new LoginBotNetCommand(botNetDataBase, webAppUrl));
        botCommandsList.add(new JoinRoomBotNetCommand(botNetDataBase, webAppUrl));
        botCommandsList.add(new CreateRoomBotNetCommand(botNetDataBase, webAppUrl));
        botCommandsList.add(new LeaveRoomBotNetCommand(botNetDataBase, webAppUrl));
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                // wait while there are no boxes for processing
                while (receivedMails.isEmpty()) {

                }

                // get and remove first box
                final BotNetMail botNetMail = receivedMails.removeFirst();

                // process box
                processBotNetMail(botNetMail);

//                botNetMail.getSenderUser().setLastCommand(botNetMail.getParsedCommand());
//                botNetDataBase.updateUser(botNetMail.getSenderUser());
            }
        }).start();
    }

    private void deliverBox(@NotNull final BotNetBox botNetBox) {
        System.out.println("LOG: delivering new box......");
        System.out.println("LOG: message = " + botNetBox.getMessage());
        botRequestSender.sendBotNetBox(botNetBox);
    }

    private boolean parseAndRunCommand(@NotNull final BotNetMail botNetMail) {
        for (final BotCommand botCommand : botCommandsList) {
            if (botCommand.parseCommand(botNetMail)) {
                botCommand.executeCommand(botNetMail, botRequestSender);

                // remember type of parsed command
                botNetDataBase.setUserParsedCommandByChatId(botNetMail.getUserChatId(), botCommand.getCommandMarcForRemembering(botNetMail));
                return true;
            }
        }
        return false;
    }

    private void processBotNetMail(@NotNull final BotNetMail botNetMail) {
        try {
            //check commands
            if (parseAndRunCommand(botNetMail)) {
                return;
            }

            if (botNetDataBase.checkUserAuthorizationByChatId(botNetMail.getUserChatId())) {
                //authorized user

                int userRoomId = botNetDataBase.getUserRoomIdByChatId(botNetMail.getUserChatId());
                if (userRoomId == -1) {
                    //User didn't joined any room yet
                    final BotNetBox botNetBox = new BotNetBox();
                    botNetBox.setReceiverChatId(botNetMail.getUserChatId());
                    botNetBox.setMessage("You aren't joined any room. Please join any room.");
                    botNetBox.addButton(new BotNetButton("Join", "/join"));
                    botNetBox.addButton(new BotNetButton("Create", "/create"));
                    botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                    botNetBox.addButton(new BotNetButton("Help", "/help"));

                    deliverBox(botNetBox);
                } else {
                    // user is a member of some room

                    //TODO report to RabbitMQ about new message in room

                    //transfer received message to users in that messenger
                    final BotNetBox botNetBox = new BotNetBox();
                    botNetBox.setMessage(botNetMail.getMessage());
                    botNetBox.addButton(new BotNetButton("Leave", "/leave"));
                    botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                    botNetBox.addButton(new BotNetButton("Help", "/help"));

                    final List<String> roomMembersChatIds = botNetDataBase.getRoomMembersByRoomId(userRoomId);
                    for (final String receiverChatId : roomMembersChatIds) {
                        if (!receiverChatId.equals(botNetMail.getUserChatId())) {
                            botNetBox.setReceiverChatId(receiverChatId);
                            deliverBox(botNetBox);
                        }
                    }
                }
            } else {
                // new user
                final BotNetBox botNetBox = new BotNetBox();
                botNetBox.setReceiverChatId(botNetMail.getUserChatId());
                botNetBox.setMessage("Welcome. Please login.");
                botNetBox.addButton(new BotNetButton("Login", "/login"));
                botNetBox.addButton(new BotNetButton("Register", "/register"));
                botNetBox.addButton(new BotNetButton("Help", "/help"));

                deliverBox(botNetBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

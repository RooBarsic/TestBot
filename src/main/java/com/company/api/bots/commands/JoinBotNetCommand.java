package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.AvailableCommand;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public class JoinBotNetCommand implements BotCommand {
    private final BotNetDataBase botNetDataBase;
    private final String webAppUrl;

    public JoinBotNetCommand(@NotNull final BotNetDataBase botNetDataBase,
                              @NotNull final String webAppUrl) {
        this.botNetDataBase = botNetDataBase;
        this.webAppUrl = webAppUrl;
    }

    @Override
    public AvailableCommand getCommandMarcForRemembering(@NotNull BotNetMail botNetMail) {
        if (parseCommand(botNetMail)) {
            if (botNetDataBase.getUserPrevCommandByChatId(botNetMail.getUserChatId()) == AvailableCommand.JOIN) {
                return AvailableCommand.NONE;
            }
            return AvailableCommand.JOIN;
        }
        return AvailableCommand.NONE;
    }

    @Override
    public boolean parseCommand(@NotNull final BotNetMail botNetMail) {
        final String userChatId = botNetMail.getUserChatId();
        final String receivedMessage = botNetMail.getMessage();
        if (!botNetDataBase.checkUserAuthorizationByChatId(userChatId) &&
                (receivedMessage.startsWith("/join") ||
                        botNetDataBase.getUserPrevCommandByChatId(userChatId) == AvailableCommand.JOIN)
        )
            return true;
        return false;
    }

    @Override
    public ProcessStatus executeCommand(@NotNull BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender) {
        final BotNetBox botNetBox = new BotNetBox();
        botNetBox.setReceiverChatId(botNetMail.getUserChatId());

        // Not authorized user
        botNetBox.addButton(new BotNetButton("Join", "/join"));
        botNetBox.addButton(new BotNetButton("Create", "/create"));
        botNetBox.addButton(new BotNetButton("Profile", "/profile"));
        botNetBox.addButton(new BotNetButton("Help", "/help"));

        if (botNetDataBase.getUserPrevCommandByChatId(botNetMail.getUserChatId()) != AvailableCommand.JOIN) {
            botNetBox.setMessage("Please write room Id. You can find available rooms here " + webAppUrl);
        } else {
            if (!main.java.com.company.utils.BotNetUtils.isNumber(botNetMail.getMessage())) {
                botNetBox.setMessage("Room id is number!! [" + botNetMail.getMessage() + "] is not number!!!\n" +
                        "Try again please");
            } else {
                int roomId = Integer.parseInt(botNetMail.getMessage());
                if (botNetDataBase.isRoomExist(roomId)) {
                    botNetBox.setMessage("Congratulations. You are int the room_id : " + roomId + " now. Enjoy!");
                    botNetBox.cleanButtons();

                    botNetBox.addButton(new BotNetButton("Leave", "/leave"));
                    botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                    botNetBox.addButton(new BotNetButton("Help", "/help"));

                } else {
                    botNetBox.setMessage("Sorry, room [" + roomId + "] you are looking for is not exist!!\n" +
                            "Try again please.");
                }
            }
        }

        //deliver box
        botRequestSender.sendBotNetBox(botNetBox);

        return ProcessStatus.SUCCESS;
    }
}

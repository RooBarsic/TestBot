package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.AvailableCommand;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public class LeaveRoomBotNetCommand implements BotCommand {
    private final BotNetDataBase botNetDataBase;
    private final String webAppUrl;

    public LeaveRoomBotNetCommand(@NotNull final BotNetDataBase botNetDataBase,
                                 @NotNull final String webAppUrl) {
        this.botNetDataBase = botNetDataBase;
        this.webAppUrl = webAppUrl;
    }

    @Override
    public AvailableCommand getCommandMarcForRemembering(@NotNull BotNetMail botNetMail) {
        return AvailableCommand.NONE;
    }

    @Override
    public boolean parseCommand(@NotNull final BotNetMail botNetMail) {
        final String userChatId = botNetMail.getUserChatId();
        final String receivedMessage = botNetMail.getMessage();
        if (receivedMessage.startsWith("/leave") &&
                botNetDataBase.checkUserAuthorizationByChatId(userChatId) &&
                botNetDataBase.getUserRoomIdByChatId(userChatId) != -1)
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

        int userRoomId = botNetDataBase.getUserRoomIdByChatId(botNetMail.getUserChatId());
        if (userRoomId != -1) {
            botNetDataBase.removeUserFromRoom(botNetMail.getUserChatId());
            botNetBox.setMessage("You are lived room [" + userRoomId + "]");
        } else {
            botNetBox.setMessage("You aren't joined any room yet!!");
        }

        //deliver box
        botRequestSender.sendBotNetBox(botNetBox);

        return ProcessStatus.SUCCESS;
    }
}

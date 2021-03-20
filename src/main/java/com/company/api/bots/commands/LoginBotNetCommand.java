package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.AvailableCommand;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public class LoginBotNetCommand implements BotCommand {
    private final BotNetDataBase botNetDataBase;
    private final String webAppUrl;

    public LoginBotNetCommand(@NotNull final BotNetDataBase botNetDataBase,
                              @NotNull final String webAppUrl) {
        this.botNetDataBase = botNetDataBase;
        this.webAppUrl = webAppUrl;
    }

    @Override
    public AvailableCommand getCommandMarcForRemembering(@NotNull BotNetMail botNetMail) {
        if (botNetDataBase.getUserPrevCommandByChatId(botNetMail.getUserChatId()) == AvailableCommand.LOGIN) {
            return AvailableCommand.NONE;
        }
        return AvailableCommand.LOGIN;
    }

    @Override
    public boolean parseCommand(@NotNull final BotNetMail botNetMail) {
        final String userChatId = botNetMail.getUserChatId();
        final String receivedMessage = botNetMail.getMessage();
        if (!botNetDataBase.checkUserAuthorizationByChatId(userChatId) &&
                (receivedMessage.startsWith("/login") ||
                        botNetDataBase.getUserPrevCommandByChatId(userChatId) == AvailableCommand.LOGIN)
        )
            return true;
        return false;
    }

    @Override
    public ProcessStatus executeCommand(@NotNull BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender) {
        final BotNetBox botNetBox = new BotNetBox();
        botNetBox.setReceiverChatId(botNetMail.getUserChatId());

        // Not authorized user
        botNetBox.addButton(new BotNetButton("Login", "/login"));
        botNetBox.addButton(new BotNetButton("Register", "/register"));
        botNetBox.addButton(new BotNetButton("Help", "/help"));

        if (botNetDataBase.getUserPrevCommandByChatId(botNetMail.getUserChatId()) != AvailableCommand.LOGIN) {
            botNetBox.setMessage("Please send me your authorization key which was given to you in our web site " + webAppUrl);
        } else {
            if (botNetDataBase.authorizeUserByChatId(botNetMail.getUserChatId(), botNetMail.getMessage())) {
                botNetBox.setMessage("Congratulations. You are authorized now");
                botNetBox.cleanButtons();
                botNetBox.addButton(new BotNetButton("Join", "/join"));
                botNetBox.addButton(new BotNetButton("Create", "/create"));
                botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                botNetBox.addButton(new BotNetButton("Help", "/help"));
            } else {
                botNetBox.setMessage("Wrong authorization key. Please try again.");
            }
        }

        //deliver box
        botRequestSender.sendBotNetBox(botNetBox);

        return ProcessStatus.SUCCESS;
    }
}

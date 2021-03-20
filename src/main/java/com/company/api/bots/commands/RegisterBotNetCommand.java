package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public class RegisterBotNetCommand implements BotCommand {
    private final BotNetDataBase botNetDataBase;
    private final String webAppUrl;

    public RegisterBotNetCommand(@NotNull final BotNetDataBase botNetDataBase,
                                 @NotNull final String webAppUrl) {
        this.botNetDataBase = botNetDataBase;
        this.webAppUrl = webAppUrl;
    }

    @Override
    public boolean parseCommand(@NotNull final BotNetMail botNetMail) {
        return botNetMail.getMessage().startsWith("/register") &&
                !botNetDataBase.checkUserAuthorizationByChatId(botNetMail.getUserChatId());
    }

    @Override
    public ProcessStatus executeCommand(@NotNull BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender) {
        final BotNetBox botNetBox = new BotNetBox();
        botNetBox.setReceiverChatId(botNetMail.getUserChatId());

        botNetBox.setMessage("Please register in our main website " + webAppUrl);

        // Not authorized user
        botNetBox.addButton(new BotNetButton("Login", "/login"));
        botNetBox.addButton(new BotNetButton("Register", "/register"));
        botNetBox.addButton(new BotNetButton("Help", "/help"));

        //deliver box
        botRequestSender.sendBotNetBox(botNetBox);

        return ProcessStatus.SUCCESS;
    }
}

package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public class HelpBotNetCommand implements BotCommand {
    private final String COMMANDS_NAMES = "/start - to see welcome message\n" +
            "/help - to get help if you need\n" +
            "/set_login loginName - to set your login as loginName\n" +
            "/chat_with loginName - to chat with user with login = loginName\n" +
            "/help my - to get information about your profile in BotNet\n" +
            "/help user login - to get information about user with login [login]";

    private final BotNetDataBase botNetDataBase;

    public HelpBotNetCommand(@NotNull final BotNetDataBase botNetDataBase) {
        this.botNetDataBase = botNetDataBase;
    }

    @Override
    public boolean parseCommand(@NotNull final BotNetMail botNetMail) {
        return botNetMail.getMessage().startsWith("/help");
    }

    @Override
    public ProcessStatus executeCommand(@NotNull BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender) {
        final BotNetBox botNetBox = new BotNetBox();
        botNetBox.setReceiverChatId(botNetMail.getUserChatId());

        botNetBox.setMessage(COMMANDS_NAMES);

        if (botNetDataBase.checkUserAuthorizationByChatId(botNetMail.getUserChatId())) {
            // Authorized user
            if (botNetDataBase.getUserRoomIdByChatId(botNetMail.getUserChatId()) == -1) {
                //User didn't joined any room yet
                botNetBox.addButton(new BotNetButton("Join", "/join"));
                botNetBox.addButton(new BotNetButton("Create", "/create"));
                botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                botNetBox.addButton(new BotNetButton("Help", "/help"));
            } else {
                // user is a member of some room
                botNetBox.addButton(new BotNetButton("Leave", "/leave"));
                botNetBox.addButton(new BotNetButton("Profile", "/profile"));
                botNetBox.addButton(new BotNetButton("Help", "/help"));
            }
        } else {
            // Not authorized user
            botNetBox.addButton(new BotNetButton("Login", "/login"));
            botNetBox.addButton(new BotNetButton("Register", "/register"));
            botNetBox.addButton(new BotNetButton("Help", "/help"));
        }

        //deliver box
        botRequestSender.sendBotNetBox(botNetBox);

        return ProcessStatus.SUCCESS;
    }
}

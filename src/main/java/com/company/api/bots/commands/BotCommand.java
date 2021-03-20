package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetMail;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public interface BotCommand {
    boolean parseCommand(@NotNull final BotNetMail botNetMail);
    ProcessStatus executeCommand(@NotNull final BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender);

}

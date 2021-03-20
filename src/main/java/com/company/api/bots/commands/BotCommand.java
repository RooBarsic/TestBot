package com.company.api.bots.commands;

import com.company.api.bots.BotRequestSender;
import com.company.data.AvailableCommand;
import com.company.data.BotNetMail;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

/**
 * General methods which each command should have
 *
 * @author Farrukh Karimov
 */
public interface BotCommand {
    AvailableCommand getCommandMarcForRemembering(@NotNull final BotNetMail botNetMail);
    boolean parseCommand(@NotNull final BotNetMail botNetMail);
    ProcessStatus executeCommand(@NotNull final BotNetMail botNetMail, @NotNull final BotRequestSender botRequestSender);

}

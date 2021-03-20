package com.company.api.bots;

import com.company.data.BotNetBox;
import com.company.data.BotNetFile;
import com.company.data.BotNetMail;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;

public interface BotRequestSender {
    ProcessStatus sendBotNetBox(@NotNull final BotNetBox botNetBox);

    ProcessStatus sendBotNetFile(@NotNull final String chatId, @NotNull final BotNetFile botNetFile);

}
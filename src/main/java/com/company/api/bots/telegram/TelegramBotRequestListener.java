package com.company.api.bots.telegram;

import com.company.api.bots.BotRequestListener;
import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetMail;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Class for receiving messages from Telegram
 *
 * @author Farrukh Karimov
 */
public class TelegramBotRequestListener extends TelegramLongPollingBot implements BotRequestListener {
    private final int RECONNECT_PAUSE = 10000;
    private final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue;

    @Setter
    @Getter
    private String botName;

    @Setter
    private String botToken;

    public TelegramBotRequestListener(@NotNull final String botName,
                                      @NotNull final String botToken,
                                      @NotNull final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue) {
        this.botName = botName;
        this.botToken = botToken;
        this.receivedMailsQueue = receivedMailsQueue;

    }

    @Override
    public void onUpdateReceived(Update update) {
        final BotNetMail botNetMail = new BotNetMail();

        if (update.hasMessage()) {
            final Message telegramMessage = update.getMessage();
            botNetMail.setUserChatId(Long.toString(telegramMessage.getChatId()));
            botNetMail.setMessage(telegramMessage.getText());
        }
        else if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            botNetMail.setUserChatId(Long.toString(callbackQuery.getMessage().getChatId()));
            botNetMail.setMessage(callbackQuery.getData());
        }
        else {
            return;
        }

        // add attachments if has some
        if (update.hasMessage()) {
            final Message telegramMessage = update.getMessage();
        }

        // add filled box to the processing queue
        receivedMailsQueue.addLast(botNetMail);
    }

    @Override
    public String getBotUsername() {
        //log.debug("Bot name: " + botName);
        System.out.println(" ### Request for Bot name");
        return botName;
    }

    @Override
    public String getBotToken() {
        //log.debug("Bot token: " + botToken);
        System.out.println(" ### Request for token");
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            System.out.println(" ### Bot connecting....");
            telegramBotsApi.registerBot(this);
            //log.info("TelegramAPI started. Bot connected and waiting for messages");
        } catch (TelegramApiRequestException e) {
            //log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    @Override
    public BotRequestSender getBotRequestSender() {
        return new TelegramBotRequestSender(getOptions(), botToken);
    }
}

package com.company;

import com.company.api.bots.BotRequestSender;
import com.company.api.bots.RoomBotLogic;
import com.company.api.bots.telegram.TelegramBotRequestListener;
import com.company.data.BotNetBox;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.Tokens;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
    private static Tokens tokensStorage;

    public static void main(String[] args) {
        System.out.println("Hello HackNU");
        tokensStorage = new Tokens();
        tokensStorage.addTokens();
        tokensStorage.showTokens();

        // create BotNet DataBase
        final BotNetDataBase botNetDataBase = new BotNetDataBase(); //initDataBase();

        // create HyperMessages queue
        final ConcurrentLinkedDeque<BotNetMail> botNetMails = new ConcurrentLinkedDeque<>();

        // create and run Telegram bot
        final BotRequestSender telegramBotRequestSender = runTestingTelegramBot(botNetDataBase, botNetMails);

        // start BotBrain
        runBotBrain(botNetDataBase, botNetMails, telegramBotRequestSender);

        System.out.println(" All systems up");
    }

    private static void runBotBrain(@NotNull final BotNetDataBase botNetDataBase,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                    @NotNull final BotRequestSender botRequestSender) {
        System.out.println("##### Starting BotBrain ....... ");
        final RoomBotLogic botMainLogic = new RoomBotLogic(botRequestSender, botNetReceivedMails);
        botMainLogic.start();
        System.out.println("##### BotBrain - started ....... ");
    }



    private static BotRequestSender runTestingTelegramBot(@NotNull final BotNetDataBase botNetDataBase,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails) {
        System.out.println("##### Starting Telegram bot ....... ");
        ApiContextInitializer.init();
        final String TESTING_TELEGRAM_BOT_NAME = tokensStorage.getTokens("TESTING_TELEGRAM_BOT_NAME");
        final String TESTING_TELEGRAM_BOT_TOKEN = tokensStorage.getTokens("TESTING_TELEGRAM_BOT_TOKEN");

        final TelegramBotRequestListener testingBot = new TelegramBotRequestListener(TESTING_TELEGRAM_BOT_NAME, TESTING_TELEGRAM_BOT_TOKEN, botNetReceivedMails);

        testingBot.botConnect();

        System.out.println("##### Telegram bot - started ....... ");
        return testingBot.getBotRequestSender();
    }

}

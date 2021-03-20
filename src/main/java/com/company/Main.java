package com.company;

import com.company.api.bots.BotRequestSender;
import com.company.api.bots.RoomBotLogic;
import com.company.api.bots.mail_ru_agent.MailRuAgentBotRequestListener;
import com.company.api.bots.telegram.TelegramBotRequestListener;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.data.database.BotNetDataBaseHashMapImpl;
import com.company.utils.Tokens;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
    private static Tokens tokensStorage;
    private static String webAppUrl = "https://www.google.com/";

    public static void main(String[] args) {
        System.out.println("Hello HackNU");
        tokensStorage = new Tokens();
        tokensStorage.addTokens();
        tokensStorage.showTokens();

        // create BotNet DataBase
        final BotNetDataBase botNetDataBase = new BotNetDataBaseHashMapImpl(); //initDataBase();
        addTestUser((BotNetDataBaseHashMapImpl) botNetDataBase);

        // create HyperMessages queue
        final ConcurrentLinkedDeque<BotNetMail> botNetMails = new ConcurrentLinkedDeque<>();

        // create and run Telegram bot with BotRoomsBrain
        final BotRequestSender telegramBotRequestSender = runTestingTelegramBot(botNetDataBase, botNetMails);

        // create and run Telegram bot with BotRoomsBrain
        //final BotRequestSender mailRuAgentBotRequestSender = runTestingMailRuBot(botNetDataBase, botNetMails);

        System.out.println(" All systems up");
    }

    private static void addTestUser(@NotNull final BotNetDataBaseHashMapImpl botNetDataBase) {
        //botNetDataBase.isRoomExist();

//        botNetDataBase.authorizeUserByChatId("613929425");
//        botNetDataBase.addUserToRoom("613929425", 0);
    }

    private static void runBotBrain(@NotNull final BotNetDataBase botNetDataBase,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                    @NotNull final BotRequestSender botRequestSender) {

        System.out.println("##### Starting BotBrain ....... ");
        final RoomBotLogic botMainLogic = new RoomBotLogic(botRequestSender, botNetDataBase, botNetReceivedMails, webAppUrl);
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

        // start BotRoomsBrain
        runBotBrain(botNetDataBase, botNetReceivedMails, testingBot.getBotRequestSender());

        return testingBot.getBotRequestSender();
    }

    private static BotRequestSender runTestingMailRuBot(@NotNull final BotNetDataBase botNetDataBase,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails) {
        System.out.println("##### Starting MailRuAgent bot ....... ");
        final String TESTING_MAIL_RU_AGENT_BOT_TOKEN = tokensStorage.getTokens("TESTING_MAIL_RU_AGENT_BOT_TOKEN");

        final MailRuAgentBotRequestListener mailRuAgentBot = new MailRuAgentBotRequestListener(TESTING_MAIL_RU_AGENT_BOT_TOKEN, botNetDataBase, botNetReceivedMails);

        mailRuAgentBot.startBot();


        // start BotRoomsBrain
        runBotBrain(botNetDataBase, botNetReceivedMails, mailRuAgentBot.getBotRequestSender());

        System.out.println("##### MailRuAgent bot - started....... ");

        return mailRuAgentBot.getBotRequestSender();
    }
}

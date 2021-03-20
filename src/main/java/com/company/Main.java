package com.company;

import com.company.api.bots.BotRequestSender;
import com.company.api.bots.RoomBotLogic;
import com.company.api.bots.TransferRoomUpdateWorker;
import com.company.api.bots.mail_ru_agent.MailRuAgentBotRequestListener;
import com.company.api.bots.telegram.TelegramBotRequestListener;
import com.company.api.web.RoomUpdate;
import com.company.api.web.RoomUpdateListener;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.data.database.BotNetDataBaseHashMapImpl;
import com.company.utils.Tokens;
import com.google.gson.Gson;
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

        // create HyperMessages queue
        final ConcurrentLinkedDeque<BotNetMail> botNetMails = new ConcurrentLinkedDeque<>();

        // create room updates queue
        final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue = new ConcurrentLinkedDeque<>();

        // create and run Telegram bot with BotRoomsBrain
        final BotRequestSender telegramBotRequestSender = runTestingTelegramBot(botNetDataBase, botNetMails, roomUpdatesQueue);

        // create and run Telegram bot with BotRoomsBrain
        final BotRequestSender mailRuAgentBotRequestSender = runTestingMailRuBot(botNetDataBase, botNetMails, roomUpdatesQueue);

        System.out.println(" All systems up");
    }

    private static void runBotBrain(@NotNull final BotNetDataBase botNetDataBase,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                    @NotNull final BotRequestSender botRequestSender) {

        System.out.println("##### Starting BotBrain ....... ");
        final RoomBotLogic botMainLogic = new RoomBotLogic(botRequestSender, botNetDataBase, botNetReceivedMails, webAppUrl, roomUpdatesQueue);
        botMainLogic.start();
        System.out.println("##### BotBrain - started ....... ");
    }

    private static void runTransferRoomUpdatesWorker(@NotNull final BotNetDataBase botNetDataBase,
                                                     @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                                     @NotNull final BotRequestSender botRequestSender) {
        System.out.println("##### Starting - transfer room updates worker ....... ");
        final TransferRoomUpdateWorker transferRoomUpdateWorker = new TransferRoomUpdateWorker(botNetDataBase, botRequestSender, roomUpdatesQueue);
        transferRoomUpdateWorker.startWorker();
        System.out.println("##### Transfer room updates worker - started ....... ");
    }

    private static void runWebRoomUpdateListener(@NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue) {
        System.out.println("##### Starting Web room updates listener ....... ");
        final String APP_SECRET_KEY = tokensStorage.getTokens("APP_SECRET_KEY");
        final int SERVER_PORT = Integer.parseInt(tokensStorage.getTokens("SERVER_PORT"));
        final RoomUpdateListener roomUpdateListener = new RoomUpdateListener(SERVER_PORT, APP_SECRET_KEY, roomUpdatesQueue);
        roomUpdateListener.startListener();
        System.out.println("##### Web room updates listener - started ....... ");

        Gson gson = new Gson();
        RoomUpdate newRoomUpdate = new RoomUpdate(10, "Hello HackNU", "BotNet");
        System.out.println(gson.toJson(newRoomUpdate, RoomUpdate.class));
    }

    private static BotRequestSender runTestingTelegramBot(@NotNull final BotNetDataBase botNetDataBase,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue) {
        System.out.println("##### Starting Telegram bot ....... ");
        ApiContextInitializer.init();
        final String TESTING_TELEGRAM_BOT_NAME = tokensStorage.getTokens("TESTING_TELEGRAM_BOT_NAME");
        final String TESTING_TELEGRAM_BOT_TOKEN = tokensStorage.getTokens("TESTING_TELEGRAM_BOT_TOKEN");

        final TelegramBotRequestListener testingBot = new TelegramBotRequestListener(TESTING_TELEGRAM_BOT_NAME, TESTING_TELEGRAM_BOT_TOKEN, botNetReceivedMails);

        testingBot.botConnect();

        System.out.println("##### Telegram bot - started ....... ");

        // start TransferRoomUpdate Worker
        runTransferRoomUpdatesWorker(botNetDataBase, roomUpdatesQueue, testingBot.getBotRequestSender());

        // start Web room updates listener
        runWebRoomUpdateListener(roomUpdatesQueue);

        // start BotRoomsBrain
        runBotBrain(botNetDataBase, botNetReceivedMails, roomUpdatesQueue, testingBot.getBotRequestSender());

        return testingBot.getBotRequestSender();
    }

    private static BotRequestSender runTestingMailRuBot(@NotNull final BotNetDataBase botNetDataBase,
                                                        @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                                        @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue) {
        System.out.println("##### Starting MailRuAgent bot ....... ");
        final String TESTING_MAIL_RU_AGENT_BOT_TOKEN = tokensStorage.getTokens("TESTING_MAIL_RU_AGENT_BOT_TOKEN");

        final MailRuAgentBotRequestListener mailRuAgentBot = new MailRuAgentBotRequestListener(TESTING_MAIL_RU_AGENT_BOT_TOKEN, botNetDataBase, botNetReceivedMails);

        mailRuAgentBot.startBot();

        // start TransferRoomUpdate Worker
        runTransferRoomUpdatesWorker(botNetDataBase, roomUpdatesQueue, mailRuAgentBot.getBotRequestSender());

        // start Web room updates listener
        runWebRoomUpdateListener(roomUpdatesQueue);

        // start BotRoomsBrain
        runBotBrain(botNetDataBase, botNetReceivedMails, roomUpdatesQueue, mailRuAgentBot.getBotRequestSender());

        System.out.println("##### MailRuAgent bot - started....... ");

        return mailRuAgentBot.getBotRequestSender();
    }
}

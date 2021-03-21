package com.company;

import com.company.api.bots.BotRequestSender;
import com.company.api.bots.RoomBotLogic;
import com.company.api.bots.TransferRoomUpdateWorker;
import com.company.api.bots.UiPlatform;
import com.company.api.bots.facebook.FacebookBotRequestListener;
import com.company.api.bots.mail_ru_agent.MailRuAgentBotRequestListener;
import com.company.api.bots.telegram.TelegramBotRequestListener;
import com.company.api.web.RoomUpdate;
import com.company.api.web.RoomUpdateListener;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.data.database.BotNetDataBaseHashMapImpl;
import com.company.utils.BotNetUtils;
import com.company.utils.PingBotFriends;
import com.company.utils.Tokens;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
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

        Map<UiPlatform, String> appUrlByPlatformName = new HashMap<>();
        appUrlByPlatformName.put(UiPlatform.TELEGRAM, tokensStorage.getTokens("TESTING_TELEGRAM_BOT_HEROKU_URL"));
        appUrlByPlatformName.put(UiPlatform.MAIL_RU_AGENT, tokensStorage.getTokens("TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL"));
        appUrlByPlatformName.put(UiPlatform.FACEBOOK, tokensStorage.getTokens("TESTING_FACEBOOK_BOT_HEROKU_URL"));


        HttpServer httpServer = createHttpServer();
        if (httpServer == null){
            return;
        }

        // create BotNet DataBase
        final BotNetDataBase botNetDataBase = new BotNetDataBaseHashMapImpl(); //initDataBase();

        // create HyperMessages queue
        final ConcurrentLinkedDeque<BotNetMail> botNetMails = new ConcurrentLinkedDeque<>();

        // create room updates queue
        final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue = new ConcurrentLinkedDeque<>();

        // create and run Telegram bot with BotRoomsBrain
        final BotRequestSender telegramBotRequestSender = runTestingTelegramBot(botNetDataBase, botNetMails, roomUpdatesQueue, httpServer, appUrlByPlatformName);

        // create and run MailRuAgentBot bot with BotRoomsBrain
        //final BotRequestSender mailRuAgentBotRequestSender = runTestingMailRuBot(botNetDataBase, botNetMails, roomUpdatesQueue, httpServer, appUrlByPlatformName);

        // create and run Facebook bot with BotRoomsBrain
        //final BotRequestSender facebookBotRequestSender = runTestingFacebookBot(botNetDataBase, botNetMails, roomUpdatesQueue, httpServer, appUrlByPlatformName);

        // create and start pinger
        runPeriodicalPing();

        // start HttpServer
        httpServer.start();
        System.out.println("HttpServer - started ....");

        System.out.println(" All systems up");
    }

    private static HttpServer createHttpServer() {
        try {
            final int SERVER_PORT = Integer.parseInt(tokensStorage.getTokens("SERVER_PORT"));

            return HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void runBotBrain(@NotNull final BotNetDataBase botNetDataBase,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                    @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                    @NotNull final BotRequestSender botRequestSender,
                                    @NotNull final Map<UiPlatform, String> appUrlByPlatformName,
                                    @NotNull final UiPlatform curBotUiPlatform
                                    ) {

        System.out.println("##### Starting BotBrain ....... ");
        final RoomBotLogic botMainLogic = new RoomBotLogic(botRequestSender,
                botNetDataBase,
                botNetReceivedMails,
                webAppUrl,
                roomUpdatesQueue,
                appUrlByPlatformName,
                curBotUiPlatform,
                tokensStorage.getTokens("APP_SECRET_KEY")
        );
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

    private static void runWebRoomUpdateListener(@NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                                 @NotNull final HttpServer httpServer) {
        System.out.println("##### Starting Web room updates listener ....... ");
        final String APP_SECRET_KEY = tokensStorage.getTokens("APP_SECRET_KEY");
        final RoomUpdateListener roomUpdateListener = new RoomUpdateListener(httpServer, APP_SECRET_KEY, roomUpdatesQueue);
        roomUpdateListener.startListener();
        System.out.println("##### Web room updates listener - started ....... ");

        Gson gson = new Gson();
        RoomUpdate newRoomUpdate = new RoomUpdate(10, "Hello HackNU", "BotNet");
        System.out.println(gson.toJson(newRoomUpdate, RoomUpdate.class));
    }

    private static BotRequestSender runTestingTelegramBot(@NotNull final BotNetDataBase botNetDataBase,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                                          @NotNull final HttpServer httpServer,
                                                          @NotNull final Map<UiPlatform, String> appUrlByPlatformName) {
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
        runWebRoomUpdateListener(roomUpdatesQueue, httpServer);

        // start BotRoomsBrain
        runBotBrain(botNetDataBase,
                botNetReceivedMails,
                roomUpdatesQueue,
                testingBot.getBotRequestSender(),
                appUrlByPlatformName,
                UiPlatform.TELEGRAM);

        return testingBot.getBotRequestSender();
    }

    private static BotRequestSender runTestingMailRuBot(@NotNull final BotNetDataBase botNetDataBase,
                                                        @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                                        @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                                        @NotNull final HttpServer httpServer,
                                                        @NotNull final Map<UiPlatform, String> appUrlByPlatformName) {
        System.out.println("##### Starting MailRuAgent bot ....... ");
        final String TESTING_MAIL_RU_AGENT_BOT_TOKEN = tokensStorage.getTokens("TESTING_MAIL_RU_AGENT_BOT_TOKEN");

        final MailRuAgentBotRequestListener mailRuAgentBot = new MailRuAgentBotRequestListener(TESTING_MAIL_RU_AGENT_BOT_TOKEN, botNetDataBase, botNetReceivedMails);

        mailRuAgentBot.startBot();

        // start TransferRoomUpdate Worker
        runTransferRoomUpdatesWorker(botNetDataBase, roomUpdatesQueue, mailRuAgentBot.getBotRequestSender());

        // start Web room updates listener
        runWebRoomUpdateListener(roomUpdatesQueue, httpServer);

        // start BotRoomsBrain
        runBotBrain(botNetDataBase,
                botNetReceivedMails,
                roomUpdatesQueue,
                mailRuAgentBot.getBotRequestSender(),
                appUrlByPlatformName,
                UiPlatform.MAIL_RU_AGENT);

        System.out.println("##### MailRuAgent bot - started....... ");

        return mailRuAgentBot.getBotRequestSender();
    }

    private static BotRequestSender runTestingFacebookBot(@NotNull final BotNetDataBase botNetDataBase,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> botNetReceivedMails,
                                                          @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue,
                                                          @NotNull final HttpServer httpServer,
                                                          @NotNull final Map<UiPlatform, String> appUrlByPlatformName) {
        System.out.println("##### Starting Facebook bot ....... ");
        final String TESTING_FACEBOOK_BOT_VERIFY_TOKEN = tokensStorage.getTokens("TESTING_FACEBOOK_BOT_VERIFY_TOKEN");
        final String TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = tokensStorage.getTokens("TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN");
        final String TESTING_FACEBOOK_BOT_BASE_URL = tokensStorage.getTokens("TESTING_FACEBOOK_BOT_BASE_URL");
        final FacebookBotRequestListener facebookBot = new FacebookBotRequestListener(
                TESTING_FACEBOOK_BOT_VERIFY_TOKEN,
                TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN,
                TESTING_FACEBOOK_BOT_BASE_URL,
                botNetDataBase,
                botNetReceivedMails
        );

        new Thread(() -> {
            try {
                httpServer.createContext("/facebook-bot", facebookBot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // start TransferRoomUpdate Worker
        runTransferRoomUpdatesWorker(botNetDataBase, roomUpdatesQueue, facebookBot.getBotRequestSender());

        // start Web room updates listener
        runWebRoomUpdateListener(roomUpdatesQueue, httpServer);

        // start BotRoomsBrain
        runBotBrain(botNetDataBase,
                botNetReceivedMails,
                roomUpdatesQueue,
                facebookBot.getBotRequestSender(),
                appUrlByPlatformName,
                UiPlatform.FACEBOOK);

        System.out.println("##### Facebook bot - started....... ");
        return facebookBot.getBotRequestSender();
    }


    public static void runPeriodicalPing() {
        final PingBotFriends pingBotFriends = new PingBotFriends();
        final int pingDelay = Integer.parseInt(tokensStorage.getTokens("PING_DELAY"));
        pingBotFriends.setPingDelay(pingDelay);
        pingBotFriends.addServiceUrl(tokensStorage.getTokens("TESTING_TELEGRAM_BOT_HEROKU_URL"));
        pingBotFriends.addServiceUrl(tokensStorage.getTokens("TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL"));

        pingBotFriends.addServiceUrl(tokensStorage.getTokens("TESTING_FACEBOOK_BOT_HEROKU_URL"));
        pingBotFriends.startPinger();
    }
}

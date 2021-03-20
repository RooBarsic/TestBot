package com.company.api.bots.facebook;

import com.company.api.bots.BotRequestListener;
import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.company.utils.CustomHttpHandlerCommand;
import com.company.utils.facebook.ReceivedFacebookAttachment;
import com.company.utils.facebook.ReceivedFacebookMessage;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FacebookBotRequestListener extends CustomHttpHandlerCommand implements BotRequestListener {
    private int requestsNumber = 0;
    private final String LOG_PREFIX = "FacebookBot ::: ";
    private final String VERIFY_TOKEN;
    private final String  PAGE_ACCESS_TOKEN;
    private final String BASE_URL;
    private final BotNetDataBase botNetDataBase;
    private final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue;


    public FacebookBotRequestListener(@NotNull final String verifyToken,
                                      @NotNull final String pageAccessToken,
                                      @NotNull final String baseUrl,
                                      @NotNull final BotNetDataBase botNetDataBase,
                                      @NotNull final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue) {
        this.VERIFY_TOKEN = verifyToken;
        this.PAGE_ACCESS_TOKEN = pageAccessToken;
        this.botNetDataBase = botNetDataBase;
        this.receivedMailsQueue = receivedMailsQueue;
        this.BASE_URL = baseUrl;
    }

    public void botMain(ReceivedFacebookMessage receivedFacebookMessage) {
        try {
            final BotNetMail botNetMail = new BotNetMail();

            botNetMail.setUserChatId(receivedFacebookMessage.getSenderId());
            botNetMail.setMessage(receivedFacebookMessage.getMessageText());

            // set sender user, received message, and receiving UiPlatform

            // add attachments if has some
            if (receivedFacebookMessage.hasAttachment()) {
                List<ReceivedFacebookAttachment> receivedAttachments = receivedFacebookMessage.getReceivedAttachments();
//                for (ReceivedFacebookAttachment attachment : receivedAttachments) {

                    //TODO transfer libraries
                    // final BotNetFile botNetFile = FacebookUtils.downloadAttachmentToBotNetFile(attachment);
                    // botNetMail.addFile(botNetFile);
 //               }
            }

            // add filled box to the processing queue
            System.out.println(LOG_PREFIX + " added botNetMail to queue");
            receivedMailsQueue.addLast(botNetMail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        requestsNumber++;
        System.out.println(LOG_PREFIX + " got /hello request    requestsNumber = " + requestsNumber);

        if (exchange.getRequestMethod().equals("GET")) {
            handleGetRequest(exchange);
        } else {
            handlePostRequest(exchange);
        }
    }

    /** All content from users (messages/files) would be sent by POST method from Facebook*/
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        System.out.println(LOG_PREFIX + " Got POST request");
        int responseCode = 200;

        Scanner scanner = new Scanner(exchange.getRequestBody());
        String inputDataJson = scanner.nextLine();

        if (scanner.hasNextLine())
            inputDataJson = scanner.nextLine();
        System.out.println("dataJson : " + inputDataJson);

        ReceivedFacebookMessage message = new ReceivedFacebookMessage(inputDataJson);
        System.out.println(LOG_PREFIX + "params ::");
        System.out.println(LOG_PREFIX + message.messageInfo());

        // start main Facebook bot logic
        botMain(message);

        endResponse(exchange, "", responseCode);
        System.out.println(LOG_PREFIX + " Sended response to POST message");
    }


    /** This method would be used only for verifying our server by facebook */
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        System.out.println(LOG_PREFIX + " Got GET request");
        final StringBuilder responseBuilder = new StringBuilder();
        int responseCode = 405;
        Map<String, String> paramByKey = splitQuery(exchange.getRequestURI().getRawQuery());

        System.out.println(LOG_PREFIX + " MAP params ::");
        paramByKey.forEach((a, b) -> {
            System.out.println(LOG_PREFIX + " key = " + a + " val = " + b);
        });

        if (paramByKey.containsKey("hub.mode") && paramByKey.containsKey("hub.verify_token") && paramByKey.containsKey("hub.challenge")) {
            if (paramByKey.get("hub.mode").equals("subscribe") && paramByKey.get("hub.verify_token").equals(VERIFY_TOKEN)) {
                responseCode = 200;
                responseBuilder.append(paramByKey.get("hub.challenge"));
            }
        }

        if (responseCode == 405) {
            responseBuilder.append("Wrong method usage. Use /help");
        }

        endResponse(exchange, responseBuilder.toString(), responseCode);
        System.out.println(LOG_PREFIX + " Sended response to GET message");
    }


    @Override
    public BotRequestSender getBotRequestSender() {
        return new FacebookBotRequestSender(PAGE_ACCESS_TOKEN, BASE_URL);
    }
}

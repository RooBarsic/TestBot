package com.company.api.web;

import com.company.data.BotNetMail;
import com.company.utils.CustomHttpHandlerCommand;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Farrukh Karimov
 */
public class RoomUpdateListener extends CustomHttpHandlerCommand {
    private final int SERVER_PORT;
    private final String APP_SECRET_KEY;
    private final String LOG_PREFIX = "WebListener ::: ";
    private final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue;

    public RoomUpdateListener(final int SERVER_PORT,
                              @NotNull final String APP_SECRET_KEY,
                              @NotNull final ConcurrentLinkedDeque<BotNetMail> roomUpdatesQueue) {
        this.SERVER_PORT = SERVER_PORT;
        this.APP_SECRET_KEY = APP_SECRET_KEY;
        this.roomUpdatesQueue = roomUpdatesQueue;
    }

    public void startListener() {
        new Thread(() -> {
            try {
                HttpServer httpServer = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
                httpServer.createContext("/room/update", this);

                httpServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("GET")) {
            System.out.println(LOG_PREFIX + " got GET request (ping)");

            endResponse(exchange, "Hello", 200);
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
        System.out.println(LOG_PREFIX + " dataJson : " + inputDataJson);

        Gson gson = new Gson();
        RoomUpdate newRoomUpdate = gson.fromJson(inputDataJson, RoomUpdate.class);

        if (APP_SECRET_KEY.equals(newRoomUpdate.getSecretKey())) {
            System.out.println(LOG_PREFIX + " SECRET_KEY --- OKAY");
            final BotNetMail botNetMail = new BotNetMail();
            botNetMail.setMessage(newRoomUpdate.getMessage());
            botNetMail.setRoomId(newRoomUpdate.getRoomId());
            botNetMail.setUserChatId("fake_user_id");
            roomUpdatesQueue.addLast(botNetMail);
        } else {
            System.out.println(LOG_PREFIX + " wrong SECRET_KEY = " + newRoomUpdate.getSecretKey());
        }

        endResponse(exchange, "", responseCode);
        System.out.println(LOG_PREFIX + " Sended response to POST message");
    }

}

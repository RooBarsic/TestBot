package com.company.utils;

import com.lukaspradel.steamapi.data.json.friendslist.Friendslist;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Class for pinging web services deployed on Heroku.com
 *
 * @author Farrukh Karimov
 */
public class PingBotFriends {
    private int PING_DELAY = 1000; // ms
    private final List<String> friendBotAppsUrl;

    public PingBotFriends(@NotNull final List<String> friendBotAppsUrl) {
        this.friendBotAppsUrl = friendBotAppsUrl;
    }

    public PingBotFriends() {
        this.friendBotAppsUrl = new ArrayList<>();
    }

    public void setPingDelay(int x) {
        PING_DELAY = x;
    }

    public void addServiceUrl(@NotNull final String webAppUrl) {
        friendBotAppsUrl.add(webAppUrl);
    }

    public void startPinger() {
        new Thread(() -> {
            try {
                while (true) {
                    for (final String appUrl : friendBotAppsUrl) {
                        System.out.println("PINGER ::: sending ping to " + appUrl);
                        BotNetUtils.httpsGETRequest(appUrl);
                    }
                    Thread.sleep(PING_DELAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}

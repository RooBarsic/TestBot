package com.company.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Farrukh Karimov
 */
public abstract class CustomHttpHandlerCommand implements HttpHandler {

    public void endResponse(HttpExchange exchange, String response, int respCode) throws IOException {
        String encoding = "UTF-8";

        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=" + encoding);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

        exchange.sendResponseHeaders(respCode, response.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(response.getBytes());
        output.flush();
        exchange.close();
    }

    public Map<String, String> splitQuery(String query) {
        Map<String, String> paramByKey = new HashMap<>();

        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }
        String[] keysAndValues = query.split("&");
        for (String s : keysAndValues) {
            String[] kv = s.split("=");
            paramByKey.put(kv[0], kv[1]);
        }
        return paramByKey;
    }
}

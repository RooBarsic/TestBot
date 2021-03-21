package com.company.utils;

import com.sun.net.httpserver.Headers;
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

        System.out.println("incomig request headers num = " + exchange.getRequestHeaders().size());
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.forEach((a, b) -> {
            System.out.println("----- a = " + a);
            for (String x : b) {
                System.out.println("----- b_x = " + x);
            }
        });

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "86400");

        //exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        //exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

//        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
//        httpServletResponse.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
//        httpServletResponse.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
//        httpServletResponse.addHeader("Access-Control-Max-Age", "1728000");

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

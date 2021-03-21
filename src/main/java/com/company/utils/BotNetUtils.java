package com.company.utils;

import com.company.api.web.RoomUpdate;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BotNetUtils {

    public static boolean isNumber(@NotNull final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String coverNull(String string) {
        if (string == null)
            return "";
        return string;
    }

    public static boolean isCorrectLogin(@NotNull final String login) {
        if (login.length() < 1)
            return false;
        for (int i = 0; i < login.length(); i++) {
            final char e = login.charAt(i);
            boolean flag = false;
            if ('a' <= e && e <= 'z') flag = true;
            if ('A' <= e && e <= 'Z') flag = true;
            if ('0' <= e && e <= '9') flag = true;
            if (e == '_') flag = true;

            if (!flag)
                return false;
        }
        return true;
    }

    public static boolean isCorrectUserFirstLastName(@NotNull final String userFirstLastName) {
        return isCorrectLogin(userFirstLastName);
    }

    public static boolean isCorrectUserEmail(@NotNull final String userEmail) {
        if (userEmail.length() < 1)
            return false;
        boolean specialSymbolAppears = false;
        for (char e : userEmail.toCharArray()) {
            if (e == '@') {
                if (specialSymbolAppears) {
                    return false;
                } else {
                    specialSymbolAppears = true;
                }
            }
            else if (!(('a' <= e && e <= 'z') ||
                    ('A' <= e && e <= 'Z') ||
                    ('0' <= e && e <= '9') ||
                    (e == '_') ||
                    (e == '-') ||
                    (e == '.'))) {
                return false;
            }
        }
        return (specialSymbolAppears == true);
    }

    public static boolean isCorrectPhoneNumber(@NotNull final String phoneNumber) {
        if (phoneNumber.length() < 2)
            return false;
        if (phoneNumber.charAt(0) != '+')
            return false;
        for (int i = 1; i < phoneNumber.length(); i++) {
            char e = phoneNumber.charAt(i);
            if (!('0' <= e && e <= '9'))
                return false;
        }
        return true;
    }

    /**
     * Cut and return the rest of string data if str has given prefix
     * @param prefix
     * @param str
     */
    public static String cutPrefix(@NotNull final String prefix, @NotNull final String str) {
        if (str.contains(prefix)) {
            String[] buff = str.split(prefix, 2);
            if (buff.length == 2) {
                return buff[1];
            }
            return null;
        }
        return str;
    }


    /** Checks  */
    public static void createPathIfAbsent(@NotNull final String pathName) {
        final String buff[] = pathName.split("/");
        final StringBuilder pathBuilder = new StringBuilder(".");
        for (int i = 0; i < buff.length;  i++) {
            pathBuilder.append("/").append(buff[i]);
            final String curPath = pathBuilder.toString();
            final File file = new File(curPath);
            if (!file.exists()) {
                file.mkdir();
            }
            System.out.println(" File status ------ " + file.exists() + "  : curPath = " + curPath);
        }
    }

    /** Checks  */
    public static void deleteFile(@NotNull final String pathName) {
        final File file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }
        System.out.println(" File status ------ file was deleted  : Path = " + pathName);
    }


    /** Method to load file from required url and save it in required directory */
    public static ProcessStatus loadFileFromWeb(@NotNull final String url, @NotNull final String path, @NotNull final String fileName) {
        try {
            //
            URL download = new URL(url);
            FileOutputStream fos = new FileOutputStream(path + fileName);
            System.out.println("Start upload");
            ReadableByteChannel rbc = Channels.newChannel(download.openStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            // adFlag = 0;
            System.out.println("Uploaded!");
            return ProcessStatus.SUCCESS;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ProcessStatus.FAILED;
    }

    /** Method to make quick GET request and get the JSON response data*/
    public static String httpsGETRequest(@NotNull final String urlPath) {
        try {
            HttpURLConnection con;
            URL myUrl = new URL(urlPath);
            con = (HttpURLConnection) myUrl.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            StringBuilder content = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                br.lines().forEach( line -> {
                    content.append(line);
                    content.append(System.lineSeparator());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return content.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** Method to make quick GET request and get the JSON response data*/
    public static String httpsPOSTRequest(@NotNull final String urlPath, @NotNull final RoomUpdate roomUpdate) {
        try {
            HttpURLConnection con;
            URL myUrl = new URL(urlPath);
            con = (HttpURLConnection) myUrl.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            Gson gson = new Gson();

            byte[] out = gson.toJson(roomUpdate, RoomUpdate.class).getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            con.setFixedLengthStreamingMode(length);
            con.connect();
            try(OutputStream os = con.getOutputStream()) {
                os.write(out);
            }
// Do something with http.getInputStream()

            StringBuilder content = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                br.lines().forEach( line -> {
                    content.append(line);
                    content.append(System.lineSeparator());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return content.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

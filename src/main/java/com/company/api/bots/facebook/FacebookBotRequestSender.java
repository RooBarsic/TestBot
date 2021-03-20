package com.company.api.bots.facebook;

import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetFile;
import com.company.utils.ProcessStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FacebookBotRequestSender implements BotRequestSender {
    private final String DEFAULT_IMG_URL = "https://scontent-hel3-1.xx.fbcdn.net/v/t1.0-9/163158886_103354485181184_712371351835235755_n.png?_nc_cat=106&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=EN16qaVkmcIAX_NwBHt&_nc_ht=scontent-hel3-1.xx&oh=ad5d7e372fba0726b856449c7556c503&oe=607B35A8";
    private final String PAGE_ACCESS_TOKEN;
    private final String BASE_URL;
    private final String ENTITY_SENDING_URL = "https://graph.facebook.com/v9.0/me/messages?access_token=";


    public FacebookBotRequestSender(@NotNull final String PAGE_ACCESS_TOKEN, @NotNull final String BASE_URL) {
        this.PAGE_ACCESS_TOKEN = PAGE_ACCESS_TOKEN;
        this.BASE_URL = BASE_URL;
    }

    @Override
    public ProcessStatus sendBotNetBox(@NotNull BotNetBox botNetBox) {
        return sendBoxNewVersion(botNetBox);

        //  return sendBoxOldVersion(botNetBox);
    }

    private ProcessStatus sendBoxNewVersion(@NotNull final BotNetBox botNetBox) {
        final String receiverChatId = botNetBox.getReceiverChatId();
        String responseText = botNetBox.getMessage();

        // send files if has some
        if (botNetBox.hasFiles()) {
            List<BotNetFile> filesList = botNetBox.getFilesList();
            for (BotNetFile file : filesList) {
                sendBotNetFile(receiverChatId, file);
            }
        }

        if (botNetBox.getMessage().length() == 0 && botNetBox.hasButtons() == false) {
            return ProcessStatus.SUCCESS;
        }

        JSONObject responseJson = new JSONObject();

        // add receiver id
        final JSONObject recipientJsonObject = new JSONObject();
        recipientJsonObject.put("id", receiverChatId);
        responseJson.put("recipient", recipientJsonObject);

        // add message type
        responseJson.put("messaging_type", "RESPONSE");

        // add response text to message
        final JSONObject messageTextWithButtonsJson = new JSONObject();
        messageTextWithButtonsJson.put("text", responseText);

        // add buttons to message if has some
        if (botNetBox.hasButtons()) {
            final JSONArray buttonsArrayJson = new JSONArray();
            List<List<BotNetButton>> buttonsMatrix = botNetBox.getButtonsMatrix();
            for (List<BotNetButton> buttonsInRow : buttonsMatrix) {
                for (BotNetButton botNetButton : buttonsInRow) {
                    final JSONObject buttonJson = new JSONObject();
                    buttonJson.put("content_type", "text");
                    buttonJson.put("title", botNetButton.getButtonHiddenText());
                    buttonJson.put("payload", "<POSTBACK_PAYLOAD>");
                    buttonJson.put("image_url", DEFAULT_IMG_URL);

                    buttonsArrayJson.put(buttonJson);
                }
            }
            messageTextWithButtonsJson.put("quick_replies", buttonsArrayJson);
        }

        responseJson.put("message", messageTextWithButtonsJson);


        sendTextJsonMessage(responseJson.toString());
        return ProcessStatus.SUCCESS;
    }

    public ProcessStatus sendBoxOldVersion(@NotNull BotNetBox botNetBox) {
        final String receiverChatId = botNetBox.getReceiverChatId();
        String responseText = botNetBox.getMessage();
        System.out.println(" ------- old text : " + responseText);
        responseText = coverSpecialSymbols(responseText);
        System.out.println(" ------- old text : " + responseText);

        // send files if has some
        if (botNetBox.hasFiles()) {
            List<BotNetFile> filesList = botNetBox.getFilesList();
            for (BotNetFile file : filesList) {
                sendBotNetFile(receiverChatId, file);
            }
        }

        if (botNetBox.getMessage().length() == 0 && botNetBox.hasButtons() == false) {
            return ProcessStatus.SUCCESS;
        }

        final StringBuilder responseJsonStr = new StringBuilder("");
        responseJsonStr
                .append("{\n")
                .append("  \"recipient\":{\n")
                .append("    \"id\":\"" + receiverChatId + "\"\n")
                .append("  },\n")
                .append("  \"messaging_type\": \"RESPONSE\",\n")
                .append("  \"message\":{\n");

        // add buttons if has some
        if (botNetBox.hasButtons()) {
            responseJsonStr
                    .append("    \"text\": \"" + responseText + "\",\n")
                    .append("    \"quick_replies\":[\n");
            List<List<BotNetButton>> buttonsMatrix = botNetBox.getButtonsMatrix();
            boolean isFirstButton = true;
            for (List<BotNetButton> buttonsInRow : buttonsMatrix) {
                for (BotNetButton botNetButton : buttonsInRow) {
                    if (isFirstButton)
                        responseJsonStr.append("      {\n");
                    else
                        responseJsonStr.append("      ,{\n"); // separate buttons descriptions by coma
                    isFirstButton = false;

                    responseJsonStr
                            .append("        \"content_type\":\"text\",\n")
                            .append("        \"title\":\"" + botNetButton.getButtonHiddenText() + "\",\n")
                            .append("        \"payload\":\"<POSTBACK_PAYLOAD>\",\n")
                            .append("        \"image_url\":\"" + DEFAULT_IMG_URL + "\"\n")
                            .append("      }\n");
                }
            }
            responseJsonStr
                    .append("    ]\n");
        } else {
            responseJsonStr.append("    \"text\": \"" + responseText + "\"\n");
        }
        responseJsonStr
                .append("  }\n")
                .append("}");

        sendTextJsonMessage(responseJsonStr.toString());
        return null;
    }

    /** Method for sending files throw Facebook platform*/
    @Override
    public ProcessStatus sendBotNetFile(@NotNull final String chatId, @NotNull final BotNetFile botNetFile) {
        try {
            System.out.println("FACEBOOK bot :::: sending file started");
            final String pathToFile = botNetFile.getFullPath();

            final MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("recipient", new StringBody("{\"id\":\"" + chatId + "\"}"));

            // identify type of file
            switch (botNetFile.getType()) {
                case IMAGE:
                    //for sending photos
                    mpEntity.addPart("message", new StringBody("{\"attachment\":{\"type\":\"image\", \"payload\":{\"is_reusable\":false}}}"));
                    mpEntity.addPart("filedata", new FileBody(new File(pathToFile), "image/png"));
                    break;
                case GIF:
                    //for sending photos
                    mpEntity.addPart("message", new StringBody("{\"attachment\":{\"type\":\"image\", \"payload\":{\"is_reusable\":false}}}"));
                    mpEntity.addPart("filedata", new FileBody(new File(pathToFile), "image/gif"));
                    break;
                case AUDIO:
                case VOICE:
                    //for sending audios
                    mpEntity.addPart("filedata", new FileBody(new File(pathToFile), "audio/mp4"));
                    mpEntity.addPart("message", new StringBody("{\"attachment\":{\"type\":\"audio\", \"payload\":{\"is_reusable\":false}}}"));
                    break;
                case VIDEO_NOTE:
                case VIDEO:
                    //for sending videos
                    mpEntity.addPart("filedata", new FileBody(new File(pathToFile), "video/mp4"));
                    mpEntity.addPart("message", new StringBody("{\"attachment\":{\"type\":\"video\", \"payload\":{\"is_reusable\":false}}}"));
                    break;
                default:
                    //for sending photos
                    mpEntity.addPart("filedata", new FileBody(new File(pathToFile), "file"));
                    mpEntity.addPart("message", new StringBody("{\"attachment\":{\"type\":\"file\", \"payload\":{\"is_reusable\":false}}}"));
                    break;
            }

            sendEntityByPost(mpEntity);
            System.out.println("FACEBOOK bot :::: sending file was completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ProcessStatus.SUCCESS;

    }

    private String coverSpecialSymbols(@NotNull final String message) {
        final Set<Character> specialSymbols = new HashSet<>();
        specialSymbols.add('\n');
        specialSymbols.add('\t');
        specialSymbols.add('"');
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char e = message.charAt(i);
            if (message.charAt(i) == '\n') {
                stringBuilder.append("\\n");
            }
            else if (message.charAt(i) == '\t') {
                stringBuilder.append("\\t");
            } else {
                stringBuilder.append(message.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    private ProcessStatus sendTextJsonMessage(@NotNull String messageJsonStr) {
        System.out.println("\n-----------  Sending response message  -------------------\n");
        try {
            System.out.println("FROM FACEBOOK BOT::: sending message is :" + messageJsonStr);

            String url = BASE_URL + "?access_token=" + PAGE_ACCESS_TOKEN;

            System.out.println("FROM FACEBOOK BOT::: sending jsonResponseText is :" + messageJsonStr);

            HttpURLConnection con;
            URL myUrl = new URL(url);
            con = (HttpURLConnection) myUrl.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = messageJsonStr.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }

            StringBuilder content = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ProcessStatus.FAILED;
            }

            System.out.print("Facebook response for our message : " + content.toString());
            con.disconnect();

            return ProcessStatus.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ProcessStatus.FAILED;
        } finally {
            System.out.println("----- finished");
        }
    }

    /** Sends filled MultipartEntity by POST request to Facebook */
    private ProcessStatus sendEntityByPost(@NotNull final MultipartEntity mpEntity) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(ENTITY_SENDING_URL + PAGE_ACCESS_TOKEN);

            httppost.setEntity(mpEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println(EntityUtils.toString(resEntity));
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ProcessStatus.SUCCESS;

    }
}

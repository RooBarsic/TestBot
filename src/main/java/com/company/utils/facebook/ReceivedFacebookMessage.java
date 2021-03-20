package com.company.utils.facebook;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReceivedFacebookMessage {
    private static final String DEFAULT_IMG_URL = "https://www.soyuz.ru/public/uploads/files/2/7439415/2020051814170096742ace98.jpg";

    private String senderId = "";
    private String recipientId = "";
    private String messageText = "";
    private String messageId = "";
    private String time = "";
    private List<ReceivedFacebookAttachment> receivedAttachments = new ArrayList<>();

    public ReceivedFacebookMessage(@NotNull final String messageJsonText) {
        JSONObject jsonObject = new JSONObject(messageJsonText);
        try {
            // try to get message text if it's available
            messageText = jsonObject
                    .getJSONArray("entry")
                    .getJSONObject(0)
                    .getJSONArray("messaging")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("text");
        } catch (Exception e) {
            messageText = "";
            //e.printStackTrace();
            System.out.println("Facebook report ::: message has no text");
        }

        try {
            // try to get message attachment if it's available
            JSONArray attachments = jsonObject
                    .getJSONArray("entry")
                    .getJSONObject(0)
                    .getJSONArray("messaging")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getJSONArray("attachments");
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.getJSONObject(i);
                ReceivedFacebookAttachment attachment1 = new ReceivedFacebookAttachment(attachment);
                receivedAttachments.add(attachment1);
                System.out.println(" facebook attachment ::: " +
                        " id = " + i +
                        " type = " + attachment1.getType() +
                        " fileName = " + attachment1.getFileName() +
                        " url = " + attachment1.getLoadingUrl());
            }

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Facebook report ::: message has no attachment");
        }

        JSONArray entry = jsonObject.getJSONArray("entry");
        JSONObject jsonObject1 = entry.getJSONObject(0);
        JSONArray messaging = jsonObject1.getJSONArray("messaging");
        JSONObject jsonObject2 = messaging.getJSONObject(0);

        senderId = jsonObject
                .getJSONArray("entry")
                .getJSONObject(0)
                .getJSONArray("messaging")
                .getJSONObject(0)
                .getJSONObject("sender")
                .getString("id");

        recipientId = jsonObject
                .getJSONArray("entry")
                .getJSONObject(0)
                .getJSONArray("messaging")
                .getJSONObject(0)
                .getJSONObject("recipient")
                .getString("id");

        messageId = jsonObject
                .getJSONArray("entry")
                .getJSONObject(0)
                .getString("id");

        time = Integer.toString(jsonObject
                .getJSONArray("entry")
                .getJSONObject(0)
                .getInt("time"));


    }

    public String getMessageText() {
        return messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTime() {
        return time;
    }

    public boolean hasAttachment() {
        return !receivedAttachments.isEmpty();
    }

    public List<ReceivedFacebookAttachment> getReceivedAttachments() {
        return new LinkedList<>(receivedAttachments);
    }

    public String messageInfo() {
        return "sender_id = " + senderId + "\n" +
                "recipient_id = " + recipientId + "\n" +
                "message_id = " + messageId + "\n" +
                "time = " + time + "\n" +
                "text = " + messageText + "\n";
    }
}

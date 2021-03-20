package com.company.utils.facebook;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ReceivedFacebookAttachment {
    private String type = "";
    private String loadingUrl = "";
    private String fileName = "";
    private boolean parsedStatus = false;

    ReceivedFacebookAttachment(@NotNull final JSONObject attachment) {
        try {
            this.type = attachment.getString("type");
            this.loadingUrl = attachment.getJSONObject("payload").getString("url");
            final String buff[] = loadingUrl.split("\\?")[0].split("/");
            this.fileName = buff[buff.length - 1];
            this.parsedStatus = true;
        } catch (Exception e) {
            this.parsedStatus = false;
        }
    }

    public boolean getParsedStatus() {
        return parsedStatus;
    }

    public String getType() {
        return type;
    }

    public String getLoadingUrl() {
        return loadingUrl;
    }

    public String getFileName() {
        return fileName;
    }
}

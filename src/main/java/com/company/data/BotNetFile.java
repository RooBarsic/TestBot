package com.company.data;

import org.jetbrains.annotations.NotNull;

public class BotNetFile {
    private BotNetFileType type = null;
    private String loadingUrl = "";
    private String fileName = "";
    private String localStoragePath = "";
    private boolean fileOkStatus = false;

    public BotNetFile(@NotNull final BotNetFileType type,
                      @NotNull final String fileName,
                      @NotNull final String localStoragePath,
                      @NotNull final String loadingUrl){
        this.type = type;
        this.loadingUrl = loadingUrl;
        this.fileName = fileName;
        this.localStoragePath = localStoragePath;
    }

    public BotNetFile(){ }

    public boolean isFileOk() {
        return fileOkStatus;
    }

    public void setFileOkStatus(final boolean fileOkStatus) {
        this.fileOkStatus = fileOkStatus;
    }

    public String getFullPath() {
        return localStoragePath + fileName;
    }

    public BotNetFileType getType() {
        return type;
    }

    public void setType(BotNetFileType type) {
        this.type = type;
    }

    public String getLoadingUrl() {
        return loadingUrl;
    }

    public void setLoadingUrl(String loadingUrl) {
        this.loadingUrl = loadingUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalStoragePath() {
        return localStoragePath;
    }

    public void setLocalStoragePath(String localStoragePath) {
        this.localStoragePath = localStoragePath;
    }


    public static BotNetFileType convertToBotNetFileType(final String type) {
        switch (type) {
            case "image": return BotNetFileType.IMAGE;
            case "gif": return BotNetFileType.GIF;
            case "voice": return BotNetFileType.VOICE;
            case "audio": return BotNetFileType.AUDIO;
            case "video": return BotNetFileType.VIDEO;
            case "file": return BotNetFileType.DOCUMENT;
        }
        return BotNetFileType.DOCUMENT;
    }

}

package com.company.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Farrukh Karimov
 */
public class BotNetBox {
    private String receiverChatId;
    private String message;
    private List<List<BotNetButton>> buttonsMatrix;

    public BotNetBox() {
        buttonsMatrix = new LinkedList<>();
        buttonsMatrix.add(new LinkedList<>());
    }

    /** Method to add button to the last row of buttons */
    public BotNetBox addButton(@NotNull final BotNetButton botNetButton) {
        buttonsMatrix.get(buttonsMatrix.size() - 1).add(botNetButton);
        return this;
    }

    /** Method to set new row of buttons */
    public BotNetBox setNewButtonsLine() {
        buttonsMatrix.add(new LinkedList<>());
        return this;
    }

    public void cleanButtons() {
        buttonsMatrix.clear();
        setNewButtonsLine();
    }

    public boolean hasButtons() {
        return getButtonsMatrix().size() > 1 || getButtonsMatrix().get(0).size() > 0;
    }

    public List<List<BotNetButton>> getButtonsMatrix() {
        final List<List<BotNetButton>> result = new LinkedList<>();
        for (List<BotNetButton> buttonsRow : buttonsMatrix ) {
            result.add(new LinkedList<>(buttonsRow));
        }
        return result;
    }

    public String getReceiverChatId() {
        return receiverChatId;
    }

    public void setReceiverChatId(String receiverChatId) {
        this.receiverChatId = receiverChatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull final String message) {
        this.message = message;
    }

    //TODO transfer file working functions
    public boolean hasFiles() {
        return false;
    }

    public List<BotNetFile> getFilesList() {
        return new ArrayList<>();
    }
}

package com.company.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Farrukh Karimov
 */
public class BotNetButton {
    private String buttonText;
    private String buttonHiddenText;

    public BotNetButton(@NotNull final String buttonText, @NotNull final String buttonHiddenText) {
        this.buttonText = buttonText;
        this.buttonHiddenText = buttonHiddenText;
    }

    public BotNetButton() {
        buttonHiddenText = "";
        buttonHiddenText = "";
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonHiddenText() {
        return buttonHiddenText;
    }

    public void setButtonHiddenText(String buttonHiddenText) {
        this.buttonHiddenText = buttonHiddenText;
    }
}

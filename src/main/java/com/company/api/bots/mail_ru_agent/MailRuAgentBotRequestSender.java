package com.company.api.bots.mail_ru_agent;

import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetButton;
import com.company.data.BotNetFile;
import com.company.utils.ProcessStatus;
import org.jetbrains.annotations.NotNull;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.api.entity.InlineKeyboardButton;
import ru.mail.im.botapi.api.entity.SendTextRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for sending messages from MailRuAgent bot to MailRuAgent user
 *
 * @author Farrukh Karimov
 */
public class MailRuAgentBotRequestSender implements BotRequestSender {
    protected final BotApiClientController apiClientController;

    public MailRuAgentBotRequestSender(@NotNull BotApiClientController apiClientController) {
        this.apiClientController = apiClientController;
    }

    @Override
    public ProcessStatus sendBotNetBox(@NotNull final BotNetBox botNetBox) {
        final String receiverChatId = botNetBox.getReceiverChatId();
        final String responseText = botNetBox.getMessage();

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

        SendTextRequest sendTextRequest = new SendTextRequest();

        sendTextRequest.setChatId(receiverChatId);
        sendTextRequest.setText(responseText);

        // add buttons if has some
        if (botNetBox.hasButtons()) {
            final List<List<BotNetButton>> buttonsMatrixFromBox = botNetBox.getButtonsMatrix();
            final List<List<InlineKeyboardButton>> buttonsMatrix = buttonsMatrixFromBox.stream()
                    .map(buttonsInRow -> buttonsInRow.stream()
                            .map(button -> InlineKeyboardButton.callbackButton(button.getButtonText(), button.getButtonHiddenText()))
                            .collect(Collectors.toList())
                    ).collect(Collectors.toList());

            /**
             * Analog of that ......
             buttonsMatrix = new ArrayList<>();
             for (List<BotNetButton> buttonsInRow : buttonsMatrixFromBox) {
             buttonsMatrix.add(new ArrayList<>());
             for (BotNetButton botNetButton : buttonsInRow) {
             final InlineKeyboardButton mailRuButton = InlineKeyboardButton
             .callbackButton(
             botNetButton.getButtonText(),
             botNetButton.getButtonHiddenText()
             );

             buttonsMatrix.get(buttonsMatrix.size() - 1).add(mailRuButton);
             }
             }
             */

            sendTextRequest.setKeyboard(buttonsMatrix);
        }

        // send response
        try {
            apiClientController.sendTextMessage(sendTextRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProcessStatus sendBotNetFile(@NotNull final String chatId, @NotNull final BotNetFile botNetFile) {
        try {
            final String pathToFile = botNetFile.getFullPath();

//            SendFileRequest sendFileRequest = new SendFileRequest();
//            sendFileRequest.setChatId(chatId);
//            sendFileRequest.setFile(new File(pathToFile));
//
//            apiClientController.sendFile(sendFileRequest);
            apiClientController.sendFile(chatId, new File(botNetFile.getFullPath()));
            return ProcessStatus.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ProcessStatus.FAILED;
    }

}

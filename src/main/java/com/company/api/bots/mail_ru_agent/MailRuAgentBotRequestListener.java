package com.company.api.bots.mail_ru_agent;

import com.company.api.bots.BotRequestListener;
import com.company.api.bots.BotRequestSender;
import com.company.data.BotNetBox;
import com.company.data.BotNetFile;
import com.company.data.BotNetFileType;
import com.company.data.BotNetMail;
import com.company.data.database.BotNetDataBase;
import org.jetbrains.annotations.NotNull;
import ru.mail.im.botapi.BotApiClient;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.fetcher.OnEventFetchListener;
import ru.mail.im.botapi.fetcher.event.CallbackQueryEvent;
import ru.mail.im.botapi.fetcher.event.Event;
import ru.mail.im.botapi.fetcher.event.NewMessageEvent;
import ru.mail.im.botapi.fetcher.event.parts.File;
import ru.mail.im.botapi.fetcher.event.parts.Part;
import ru.mail.im.botapi.fetcher.event.parts.Sticker;
import ru.mail.im.botapi.fetcher.event.parts.Voice;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Class for receiving messages from MailRuAgent
 *
 * @author Farrukh Karimov
 */
public class MailRuAgentBotRequestListener implements BotRequestListener, OnEventFetchListener {
    private final BotApiClient apiClient;
    private final BotApiClientController apiClientController;
    private final String TOKEN;
    private final BotNetDataBase botNetDataBase;
    private final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue;

    public MailRuAgentBotRequestListener(@NotNull final String token,
                                         @NotNull final BotNetDataBase botNetDataBase,
                                         @NotNull final ConcurrentLinkedDeque<BotNetMail> receivedMailsQueue) {
        TOKEN = token;
        apiClient = new BotApiClient(TOKEN);
        apiClientController = BotApiClientController.startBot(apiClient);
        this.botNetDataBase = botNetDataBase;
        this.receivedMailsQueue = receivedMailsQueue;
    }

    /** Method for starting MailRuAgentBot */
    public void startBot() {
        apiClient.addOnEventFetchListener(this);
    }

    @Override
    public void onEventFetch(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            System.out.println(" Mail_Ru_Agent -----" + i);
            final Event event = events.get(i);
            final BotNetMail receivedBotNetMail = new BotNetMail();

            if (event instanceof NewMessageEvent) {
                final NewMessageEvent newMessageEvent = (NewMessageEvent) events.get(i);

                // get MailRu user chat_id
                receivedBotNetMail.setUserChatId(newMessageEvent.getChat().getChatId());

                // set received message
                receivedBotNetMail.setMessage(newMessageEvent.getText());

                System.out.println("MailRu :::: message = " + receivedBotNetMail.getMessage());
                //TODO: when user users gifs from "fmedia.giphy.com", we receiving the link to the gif instead of that gif file
                //TODO: already tried to download, but we wasn't able to play the downloaded file at all

                // add attachments if has some
                if ((newMessageEvent.getParts() != null) && (newMessageEvent.getParts().size() > 0)) {
                    //TODO: assume that if request has attachment than it doesn't have any text message
                    //TODO: When user sends attachment the newMessageEvent.getText() - would contain the link for sharing/loading that attachment
                    receivedBotNetMail.setMessage("");
                    final List<Part> parts = newMessageEvent.getParts();
                    for (final Part part : parts) {
                        final BotNetFile botNetFile = new BotNetFile();

                        if (part instanceof File) {
                            final File file = (File) part;
                            //TODO transfer
                            // botNetFile = MailRuUtils.loadAttachmentToBotNetFile(file.getFileId(), TOKEN, "mail/file/");
                        }
                        else if (part instanceof Voice) {
                            final Voice voice = (Voice) part;
                            //TODO transfer
                            // botNetFile = MailRuUtils.loadAttachmentToBotNetFile(voice.getFileId(), TOKEN, "mail/voice/");
                            botNetFile.setType(BotNetFileType.VOICE);
                        }
                        else if (part instanceof Sticker) {
                            final Sticker sticker = (Sticker) part;
                            //TODO transfer
                            // botNetFile = MailRuUtils.loadAttachmentToBotNetFile(sticker.getFileId(), TOKEN, "mail/sticker/");
                            botNetFile.setType(BotNetFileType.IMAGE);
                        } else {
                            continue;
                        }

                        if (botNetFile.isFileOk()) {
                            //add file to box

                            //TODO transfer
                            // botNetBox.addFile(botNetFile);
                        }
                    }
                }
            }
            else if (event instanceof CallbackQueryEvent) {
                final CallbackQueryEvent callbackQueryEvent = (CallbackQueryEvent) events.get(i);

                // get BotNet user
                receivedBotNetMail.setUserChatId(callbackQueryEvent.getMessageChat().getChatId());


                // set sender user, received message, and receiving UiPlatform
                receivedBotNetMail.setMessage(callbackQueryEvent.getCallbackData());
            }
            else {
                continue;
            }

            // add filled box to the processing queue
            receivedMailsQueue.addLast(receivedBotNetMail);
        }

    }

    @Override
    public BotRequestSender getBotRequestSender() {
        return new MailRuAgentBotRequestSender(apiClientController);
    }
}



package com.company.api.bots;

import com.company.data.BotNetBox;
import com.company.data.BotNetMail;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedDeque;

public class RoomBotLogic {
    private final BotRequestSender botRequestSender;
    private final ConcurrentLinkedDeque<BotNetMail> receivedMails;


    public RoomBotLogic(@NotNull final BotRequestSender botRequestSender,
                        @NotNull final ConcurrentLinkedDeque<BotNetMail> receivedMails) {
        this.botRequestSender = botRequestSender;
        this.receivedMails = receivedMails;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                // wait while there are no boxes for processing
                while (receivedMails.isEmpty()) {

                }

                // get and remove first box
                final BotNetMail botNetMail = receivedMails.removeFirst();

                // process box
                processBotNetMail(botNetMail);

//                botNetMail.getSenderUser().setLastCommand(botNetMail.getParsedCommand());
//                botNetDataBase.updateUser(botNetMail.getSenderUser());
            }
        }).start();
    }

    private void deliverBox(@NotNull final BotNetBox botNetBox) {
        System.out.println("LOG: delivering new box......");
        System.out.println("LOG: message = " + botNetBox.getMessage());
        botRequestSender.sendBotNetBox(botNetBox);
    }

    private void processBotNetMail(@NotNull final BotNetMail botNetMail) {
        try {
            BotNetBox botNetBox = new BotNetBox();
            botNetBox.setReceiverChatId(botNetMail.getUserChatId());
            botNetBox.setMessage("Hello from HackNU");

            deliverBox(botNetBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

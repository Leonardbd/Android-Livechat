package com.fudicia.usupply1;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageTime;

    public ChatMessage(String messageText, String messageUser)
    {
        this.messageText = messageText;
        this.messageUser = messageUser;

        /*
        long millisec = Instant.now().toEpochMilli();

        long minute = (millisec / (1000 * 60)) % 60;
        long hour = ((millisec / (1000 * 60 * 60)) % 24);

        messageTime = String.format("%02d:%02d", hour, minute); */

        SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm", Locale.FRANCE);

        messageTime = sdf.format(new Date());

    }

    public ChatMessage()
    {

    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }
}

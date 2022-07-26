package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

public class Channel {
    private long channelId;
    private String lastMessageReceivedDate;
    private ArrayList<Message> messages;

    public Channel(long channelId, String lastMessageReceivedDate, ArrayList<Message> messages) {
        this.channelId = channelId;
        this.lastMessageReceivedDate = lastMessageReceivedDate;
        this.messages = messages;
    }

    public Channel(long channelId) {
        this.channelId = channelId;
    }

    public Channel() {

    }

    @JsonGetter
    public long getChannelId() {
        return channelId;
    }

    @JsonSetter
    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    @JsonGetter
    public String getLastMessageReceivedDate() {
        return lastMessageReceivedDate;
    }

    @JsonSetter
    public void setLastMessageReceivedDate(String lastMessageReceivedDate) {
        this.lastMessageReceivedDate = lastMessageReceivedDate;
    }

    @JsonGetter
    public ArrayList<Message> getMessages() {
        return messages;
    }

    @JsonSetter
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public Message getMessageById(long message_id) {
        for (Message m : messages) {
            if (m.getMessageId() == message_id) {
                return m;
            }
        }
        return null;
    }
}
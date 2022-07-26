package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Message {
    private long messageId;
    private String content;
    private long author;

    public Message(long messageId, String content, long author) {
        this.messageId = messageId;
        this.content = content;
        this.author = author;
    }

    public Message(long messageId) {
        this.messageId = messageId;
    }

    public Message() {

    }

    @JsonGetter
    public long getMessageId() {
        return messageId;
    }

    @JsonSetter
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @JsonGetter
    public String getContent() {
        return content;
    }

    @JsonSetter
    public void setContent(String content) {
        this.content = content;
    }

    @JsonGetter
    public long getAuthor() {
        return author;
    }

    @JsonSetter
    public void setAuthor(long author) {
        this.author = author;
    }

}
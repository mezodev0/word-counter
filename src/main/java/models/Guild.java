package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

public class Guild {
    private long guildId;
    private String[] blacklist;

    private ArrayList<Channel> channels;

    public Guild(long guildId, ArrayList<Channel> channels) {
        this.guildId = guildId;
        this.channels = channels;
    }

    public Guild(long guildId) {
        this.guildId = guildId;
    }


    public Guild() {

    }

    @JsonGetter
    public long getGuildId() {
        return guildId;
    }

    @JsonSetter
    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    @JsonGetter
    public ArrayList<Channel> getChannels() {
        return channels;
    }

    @JsonSetter
    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public String[] getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(String[] blacklist) {
        this.blacklist = blacklist;
    }

    public Channel getChannelById(long channelId) {
        for (Channel c : channels) {
            if (c.getChannelId() == channelId) {
                return c;
            }
        }
        return null;
    }

}
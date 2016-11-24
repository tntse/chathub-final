package edu.sfsu.csc780.chathub.model;

import java.util.List;

/**
 * Created by david on 11/21/16.
 */

public class Channel{
    private String channelType;
    private List<String> userList;
    private String channelName;
    private String channelTopic;

    public Channel() {}

    public Channel(List<String> userList, String channelType, String channelName) {
        this.userList = userList;
        this.channelType = channelType;
        this.channelName = channelName;
    }

    public Channel(List<String> userList, String channelName, String channelType, String channelTopic) {
        this(userList, channelType, channelName);
        this.channelTopic = channelTopic;
    }

    public String getChannelTopic() {
        return channelTopic;
    }

    public void setChannelTopic(String channelTopic) {
        this.channelTopic = channelTopic;
    }

    public List<String> getUserList() {
        return userList;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}

package edu.sfsu.csc780.chathub.model;

import java.util.HashMap;

/**
 * Created by david on 11/21/16.
 */

public class Channel{
    private HashMap<String, String> userList;
    private String channelName;
    private String channelTopic;


    public Channel() {}

    public Channel(HashMap<String, String> userList, String channelName) {
        this.userList = userList;
        this.channelName = channelName;
    }

    public Channel(HashMap<String, String> userList, String channelName, String channelTopic) {
        this(userList, channelName);
        this.channelTopic = channelTopic;
    }

    public String getChannelTopic() {
        return channelTopic;
    }

    public void setChannelTopic(String channelTopic) {
        this.channelTopic = channelTopic;
    }

    public HashMap<String, String> getUserList() {
        return userList;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

}

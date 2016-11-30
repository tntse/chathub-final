package edu.sfsu.csc780.chathub.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by david on 11/21/16.
 */

public class Channel{
    private String channelType;
    private HashMap<String, String> userList;
    private String channelName;
    private String channelTopic;
    private boolean isPm;

    public Channel() {}

    public Channel(HashMap<String, String> userList, String channelType, String channelName, boolean isPm) {
        this.userList = userList;
        this.channelType = channelType;
        this.channelName = channelName;
        this.isPm = isPm;
    }

    public Channel(HashMap<String, String> userList, String channelName, String channelType, String channelTopic, boolean isPm) {
        this(userList, channelType, channelName, isPm);
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

    public boolean isPm() {
        return isPm;
    }

    public void setPm(boolean pm) {
        isPm = pm;
    }
}

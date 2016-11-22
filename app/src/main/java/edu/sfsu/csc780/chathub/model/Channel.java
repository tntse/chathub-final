package edu.sfsu.csc780.chathub.model;

import java.util.List;

/**
 * Created by david on 11/21/16.
 */

public class Channel{
    private ChannelType channelType;
    private List<String> userList;
    private List<ChatMessage> chatMessageList;
    private String channelTopic;

    public Channel() {}

    public Channel(List<String> userList, ChannelType channelType) {
        this.userList = userList;
        this.channelType = channelType;
    }

    public Channel(List<String> userList, String channelTopic, ChannelType channelType) {
        this(userList, channelType);
        this.channelTopic = channelTopic;
    }

    public Channel(String channelTopic, List<ChatMessage> chatMessageList, List<String> userList, ChannelType channelType) {
        this(userList, channelTopic, channelType);
        this.chatMessageList = chatMessageList;
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

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }
}

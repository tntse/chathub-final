package edu.sfsu.csc780.chathub.model;

import java.util.List;

/**
 * Created by david on 11/23/16.
 */

public class User {
    private String username;
    private List<String> channels;

    public User() {}

    public User(String username, List<String> channels) {
        this.username = username;
        this.channels = channels;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

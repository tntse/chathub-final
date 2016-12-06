package edu.sfsu.csc780.chathub.model;

import java.util.HashMap;

/**
 * Created by david on 11/23/16.
 */

public class User {
    private HashMap<String, HashMap<String, String>> username;

    public User() {}

    public User(HashMap<String, HashMap<String, String>> username) {
        this.username = username;
    }

    public  HashMap<String, HashMap<String, String>> getUsername() {
        return username;
    }

    public void setUsername(HashMap<String, HashMap<String, String>> username) {
        this.username = username;
    }
}

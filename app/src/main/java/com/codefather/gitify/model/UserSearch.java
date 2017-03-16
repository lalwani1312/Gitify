package com.codefather.gitify.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * User search model
 * Created by hitesh-lalwani on 7/3/17.
 */

public class UserSearch extends BaseSearchModel {

    @SerializedName("items")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

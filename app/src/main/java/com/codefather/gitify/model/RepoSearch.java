package com.codefather.gitify.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Repository search model.
 * Created by hitesh-lalwani on 7/3/17.
 */

public class RepoSearch extends BaseSearchModel {

    @SerializedName("items")
    private List<Repository> repositories;

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}

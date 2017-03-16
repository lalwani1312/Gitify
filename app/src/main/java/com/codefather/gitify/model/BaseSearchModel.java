package com.codefather.gitify.model;

import com.google.gson.annotations.SerializedName;

/**
 * Search model.
 * This model is extended by RepoSearchModel and UserSearchModel.
 * <p>
 * Created by hitesh-lalwani on 7/3/17.
 */

public class BaseSearchModel {

    @SerializedName("total_count")
    private String totalCount;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}

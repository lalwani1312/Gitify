package com.codefather.gitify.utils;

/**
 * This class contains attributes of server.
 * Created by hitesh-lalwani on 8/3/17.
 */

public class Constants {

    public interface DataType {
        int RECENT = 0;
        int FAVORITE = 1;
        int SEARCH = 2;
    }

    public interface RepoAttributes {
        String STARS = "stars";
    }

    public interface UserAttributes {
        String FOLLOWERS = "followers";
    }

    public interface SortOrder {
        String DESC = "desc";
        String ASC = "asc";
    }
}

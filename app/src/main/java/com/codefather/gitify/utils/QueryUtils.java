package com.codefather.gitify.utils;

/**
 * This class modifies query for appropriate search results.
 * Created by hitesh-lalwani on 7/3/17.
 */

public class QueryUtils {
    public static String getRepoSearchQuery(String q) {
        return q + " in:" + "name,fullname";
    }

    public static String getUserSearchQuery(String q) {
        return q + " in:" + "login,email,fullname";
    }
}

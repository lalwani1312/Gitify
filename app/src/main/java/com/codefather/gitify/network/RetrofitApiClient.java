package com.codefather.gitify.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit api client
 * Created by hitesh-lalwani on 2/3/17.
 */
public class RetrofitApiClient {
    private static final String BASE_URL = "https://api.github.com/";


    private static GitHubService sGitHubService;

    public static void initialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sGitHubService = retrofit.create(GitHubService.class);
    }

    public static GitHubService getApiService() {
        return sGitHubService;
    }

}

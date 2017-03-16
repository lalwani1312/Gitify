package com.codefather.gitify.network;

import com.codefather.gitify.model.RepoSearch;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.User;
import com.codefather.gitify.model.UserSearch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This class consists of REST apis of github.
 * <p>
 * Created by hitesh-lalwani on 2/3/17.
 */

@SuppressWarnings("WeakerAccess")
public interface GitHubService {
    @GET("users/{user}")
    Call<User> getUser(@Path("user") String login);

    @GET("users/{user}/repos")
    Call<List<Repository>> getUserRepositories(@Path("user") String login);

    // https://api.github.com/search/repositories?q=retrofit in:name,fullname,description&sort=stars&order=desc&page=1&per_page=6
    @GET("search/repositories")
    Call<RepoSearch> searchRepositories(@Query("q") String q, @Query("sort") String sortBy,
                                        @Query("order") String orderBy, @Query("page") String page,
                                        @Query("per_page") int perPage);

    @GET("search/users")
    Call<UserSearch> searchUsers(@Query("q") String q, @Query("sort") String sortBy,
                                 @Query("order") String orderBy, @Query("page") String page,
                                 @Query("per_page") int perPage);
}

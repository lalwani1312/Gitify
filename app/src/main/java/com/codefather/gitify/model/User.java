package com.codefather.gitify.model;

import com.codefather.gitify.database.GitifyDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * User model
 * Created by hitesh-lalwani on 6/3/17.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(database = GitifyDatabase.class)
public class User extends BaseModel {

    //extra fields
    /*      "owner": {
                "login": "dtrupenn",
                "id": 872147,
                "avatar_url": "https://avatars2.githubusercontent.com/u/872147?v=3",
                "gravatar_id": "",
                "url": "https://api.github.com/users/dtrupenn",
                "html_url": "https://github.com/dtrupenn",
                "followers_url": "https://api.github.com/users/dtrupenn/followers",
                "following_url": "https://api.github.com/users/dtrupenn/following{/other_user}",
                "gists_url": "https://api.github.com/users/dtrupenn/gists{/gist_id}",
                "starred_url": "https://api.github.com/users/dtrupenn/starred{/owner}{/repo}",
                "subscriptions_url": "https://api.github.com/users/dtrupenn/subscriptions",
                "organizations_url": "https://api.github.com/users/dtrupenn/orgs",
                "repos_url": "https://api.github.com/users/dtrupenn/repos",
                "events_url": "https://api.github.com/users/dtrupenn/events{/privacy}",
                "received_events_url": "https://api.github.com/users/dtrupenn/received_events",
                "type": "User",
                "site_admin": false
            }
            "name": "Tom Preston-Werner",
            "company": null,
            "blog": "http://tom.preston-werner.com",
            "location": "San Francisco",
            "email": "tom@mojombo.com",
            "hireable": null,
            "bio": null,
            "public_repos": 61,
            "public_gists": 62,
            "followers": 20004,
            "following": 11,
            "created_at": "2007-10-20T05:24:19Z",
            "updated_at": "2017-02-28T12:02:57Z"*/

    @Column
    private transient boolean isFavorite;

    @Column
    @SerializedName("login")
    private String login;

    @Column
    @PrimaryKey
    @SerializedName("id")
    private String id;

    @Column
    @SerializedName("avatar_url")
    private String avatarUrl;

    @Column
    @SerializedName("url")
    private String url;

    @Column
    @SerializedName("html_url")
    private String htmlUrl;

    @Column
    @SerializedName("repos_url")
    private String reposUrl;

    @Column
    @SerializedName("score")
    private String score;

    @Column
    @SerializedName("name")
    private String name;

    @Column
    @SerializedName("company")
    private String company;

    @SerializedName("blog")
    private String blogUrl;

    @SerializedName("location")
    private String location;

    @Column
    @SerializedName("email")
    private String email;

    @SerializedName("public_repos")
    private String numPublicRepos;

    @SerializedName("public_gists")
    private String numPublicGists;

    @SerializedName("followers")
    private String numFollowers;

    @SerializedName("following")
    private String numFollowings;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumPublicRepos() {
        return numPublicRepos;
    }

    public void setNumPublicRepos(String numPublicRepos) {
        this.numPublicRepos = numPublicRepos;
    }

    public String getNumPublicGists() {
        return numPublicGists;
    }

    public void setNumPublicGists(String numPublicGists) {
        this.numPublicGists = numPublicGists;
    }

    public String getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(String numFollowers) {
        this.numFollowers = numFollowers;
    }

    public String getNumFollowings() {
        return numFollowings;
    }

    public void setNumFollowings(String numFollowings) {
        this.numFollowings = numFollowings;
    }
}

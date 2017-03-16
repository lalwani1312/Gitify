package com.codefather.gitify.model;

import com.codefather.gitify.database.GitifyDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Model class for user's repository.
 *
 * @author Hitesh Lalwani
 * @version 1.0
 */

@SuppressWarnings("WeakerAccess")
@Table(database = GitifyDatabase.class)
public class Repository extends BaseModel {

    /*{
        "id": 3081286,
            "name": "Tetris",
            "full_name": "dtrupenn/Tetris",
            "owner": {
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
            },
            "private": false,
            "html_url": "https://github.com/dtrupenn/Tetris",
            "description": "A C implementation of Tetris using Pennsim through LC4",
            "fork": false,
            "url": "https://api.github.com/repos/dtrupenn/Tetris",
            "forks_url": "https://api.github.com/repos/dtrupenn/Tetris/forks",
            "keys_url": "https://api.github.com/repos/dtrupenn/Tetris/keys{/key_id}",
            "collaborators_url": "https://api.github.com/repos/dtrupenn/Tetris/collaborators{/collaborator}",
            "teams_url": "https://api.github.com/repos/dtrupenn/Tetris/teams",
            "hooks_url": "https://api.github.com/repos/dtrupenn/Tetris/hooks",
            "issue_events_url": "https://api.github.com/repos/dtrupenn/Tetris/issues/events{/number}",
            "events_url": "https://api.github.com/repos/dtrupenn/Tetris/events",
            "assignees_url": "https://api.github.com/repos/dtrupenn/Tetris/assignees{/user}",
            "branches_url": "https://api.github.com/repos/dtrupenn/Tetris/branches{/branch}",
            "tags_url": "https://api.github.com/repos/dtrupenn/Tetris/tags",
            "blobs_url": "https://api.github.com/repos/dtrupenn/Tetris/git/blobs{/sha}",
            "git_tags_url": "https://api.github.com/repos/dtrupenn/Tetris/git/tags{/sha}",
            "git_refs_url": "https://api.github.com/repos/dtrupenn/Tetris/git/refs{/sha}",
            "trees_url": "https://api.github.com/repos/dtrupenn/Tetris/git/trees{/sha}",
            "statuses_url": "https://api.github.com/repos/dtrupenn/Tetris/statuses/{sha}",
            "languages_url": "https://api.github.com/repos/dtrupenn/Tetris/languages",
            "stargazers_url": "https://api.github.com/repos/dtrupenn/Tetris/stargazers",
            "contributors_url": "https://api.github.com/repos/dtrupenn/Tetris/contributors",
            "subscribers_url": "https://api.github.com/repos/dtrupenn/Tetris/subscribers",
            "subscription_url": "https://api.github.com/repos/dtrupenn/Tetris/subscription",
            "commits_url": "https://api.github.com/repos/dtrupenn/Tetris/commits{/sha}",
            "git_commits_url": "https://api.github.com/repos/dtrupenn/Tetris/git/commits{/sha}",
            "comments_url": "https://api.github.com/repos/dtrupenn/Tetris/comments{/number}",
            "issue_comment_url": "https://api.github.com/repos/dtrupenn/Tetris/issues/comments{/number}",
            "contents_url": "https://api.github.com/repos/dtrupenn/Tetris/contents/{+path}",
            "compare_url": "https://api.github.com/repos/dtrupenn/Tetris/compare/{base}...{head}",
            "merges_url": "https://api.github.com/repos/dtrupenn/Tetris/merges",
            "archive_url": "https://api.github.com/repos/dtrupenn/Tetris/{archive_format}{/ref}",
            "downloads_url": "https://api.github.com/repos/dtrupenn/Tetris/downloads",
            "issues_url": "https://api.github.com/repos/dtrupenn/Tetris/issues{/number}",
            "pulls_url": "https://api.github.com/repos/dtrupenn/Tetris/pulls{/number}",
            "milestones_url": "https://api.github.com/repos/dtrupenn/Tetris/milestones{/number}",
            "notifications_url": "https://api.github.com/repos/dtrupenn/Tetris/notifications{?since,all,participating}",
            "labels_url": "https://api.github.com/repos/dtrupenn/Tetris/labels{/name}",
            "releases_url": "https://api.github.com/repos/dtrupenn/Tetris/releases{/id}",
            "deployments_url": "https://api.github.com/repos/dtrupenn/Tetris/deployments",
            "created_at": "2012-01-01T00:31:50Z",
            "updated_at": "2016-02-09T14:01:44Z",
            "pushed_at": "2012-01-01T00:37:02Z",
            "git_url": "git://github.com/dtrupenn/Tetris.git",
            "ssh_url": "git@github.com:dtrupenn/Tetris.git",
            "clone_url": "https://github.com/dtrupenn/Tetris.git",
            "svn_url": "https://github.com/dtrupenn/Tetris",
            "homepage": "",
            "size": 496,
            "stargazers_count": 3,
            "watchers_count": 3,
            "language": "Assembly",
            "has_issues": true,
            "has_downloads": true,
            "has_wiki": true,
            "has_pages": false,
            "forks_count": 1,
            "mirror_url": null,
            "open_issues_count": 0,
            "forks": 1,
            "open_issues": 0,
            "watchers": 3,
            "default_branch": "master",
            "network_count": 1,
            "subscribers_count": 1
    }*/

    @Column
    private transient boolean isFavorite;

    @Column
    @PrimaryKey
    @SerializedName("id")
    private String id;

    @Column
    @SerializedName("name")
    private String name;

    @Column
    @SerializedName("full_name")
    private String fullName;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column
    @SerializedName("owner")
    @ForeignKey(saveForeignKeyModel = false)
    private User owner;

    @Column
    @SerializedName("private")
    private boolean isPrivate;

    @Column
    @SerializedName("html_url")
    private String htmlUrl;

    @Column
    @SerializedName("description")
    private String description;

    @Column
    @SerializedName("url")
    private String url;

    @Column
    @SerializedName("size")
    private String sizeInKb;

    @Column
    @SerializedName("language")
    private String language;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(String sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

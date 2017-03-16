package com.codefather.gitify.recent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefather.gitify.R;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.Repository_Table;
import com.codefather.gitify.model.User;
import com.codefather.gitify.model.User_Table;
import com.codefather.gitify.search.SearchFragment;
import com.codefather.gitify.search.ViewAllActivity;
import com.codefather.gitify.utils.Constants;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends SearchFragment {

    public RecentFragment() {
    }

    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Override
    public void onViewAllClicked(boolean isRepo) {
        ViewAllActivity.start(getContext(), isRepo, Constants.DataType.RECENT /*dataType*/, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setShouldShowSearch(false);
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshData();
    }

    public void refreshData() {
        clearHashMaps();

        List<Repository> repositories = getRepositories(false /*favOnly*/);
        List<User> users = getUsers(false /*favOnly*/);

        //no need to check fav status from db because the list is retrieved from db itself.
        //Therefore it already contains the fav status.
        setRepoDataToRepoCard(repositories, false);
        setRepoCardCount(repositories.size());

        setUsersToUserCard(users, false);
        setUserCardCount(users.size());
    }

    public List<Repository> getRepositories(boolean favOnly) {
        if (favOnly) {
            return SQLite.select().from(Repository.class)
                    .where(Repository_Table.isFavorite.is(true))
                    .queryList();
        } else {
            return SQLite.select().from(Repository.class)
                    .queryList();
        }
    }

    public List<User> getUsers(boolean favOnly) {
        if (favOnly) {
            return SQLite.select().from(User.class)
                    .where(User_Table.isFavorite.is(true))
                    .queryList();
        } else {
            return SQLite.select().from(User.class)
                    .where(User_Table.score.isNot(""))
                    .queryList();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refreshData();
        }
    }
}

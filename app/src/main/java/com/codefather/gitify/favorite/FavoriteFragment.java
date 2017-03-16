package com.codefather.gitify.favorite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.User;
import com.codefather.gitify.recent.RecentFragment;
import com.codefather.gitify.search.ViewAllActivity;
import com.codefather.gitify.utils.Constants;
import com.codefather.gitify.view.EntryCardView;

import java.util.List;

public class FavoriteFragment extends RecentFragment {
    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserCard.setAnimateLayoutChanges(true);
        mRepoCard.setAnimateLayoutChanges(true);
    }

    @Override
    public void onViewAllClicked(boolean isRepo) {
        ViewAllActivity.start(getContext(), isRepo, Constants.DataType.FAVORITE /*dataType*/, "");
    }

    @Override
    public void onFavClicked(EntryCardView.Entry entry) {
        super.onFavClicked(entry);
        refreshData();
    }

    @Override
    public List<Repository> getRepositories(boolean favOnly) {
        return super.getRepositories(true  /*favOnly*/);
    }

    @Override
    public List<User> getUsers(boolean favOnly) {
        return super.getUsers(true  /*favOnly*/);
    }
}

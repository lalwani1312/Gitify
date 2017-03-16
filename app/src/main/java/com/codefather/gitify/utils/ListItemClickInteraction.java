package com.codefather.gitify.utils;

import android.content.Context;

import com.codefather.gitify.detail.UserDetailActivity;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.User;
import com.codefather.gitify.model.User_Table;
import com.codefather.gitify.search.ViewAllAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

public class ListItemClickInteraction implements ViewAllAdapter.ClickInteraction {

    private Context mContext;

    public ListItemClickInteraction(Context context) {
        mContext = context;
    }

    @Override
    public boolean onFavoriteClicked(Repository repository) {
        repository.setFavorite(!repository.isFavorite());
        boolean success = saveRepository(repository);
        if (!success)
            repository.setFavorite(!repository.isFavorite());
        return success;
    }

    @Override
    public boolean onFavoriteClicked(User user) {
        user.setFavorite(!user.isFavorite());
        boolean success = user.save();
        if (!success)
            user.setFavorite(!user.isFavorite());
        return success;
    }

    @Override
    public void onItemClicked(Repository repository) {
        ChromeCustomTabs.start(mContext, repository.getHtmlUrl());
        saveRepository(repository);
    }

    @Override
    public void onItemClicked(User user) {
        user.save();
        UserDetailActivity.start(mContext, user.getLogin(), user.getUrl(), user.getReposUrl(), user.getAvatarUrl());
    }

    private boolean saveRepository(Repository repository) {
        User user = repository.getOwner();
        long count = SQLite.select(count()).from(User.class)
                .where(User_Table.id.is(user.getId())).count();
        if (count == 0) {
            user.save();
        }
        return repository.save();
    }
}

package com.codefather.gitify.search;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codefather.gitify.R;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to show repositories/ usernames in cards.
 * <p>
 * Created by hitesh-lalwani on 8/3/17.
 */

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    private static final int INITIAL_CAPACITY = 100;

    private List<Repository> mRepositories;
    private List<User> mUsers;
    private ClickInteraction mListener;
    private boolean mIsRepo;
    private Resources mResources;

    public ViewAllAdapter(Context context, ClickInteraction listener, boolean isRepo) {
        mListener = listener;
        mRepositories = new ArrayList<>(INITIAL_CAPACITY);
        mUsers = new ArrayList<>(INITIAL_CAPACITY);
        mIsRepo = isRepo;
        mResources = context.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item_view_all, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mIsRepo) {
            holder.repository = mRepositories.get(position);
            holder.header.setText(holder.repository.getName());
            holder.subHeader.setText(mResources.getString(R.string.repo_by, holder.repository.getOwner().getLogin()));
            holder.favButton.setSelected(holder.repository.isFavorite());
        } else {
            holder.user = mUsers.get(position);
            holder.header.setText(holder.user.getLogin());
            holder.subHeader.setText(mResources.getString(R.string.score, holder.user.getScore()));
            holder.favButton.setSelected(holder.user.isFavorite());
        }
    }

    @Override
    public int getItemCount() {
        return mIsRepo ? mRepositories.size() : mUsers.size();
    }

    public void addRepositories(List<Repository> repositories) {
        int oldCount = mRepositories.size();
        mRepositories.addAll(repositories);
        notifyDataSetChanged();
//        notifyItemRangeInserted(oldCount, repositories.size() - oldCount);
    }

    public void clear() {
        mRepositories.clear();
        notifyDataSetChanged();
    }

    public void clearAndAddRepositories(List<Repository> repositories) {
        mRepositories.clear();
        addRepositories(repositories);
    }

    void addUsers(List<User> users) {
        int oldCount = mUsers.size();
        mUsers.addAll(users);
        notifyDataSetChanged();
//        notifyItemRangeInserted(oldCount, users.size() - oldCount);
    }

    public interface ClickInteraction {
        boolean onFavoriteClicked(Repository repository);

        boolean onFavoriteClicked(User user);

        void onItemClicked(Repository repository);

        void onItemClicked(User user);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Repository repository;
        User user;
        RelativeLayout parentLayout;
        TextView header, subHeader;
        ImageButton favButton;

        ViewHolder(View itemView) {
            super(itemView);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            parentLayout.setOnClickListener(this);
            header = (TextView) itemView.findViewById(R.id.tv_header);
            subHeader = (TextView) itemView.findViewById(R.id.tv_subheader);
            favButton = (ImageButton) itemView.findViewById(R.id.ib_favorite);
            favButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_favorite:
                    if (mListener != null) {
                        if (mIsRepo) {
                            mListener.onFavoriteClicked(repository);
                            favButton.setSelected(repository.isFavorite());
                        } else {
                            mListener.onFavoriteClicked(user);
                            favButton.setSelected(user.isFavorite());
                        }
                    }
                    break;

                case R.id.parent_layout:
                    if (mListener != null) {
                        if (mIsRepo)
                            mListener.onItemClicked(repository);
                        else
                            mListener.onItemClicked(user);
                    }
                    break;
            }
        }
    }
}

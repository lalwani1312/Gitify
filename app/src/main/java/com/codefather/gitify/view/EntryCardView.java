package com.codefather.gitify.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codefather.gitify.R;

import java.util.List;

/**
 * This is a custom cardview which shows maximum four entries.
 * If there are more than 4 entries, "View All" button becomes visible.
 * <p>
 * Created by hitesh-lalwani on 6/3/17.
 */

public class EntryCardView extends CardView {
    private static final int MAX_NUM_ENTRIES = 4;

    private TextView mTotalCountView;
    private LinearLayout mEntriesLinearLayout;
    private TextView mViewAllButton;

    private LayoutInflater mInflater;
    private Resources mResources;

    //Listeners
    private EntryCardInteraction mListener;
    private OnClickListener mEntryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                Entry entry = (Entry) v.getTag();
                if (entry instanceof UserEntry) {
                    mListener.onUsernameClicked((UserEntry) entry);
                } else if (entry instanceof RepoEntry) {
                    mListener.onRepoClicked((RepoEntry) entry);
                }
            }
        }
    };
    private OnClickListener mViewAllClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onViewAllClicked((boolean) v.getTag());
            }
        }
    };
    private OnClickListener mFavClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                Entry entry = (Entry) v.getTag();
                mListener.onFavClicked(entry);
                v.setSelected(entry.isFavorite);
            }
        }
    };

    public EntryCardView(Context context) {
        this(context, null);
    }


    public EntryCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EntryCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setEntryCardInteraction(EntryCardInteraction listener) {
        mListener = listener;
    }

    private void init(Context context, AttributeSet attrs) {
        mInflater = LayoutInflater.from(context);
        mResources = context.getResources();
        View v = inflate(context, R.layout.entry_card_view, this);
        mTotalCountView = (TextView) v.findViewById(R.id.tv_total_count);
        mEntriesLinearLayout = (LinearLayout) v.findViewById(R.id.ll_entries);
        mViewAllButton = (TextView) v.findViewById(R.id.btn_view_all);

        String title = null;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EntryCardView,
                0, 0);

        try {
            title = a.getString(R.styleable.EntryCardView_title);
        } finally {
            a.recycle();
        }

        TextView titleView = (TextView) v.findViewById(R.id.tv_title);
        titleView.setText(title);
        setTotalCount("0");
        mViewAllButton.setOnClickListener(mViewAllClickListener);
    }

    public void setAnimateLayoutChanges(boolean animate) {
        mEntriesLinearLayout.setLayoutTransition(animate ? new LayoutTransition() : null);
    }

    public void setTotalCount(String totalCount) {
        mTotalCountView.setText(mResources.getString(R.string.total_count, totalCount));
    }

    private void updateViewAllLayoutVisibility(int entriesSize, boolean isRepo) {
        if (entriesSize > MAX_NUM_ENTRIES) {
            mViewAllButton.setVisibility(VISIBLE);
            mViewAllButton.setTag(isRepo);
        } else {
            mViewAllButton.setVisibility(GONE);
        }
    }

    public void addUserEntries(List<UserEntry> entries) {
        mEntriesLinearLayout.removeAllViews();
        int count = 0;
        for (Entry entry : entries) {
            addEntry(entry);
            ++count;
            if (count >= MAX_NUM_ENTRIES)
                break;
        }
        updateViewAllLayoutVisibility(entries.size(), false);
    }

    public void addRepoEntries(List<RepoEntry> entries) {
        mEntriesLinearLayout.removeAllViews();
        int count = 0;
        for (Entry entry : entries) {
            addEntry(entry);
            ++count;
            if (count >= MAX_NUM_ENTRIES)
                break;
        }
        updateViewAllLayoutVisibility(entries.size(), true);
    }

    public void addEntry(Entry entry) {
        View v = mInflater.inflate(R.layout.entry_item, mEntriesLinearLayout, false);
        v.setTag(entry);
        v.setOnClickListener(mEntryClickListener);
        TextView headerView = (TextView) v.findViewById(R.id.tv_header);
        TextView subHeaderview = (TextView) v.findViewById(R.id.tv_subheader);
        ImageButton favoriteButton = (ImageButton) v.findViewById(R.id.ib_favorite);
        favoriteButton.setTag(entry);
        headerView.setText(entry.name);
        favoriteButton.setSelected(entry.isFavorite);
        favoriteButton.setOnClickListener(mFavClickListener);
        if (entry instanceof UserEntry) {
            subHeaderview.setText(mResources.getString(R.string.score, String.valueOf(((UserEntry) entry).score)));
        } else if (entry instanceof RepoEntry) {
            subHeaderview.setText(mResources.getString(R.string.repo_by, String.valueOf(((RepoEntry) entry).repoOwner)));
        }
        mEntriesLinearLayout.addView(v);
    }

    public void reset() {
        setTotalCount("0");
        mViewAllButton.setVisibility(GONE);
        mEntriesLinearLayout.removeAllViews();
    }

    public interface EntryCardInteraction {
        void onUsernameClicked(UserEntry entry);

        void onRepoClicked(RepoEntry entry);

        void onViewAllClicked(boolean isRepo);

        void onFavClicked(Entry entry);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Entry {
        public String id;
        public String name;
        public boolean isFavorite;

        public Entry(String id, String name, boolean isFavorite) {
            this.id = id;
            this.name = name;
            this.isFavorite = isFavorite;
        }
    }

    @SuppressWarnings("unused")
    public static class UserEntry extends Entry {
        public String reposUrl, avatarUrl, url, score;

        public UserEntry(String id, String name, String url, String avatarUrl, String reposUrl,
                         String score, boolean isFavorite) {
            super(id, name, isFavorite);
            this.url = url;
            this.reposUrl = reposUrl;
            this.avatarUrl = avatarUrl;
            this.score = score;
        }
    }

    public static class RepoEntry extends Entry {
        String repoOwner, htmlUrl, description, fullName;

        public RepoEntry(String id, String name, String fullName, String repoOwner, String htmlUrl,
                         String description, boolean isFavorite) {
            super(id, name, isFavorite);
            this.fullName = fullName;
            this.repoOwner = repoOwner;
            this.htmlUrl = htmlUrl;
            this.description = description;
        }
    }
}

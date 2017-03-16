package com.codefather.gitify.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codefather.gitify.R;
import com.codefather.gitify.model.RepoSearch;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.Repository_Table;
import com.codefather.gitify.model.User;
import com.codefather.gitify.model.UserSearch;
import com.codefather.gitify.model.User_Table;
import com.codefather.gitify.network.RetrofitApiClient;
import com.codefather.gitify.utils.Constants;
import com.codefather.gitify.utils.ListItemClickInteraction;
import com.codefather.gitify.utils.QueryUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

public class ViewAllActivity extends AppCompatActivity {
    private static final String TAG = ViewAllActivity.class.getSimpleName();

    //Extras
    private static final String EXTRA_IS_REPO = "is_repo";
    private static final String EXTRA_QUERY = "query";
    //This tells which tye of data is being shown
    //1. Search, 2. Recent 3. Fav
    private static final String EXTRA_DATA_TYPE = "data_type";

    //Per page count.
    private static final int PER_PAGE = 25;

    //page number
    private long mPageNumber;

    //Views
    private ProgressBar mProgressBar;
    private TextView mCountView;

    //Data
    private boolean mIsRepo;
    private String mQuery;
    private int mDataType;
    private long mCount;

    //Retrofit calls
    private Call<RepoSearch> mRepoSearchCall;
    private Call<UserSearch> mUserSearchCall;

    //Activity weak reference
    private WeakReference<ViewAllActivity> mActivityWeakReference;

    //Adapter
    private ViewAllAdapter mAdapter;

    public static void start(Context context, boolean isRepo, int dataType, String query) {
        Intent intent = new Intent(context, ViewAllActivity.class);
        intent.putExtra(EXTRA_IS_REPO, isRepo);
        intent.putExtra(EXTRA_DATA_TYPE, dataType);
        intent.putExtra(EXTRA_QUERY, query);
        context.startActivity(intent);
    }

    @SuppressWarnings("unused")
    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ViewAllActivity.class);
        context.startActivity(intent, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityWeakReference = new WeakReference<>(this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        processIntent();
        mPageNumber = 1;
        mCount = 0;
        setContentView(R.layout.activity_view_all);
        configureToolbar();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mCountView = (TextView) findViewById(R.id.tv_count);
        configureSubheader();
        configureRecyclerView();
        updateCount(0);
        if (isSearchDataType()) {
            searchOnNetwork();
        } else if (mDataType == Constants.DataType.RECENT) {
            loadRecentsfromDb();
        } else if (mDataType == Constants.DataType.FAVORITE) {
            loadFavoritesfromDb();
        }
    }

    private void searchOnNetwork() {
        if (mIsRepo)
            searchRepositories();
        else
            searchUsers();
    }

    private void loadRecentsfromDb() {
        if (mIsRepo) {
            List<Repository> repositories = SQLite.select().from(Repository.class)
                    .queryList();
            addRepositoriesToList(repositories, false);
        } else {
            List<User> users = SQLite.select().from(User.class)
                    .where(User_Table.score.isNot(""))
                    .queryList();
            addUsersToList(users, false);
        }
    }

    private void loadFavoritesfromDb() {
        if (mIsRepo) {
            List<Repository> repositories = SQLite.select().from(Repository.class)
                    .where(Repository_Table.isFavorite.is(true))
                    .queryList();
            addRepositoriesToList(repositories, false);
        } else {
            List<User> users = SQLite.select().from(User.class)
                    .where(User_Table.isFavorite.is(true))
                    .and(User_Table.score.isNot(""))
                    .queryList();
            addUsersToList(users, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processIntent() {
        Intent intent = getIntent();
        mIsRepo = intent.getBooleanExtra(EXTRA_IS_REPO, true);
        mDataType = intent.getIntExtra(EXTRA_DATA_TYPE, Constants.DataType.SEARCH);
        mQuery = intent.getStringExtra(EXTRA_QUERY);
    }

    private void configureSubheader() {
        if (isSearchDataType()) {
            TextView subheaderView = (TextView) findViewById(R.id.tv_subheader);
            subheaderView.setText(getString(R.string.showing_for_query,
                    mIsRepo ? getString(R.string.repositories).toLowerCase() : getString(R.string.usernames).toLowerCase(),
                    mQuery));
            subheaderView.setVisibility(View.VISIBLE);
        }
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mIsRepo ? getString(R.string.repositories) : getString(R.string.usernames));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configureRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (isSearchDataType()) {
                    ++mPageNumber;
                    mProgressBar.setVisibility(View.VISIBLE);
                    searchOnNetwork();
                }
            }
        });
        mAdapter = new ViewAllAdapter(this, new ListItemClickInteraction(this), mIsRepo);
        recyclerView.setAdapter(mAdapter);
    }

    private void addRepositoriesToList(List<Repository> repositories, boolean checkDbForFav) {
        if (checkDbForFav) {
            for (Repository repository : repositories) {
                long count = SQLite.select(count(Repository_Table.id))
                        .from(Repository.class)
                        .where(Repository_Table.id.is(repository.getId()))
                        .and(Repository_Table.isFavorite.is(true))
                        .count();
                if (count > 0)
                    repository.setFavorite(true);
            }
        }
        mAdapter.addRepositories(repositories);
        updateCount(repositories.size());
        mProgressBar.setVisibility(View.GONE);
    }

    private void searchRepositories() {
        mRepoSearchCall = RetrofitApiClient.getApiService()
                .searchRepositories(QueryUtils.getRepoSearchQuery(mQuery), Constants.RepoAttributes.STARS,
                        Constants.SortOrder.DESC, String.valueOf(mPageNumber), PER_PAGE);
        Log.d(TAG, mRepoSearchCall.request().url().toString());
        mRepoSearchCall.enqueue(new Callback<RepoSearch>() {
            @Override
            public void onResponse(Call<RepoSearch> call, Response<RepoSearch> response) {
                RepoSearch repoSearch = response.body();
                if (repoSearch != null) {
                    List<Repository> repositories = response.body().getRepositories();
                    addRepositoriesToList(repositories, true);
                }
            }

            @Override
            public void onFailure(Call<RepoSearch> call, Throwable t) {
                Log.e(TAG, "mSearchRepoCall failure : " + t);
                if (mActivityWeakReference.get() != null)
                    Toast.makeText(mActivityWeakReference.get(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addUsersToList(List<User> users, boolean checkDbForFav) {
        if (checkDbForFav) {
            for (User user : users) {
                long count = SQLite.select(count(User_Table.id))
                        .from(User.class)
                        .where(User_Table.id.is(user.getId()))
                        .and(User_Table.isFavorite.is(true))
                        .count();
                if (count > 0)
                    user.setFavorite(true);
            }
        }
        mAdapter.addUsers(users);
        updateCount(users.size());
        mProgressBar.setVisibility(View.GONE);
    }

    private void searchUsers() {
        mUserSearchCall = RetrofitApiClient.getApiService()
                .searchUsers(QueryUtils.getUserSearchQuery(mQuery), Constants.UserAttributes.FOLLOWERS,
                        Constants.SortOrder.DESC, String.valueOf(mPageNumber), PER_PAGE);
        Log.d(TAG, mUserSearchCall.request().url().toString());
        mUserSearchCall.enqueue(new Callback<UserSearch>() {
            @Override
            public void onResponse(Call<UserSearch> call, Response<UserSearch> response) {
                UserSearch userSearch = response.body();
                if (userSearch != null) {
                    List<User> users = userSearch.getUsers();
                    addUsersToList(users, true);
                }
            }

            @Override
            public void onFailure(Call<UserSearch> call, Throwable t) {
                Log.e(TAG, "mUserSearchCall failure : " + t);
                if (mActivityWeakReference.get() != null)
                    Toast.makeText(mActivityWeakReference.get(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void updateCount(long count) {
        mCount += count;
        if (isSearchDataType()) {
            mCountView.setText(String.valueOf(mCount));
        } else {
            mCountView.setText(getString(R.string.count_num, String.valueOf(mCount)));
        }
    }

    private boolean isSearchDataType() {
        return mDataType == Constants.DataType.SEARCH;
    }

    @Override
    protected void onDestroy() {
        if (mRepoSearchCall != null)
            mRepoSearchCall.cancel();
        if (mUserSearchCall != null)
            mUserSearchCall.cancel();
        mActivityWeakReference.clear();
        super.onDestroy();
    }
}

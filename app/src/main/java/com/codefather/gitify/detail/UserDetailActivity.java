package com.codefather.gitify.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codefather.gitify.R;
import com.codefather.gitify.analytics.FirebaseUtils;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.Repository_Table;
import com.codefather.gitify.model.User;
import com.codefather.gitify.network.RetrofitApiClient;
import com.codefather.gitify.search.ViewAllAdapter;
import com.codefather.gitify.utils.ListItemClickInteraction;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

public class UserDetailActivity extends AppCompatActivity {
    private static final String TAG = UserDetailActivity.class.getSimpleName();

    private static final String EXTRA_LOGIN = "name";
    private static final String EXTRA_URL = "url";
    private static final String EXTRA_REPOS_URL = "repos_url";
    private static final String EXTRA_AVATAR_URL = "avatar_url";

    private static final String KEY_LOGIN_ID = "login";
    private static final String KEY_REPOS_URL = "repos_url";

    private String mLogin, mAvatarUrl, mUrl, mRepoUrl;

    //Views
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmailView;

    //Adapters
    private ViewAllAdapter mAdapter;

    //Retrofit calls
    private Call<List<Repository>> mRepoCall;
    private Call<User> mUserDetailCall;

    public static void start(Context context, String login, String url, String reposUrl, String avatarUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LOGIN, login);
        bundle.putString(EXTRA_URL, url);
        bundle.putString(EXTRA_REPOS_URL, reposUrl);
        bundle.putString(EXTRA_AVATAR_URL, avatarUrl);
        Intent starter = new Intent(context, UserDetailActivity.class);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        processIntent();
        initContentViews();
//        configureToolbar();
        configureRecyclerView();
        loadRepositories();
        loadUserDetails();
        sendFirebaseLog();
    }

    private void sendFirebaseLog() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LOGIN_ID, mLogin);
        bundle.putString(KEY_REPOS_URL, mRepoUrl);
        FirebaseUtils.log(TAG, bundle);
    }

    private void processIntent() {
        Bundle bundle = getIntent().getExtras();
        mLogin = bundle.getString(EXTRA_LOGIN);
        mUrl = bundle.getString(EXTRA_URL);
        mRepoUrl = bundle.getString(EXTRA_REPOS_URL);
        mAvatarUrl = bundle.getString(EXTRA_AVATAR_URL);
    }

    private void configureRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.repo_list);
        mAdapter = new ViewAllAdapter(this, new ListItemClickInteraction(this), true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initContentViews() {
        ((TextView) findViewById(R.id.tv_login)).setText(mLogin);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmailView = (TextView) findViewById(R.id.tv_email);
        ImageView avatarView = (ImageView) findViewById(R.id.iv_avatar);
        Glide.with(this).load(mAvatarUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(avatarView);
    }

    private void loadRepositories() {
        mRepoCall = RetrofitApiClient.getApiService().getUserRepositories(mLogin);
        mRepoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repositories = response.body();
                for (Repository repository : repositories) {
                    long count = SQLite.select(count(Repository_Table.id))
                            .from(Repository.class)
                            .where(Repository_Table.id.is(repository.getId()))
                            .and(Repository_Table.isFavorite.is(true))
                            .count();
                    if (count > 0)
                        repository.setFavorite(true);
                }
                mAdapter.addRepositories(repositories);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadUserDetails() {
        mUserDetailCall = RetrofitApiClient.getApiService()
                .getUser(mLogin);
        Log.d(TAG, mUserDetailCall.request().url().toString());
        mUserDetailCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                mEmailView.setText(user.getEmail());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "mUserSearchCall failure : " + t);
            }
        });
    }
}

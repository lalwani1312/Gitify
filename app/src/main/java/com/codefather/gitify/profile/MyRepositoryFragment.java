package com.codefather.gitify.profile;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codefather.gitify.R;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.Repository_Table;
import com.codefather.gitify.network.RetrofitApiClient;
import com.codefather.gitify.search.ViewAllAdapter;
import com.codefather.gitify.utils.KeyboardUtils;
import com.codefather.gitify.utils.ListItemClickInteraction;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRepositoryFragment extends Fragment {

    private static final String SHARED_PREF_NAME = "login_detail";
    private static final String PREF_LOGIN = "login";

    private RecyclerView mRecyclerView;
    private Button mChangeLoginButton;
    private TextView mLoginView;
    private EditText mEnterLoginEditText;
    private RelativeLayout mRepoContainer;
    private ProgressBar mProgressBar;
    private View mNoResultsFoundView;

    //Resources to be freed
    private Context mContext;
    private SharedPreferences mPrefs;

    //Data
    private String mLogin;

    //Adapter
    private ViewAllAdapter mAdapter;

    public MyRepositoryFragment() {
    }

    public static MyRepositoryFragment newInstance() {
        return new MyRepositoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_repository, container, false);
        initViews(v);
        initContentViews();
        return v;
    }

    private void initViews(View v) {
        mLoginView = (TextView) v.findViewById(R.id.tv_login);
        mEnterLoginEditText = (EditText) v.findViewById(R.id.et_login);
        mChangeLoginButton = (Button) v.findViewById(R.id.b_change_login);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.repo_list);
        mRepoContainer = (RelativeLayout) v.findViewById(R.id.rl_repo_container);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mNoResultsFoundView = v.findViewById(R.id.tv_no_results_found);
    }

    private void initContentViews() {
        configureVisibility();
        configureRecyclerView();
        setLoginText();
        mChangeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginEditText();
                mEnterLoginEditText.requestFocus();
                mEnterLoginEditText.setSelection(mLogin.length());
                KeyboardUtils.showSoftKeyboard(mContext, mEnterLoginEditText);
            }
        });
        mEnterLoginEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressWarnings("UnnecessaryLocalVariable")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String login = mEnterLoginEditText.getText().toString();
//                    if (!TextUtils.isEmpty(login)) {
                    mLogin = login;
                    KeyboardUtils.hideSoftKeyboard(mContext, mEnterLoginEditText);
                    saveLoginToSharedPrefs();
                    setLoginText();
                    showRepositories();
                    loadRepositories();
                    return true;
//                    }
                }
                return false;
            }
        });
    }

    private void saveLoginToSharedPrefs() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_LOGIN, mLogin);
        editor.apply();
    }

    private void configureVisibility() {
        if (TextUtils.isEmpty(mLogin)) {
            showLoginEditText();
        } else {
            showRepositories();
            loadRepositories();
        }
    }

    private void showLoginEditText() {
        mEnterLoginEditText.setVisibility(View.VISIBLE);
        mRepoContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mEnterLoginEditText.requestFocus();
        KeyboardUtils.showSoftKeyboard(mContext, mEnterLoginEditText);
    }

    private void showRepositories() {
        mEnterLoginEditText.setVisibility(View.GONE);
        mRepoContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void setLoginText() {
        mEnterLoginEditText.setText(mLogin);
        mLoginView.setText(mLogin);
    }

    private void configureRecyclerView() {
        mAdapter = new ViewAllAdapter(mContext, new ListItemClickInteraction(mContext), true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadRepositories() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
        Call<List<Repository>> repoCall = RetrofitApiClient.getApiService().getUserRepositories(mLogin);
        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repositories = response.body();
                if (repositories == null || repositories.size() == 0) {
                    mNoResultsFoundView.setVisibility(View.VISIBLE);
                } else {
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
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mNoResultsFoundView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mPrefs = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mLogin = mPrefs.getString(PREF_LOGIN, "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        mPrefs = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            KeyboardUtils.hideSoftKeyboard(mContext, mEnterLoginEditText);
        } else {
            if (TextUtils.isEmpty(mLogin)) {
                mEnterLoginEditText.requestFocus();
                KeyboardUtils.showSoftKeyboard(mContext, mEnterLoginEditText);
            }
        }
    }
}

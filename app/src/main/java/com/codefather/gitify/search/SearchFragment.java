package com.codefather.gitify.search;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codefather.gitify.R;
import com.codefather.gitify.detail.UserDetailActivity;
import com.codefather.gitify.model.RepoSearch;
import com.codefather.gitify.model.Repository;
import com.codefather.gitify.model.Repository_Table;
import com.codefather.gitify.model.User;
import com.codefather.gitify.model.UserSearch;
import com.codefather.gitify.model.User_Table;
import com.codefather.gitify.network.RetrofitApiClient;
import com.codefather.gitify.utils.Constants;
import com.codefather.gitify.utils.KeyboardUtils;
import com.codefather.gitify.utils.ListItemClickInteraction;
import com.codefather.gitify.utils.QueryUtils;
import com.codefather.gitify.view.EntryCardView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

public class SearchFragment extends Fragment implements EntryCardView.EntryCardInteraction {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final int PER_PAGE = 6;
    public EntryCardView mUserCard, mRepoCard;
    //Resources
    private Context mContext;
    private String mQuery;
    //Views
    private EditText mSearchEditText;
    private ProgressBar mProgressBar;

    //Retrofit calls
    private Call<RepoSearch> mRepoSearchCall;
    private Call<UserSearch> mUserSearchCall;

    //Fragment weak reference.
    private WeakReference<SearchFragment> mFragmentWeakReference;

    //Data
    private Hashtable<String, Repository> mRepositoriesMap;
    private Hashtable<String, User> mUsersMap;

    //List click interaction
    private ListItemClickInteraction mItemClickInteraction;

    private boolean mShouldShowSearch;
    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mProgressBar.setVisibility(View.VISIBLE);
                mRepoCard.reset();
                mUserCard.reset();
                KeyboardUtils.hideSoftKeyboard(mContext, mSearchEditText);
                performSearchQuery(v.getText().toString());
                return true;
            }
            return false;
        }
    };

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public void setShouldShowSearch(boolean show) {
        mShouldShowSearch = show;
    }

    public Context getContext() {
        return mContext;
    }

    private void startUserDetailActivity(EntryCardView.UserEntry entry) {
        UserDetailActivity.start(mContext, entry.name, entry.url, entry.reposUrl, entry.avatarUrl);
    }

    @Override
    public void onUsernameClicked(EntryCardView.UserEntry entry) {
        User user = mUsersMap.get(entry.id);
        mItemClickInteraction.onItemClicked(user);
    }

    @Override
    public void onRepoClicked(EntryCardView.RepoEntry entry) {
        Repository repository = mRepositoriesMap.get(entry.id);
        mItemClickInteraction.onItemClicked(repository);
    }

    @Override
    public void onViewAllClicked(boolean isRepo) {
        ViewAllActivity.start(mContext, isRepo, Constants.DataType.SEARCH /*dataType*/, mQuery);
    }

    @Override
    public void onFavClicked(EntryCardView.Entry entry) {
        if (entry instanceof EntryCardView.UserEntry) {
            User user = mUsersMap.get(entry.id);
            boolean success = mItemClickInteraction.onFavoriteClicked(user);
            if (success)
                entry.isFavorite = user.isFavorite();
        } else if (entry instanceof EntryCardView.RepoEntry) {
            Repository repository = mRepositoriesMap.get(entry.id);
            boolean success = mItemClickInteraction.onFavoriteClicked(repository);
            if (success)
                entry.isFavorite = repository.isFavorite();
        }
    }

    private void cancelNetworkCalls() {
        if (mRepoSearchCall != null)
            mRepoSearchCall.cancel();
        if (mUserSearchCall != null)
            mUserSearchCall.cancel();
    }

    private void onFailure(Throwable t) {
        if (t != null && t.getMessage() != null && t.getMessage().contains("Canceled")) {
            Toast.makeText(mContext, "Request for q = \"" + mQuery + "\" has been canceled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setRepoDataToRepoCard(List<Repository> repositories, boolean checkDbForFav) {
        List<EntryCardView.RepoEntry> repoEntries = new ArrayList<>(repositories.size());
        for (Repository repository : repositories) {
            mRepositoriesMap.put(repository.getId(), repository);
            if (checkDbForFav) {
                long count = SQLite.select(count(Repository_Table.id))
                        .from(Repository.class)
                        .where(Repository_Table.id.is(repository.getId()))
                        .and(Repository_Table.isFavorite.is(true))
                        .count();
                if (count > 0)
                    repository.setFavorite(true);
            }
            repoEntries.add(new EntryCardView.RepoEntry(repository.getId(),
                    repository.getName(), repository.getFullName(), repository.getOwner().getLogin(),
                    repository.getHtmlUrl(), repository.getDescription(), repository.isFavorite()));
        }
        mRepoCard.addRepoEntries(repoEntries);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setRepoCardCount(long count) {
        setRepoCardCount(String.valueOf(count));
    }

    public void setRepoCardCount(String count) {
        mRepoCard.setTotalCount(count);
    }

    private void searchRepositories() {
        mRepoSearchCall = RetrofitApiClient.getApiService()
                .searchRepositories(QueryUtils.getRepoSearchQuery(mQuery), Constants.RepoAttributes.STARS,
                        Constants.SortOrder.DESC, /*page*/"1", PER_PAGE);
        Log.d(TAG, mRepoSearchCall.request().url().toString());
        mRepoSearchCall.enqueue(new Callback<RepoSearch>() {
            @Override
            public void onResponse(Call<RepoSearch> call, Response<RepoSearch> response) {
                RepoSearch repoSearchModel = response.body();
                List<Repository> repositories = repoSearchModel.getRepositories();
                setRepoCardCount(repoSearchModel.getTotalCount());
                setRepoDataToRepoCard(repositories, true);
            }

            @Override
            public void onFailure(Call<RepoSearch> call, Throwable t) {
                Log.e(TAG, "mRepoSearchCall failure : " + t);
                if (mFragmentWeakReference != null) {
                    SearchFragment fragment = mFragmentWeakReference.get();
                    if (fragment != null)
                        fragment.onFailure(t);
                }
            }
        });
    }

    public void setUsersToUserCard(List<User> users, boolean checkDbForFav) {
        List<EntryCardView.UserEntry> usernameEntries = new ArrayList<>(users.size());
        for (User user : users) {
            mUsersMap.put(user.getId(), user);
            if (checkDbForFav) {
                long count = SQLite.select(count(User_Table.id))
                        .from(User.class)
                        .where(User_Table.id.is(user.getId()))
                        .and(User_Table.isFavorite.is(true))
                        .count();
                if (count > 0)
                    user.setFavorite(true);
            }
            usernameEntries.add(new EntryCardView.UserEntry(user.getId(), user.getLogin(),
                    user.getUrl(), user.getAvatarUrl(), user.getReposUrl(),
                    user.getScore(), user.isFavorite()));
        }
        mUserCard.addUserEntries(usernameEntries);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setUserCardCount(long count) {
        setUserCardCount(String.valueOf(count));
    }

    public void setUserCardCount(String count) {
        mUserCard.setTotalCount(count);
    }

    private void searchUsers() {
        mUserSearchCall = RetrofitApiClient.getApiService()
                .searchUsers(QueryUtils.getUserSearchQuery(mQuery), Constants.UserAttributes.FOLLOWERS,
                        Constants.SortOrder.DESC, /*page*/"1", PER_PAGE);
        Log.d(TAG, mUserSearchCall.request().url().toString());
        mUserSearchCall.enqueue(new Callback<UserSearch>() {
            @Override
            public void onResponse(Call<UserSearch> call, Response<UserSearch> response) {
                UserSearch userSearchModel = response.body();
                List<User> users = userSearchModel.getUsers();
                setUserCardCount(userSearchModel.getTotalCount());
                setUsersToUserCard(users, true);
            }

            @Override
            public void onFailure(Call<UserSearch> call, Throwable t) {
                Log.e(TAG, "mUserSearchCall failure : " + t);
                if (mFragmentWeakReference != null) {
                    SearchFragment fragment = mFragmentWeakReference.get();
                    if (fragment != null)
                        fragment.onFailure(t);
                }
            }
        });
    }

    private void performSearchQuery(String q) {
        //Cancel the previous requests.
        cancelNetworkCalls();

        clearHashMaps();

        mQuery = q;

        //Repository search.
        searchRepositories();

        //Username search
        searchUsers();
    }

    private void showSoftKeyboard() {
        mSearchEditText.requestFocus();
        KeyboardUtils.showSoftKeyboard(mContext, mSearchEditText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setShouldShowSearch(true);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mShouldShowSearch) {
            LinearLayout searchContainerLayout = (LinearLayout) view.findViewById(R.id.ll_search_container);
            searchContainerLayout.setVisibility(View.VISIBLE);
            searchContainerLayout.findViewById(R.id.ib_home).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            mSearchEditText = (EditText) searchContainerLayout.findViewById(R.id.et_search);
            mSearchEditText.setOnEditorActionListener(mEditorActionListener);
            showSoftKeyboard();
        }
        mUserCard = (EntryCardView) view.findViewById(R.id.user_card);
        mRepoCard = (EntryCardView) view.findViewById(R.id.repo_card);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mUserCard.setEntryCardInteraction(this);
        mRepoCard.setEntryCardInteraction(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            showSoftKeyboard();
        } else {

            KeyboardUtils.hideSoftKeyboard(mContext, mSearchEditText);
            reset();
        }
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mFragmentWeakReference = new WeakReference<>(this);
        mRepositoriesMap = new Hashtable<>();
        mUsersMap = new Hashtable<>();
        mItemClickInteraction = new ListItemClickInteraction(mContext);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mContext = null;
        mFragmentWeakReference.clear();
        mItemClickInteraction = null;
        super.onDetach();
    }

    public void clearHashMaps() {
        mRepositoriesMap.clear();
        mUsersMap.clear();
    }

    private void reset() {
        cancelNetworkCalls();
        clearHashMaps();
        mSearchEditText.setText("");
        mRepoCard.reset();
        mUserCard.reset();
    }
}

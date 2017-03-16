package com.codefather.gitify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codefather.gitify.analytics.FirebaseUtils;
import com.codefather.gitify.favorite.FavoriteFragment;
import com.codefather.gitify.profile.MyRepositoryFragment;
import com.codefather.gitify.recent.RecentFragment;
import com.codefather.gitify.search.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    //fragment tags
    private static final String TAG_RECENT_FRAGMENT = "recent_fragment";
    private static final String TAG_FAVORITE_FRAGMENT = "favorite_fragment";
    private static final String TAG_MY_REPO_FRAGMENT = "my_repo_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String KEY_FRAGMENT_SHOWN = "fragment_shown";

    //Positions
    private static final int POSITION_RECENT = 0;
    private static final int POSITION_FAVORITE = 1;
    private static final int POSITION_MYREPO = 2;
    private static final int POSITION_SEARCH = -1;

    //Views
    private BottomNavigationView mBottomNavigationView;

    //Selected tab, menu item, etc.
    private String mLastVisibleFragmentTag;
    private int mSelectedMenuItemId;

    //Bundle
    private Bundle mShowFragmentBundle = new Bundle(1);

    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mSelectedMenuItemId = item.getItemId();
            String showFragmentTag;
            switch (mSelectedMenuItemId) {
                case R.id.menu_recent:
                    Log.d(TAG, "show RecentFragment");
                    showFragmentTag = TAG_RECENT_FRAGMENT;
                    break;
                case R.id.menu_favorite:
                    Log.d(TAG, "show FavoriteFragment");
                    showFragmentTag = TAG_FAVORITE_FRAGMENT;
                    break;
                case R.id.menu_my_repositories:
                    Log.d(TAG, "show MyRepositoryFragment");
                    showFragmentTag = TAG_MY_REPO_FRAGMENT;
                    break;
                default:
                    return false;
            }

            showFragment(showFragmentTag);
            hideFragment(mLastVisibleFragmentTag);
            hideFragment(TAG_SEARCH_FRAGMENT);
            getSupportFragmentManager().executePendingTransactions();
            mLastVisibleFragmentTag = showFragmentTag;
            return true;
        }
    };

    private Fragment getFragment(String tag) {
        switch (tag) {
            case TAG_RECENT_FRAGMENT:
                return RecentFragment.newInstance();
            case TAG_FAVORITE_FRAGMENT:
                return FavoriteFragment.newInstance();
            case TAG_MY_REPO_FRAGMENT:
                return MyRepositoryFragment.newInstance();
            case TAG_SEARCH_FRAGMENT:
                return SearchFragment.newInstance();
        }
        return null;
    }

    private void addFragment(String tag) {
        Fragment fragment = getFragment(tag);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.fragment_container, fragment, tag)
                    .commitAllowingStateLoss();
        }
    }

    private void showFragment(@NonNull String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null && !fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .show(fragment).commitAllowingStateLoss();
            mShowFragmentBundle.putString(KEY_FRAGMENT_SHOWN, "showing " + tag);
        } else {
            addFragment(tag);
            mShowFragmentBundle.putString(KEY_FRAGMENT_SHOWN, "adding " + tag);
        }
        FirebaseUtils.log(TAG, mShowFragmentBundle);
    }

    private void hideFragment(@NonNull String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .hide(fragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(TAG_RECENT_FRAGMENT);
        mLastVisibleFragmentTag = TAG_RECENT_FRAGMENT;
        configureToolbar();
        mSelectedMenuItemId = R.id.menu_recent;
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavigationListener);
        mBottomNavigationView.getMenu().findItem(mSelectedMenuItemId).setChecked(true);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            showFragment(TAG_SEARCH_FRAGMENT);
            hideFragment(mLastVisibleFragmentTag);
            getSupportFragmentManager().executePendingTransactions();
            mBottomNavigationView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment searchFragment = getSupportFragmentManager().findFragmentByTag(TAG_SEARCH_FRAGMENT);
        if (searchFragment != null && searchFragment.isVisible()) {
            hideFragment(TAG_SEARCH_FRAGMENT);
            showFragment(mLastVisibleFragmentTag);
            getSupportFragmentManager().executePendingTransactions();
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

//    private void startSearchActivity() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true,
//                    new Pair<>(mSearchTextView, mSearchTextView.getTransitionName()));
//            ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
//                    .makeSceneTransitionAnimation(this, pairs);
//
//            // Start the activity with the participants, animating from one to the other.
//            final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
//            ViewAllActivity.start(this, transitionBundle);
//        } else {
//            ViewAllActivity.start(this);
//        }
//    }
}

package com.miz.misho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.miz.misho.Enum.Preferences;
import com.miz.misho.Utilties.DBUtil;
import com.miz.misho.Utilties.FileUtil;
import com.miz.misho.Utilties.WebUtil;

public class searchActivity extends AppCompatActivity {

    AssetManager am;
    WebUtil webUtil;
    DBUtil dbUtil;
    FileUtil fileUtil;
    SharedPreferences mSP;
    searchFragInterface searchFragInterface;
    vocabFragInterface vocabFragInterface;


    Typeface font;


    LinearLayout lo_main;
    android.support.v7.widget.Toolbar top_bar;

    NavigationView nv_main;
    DrawerLayout dr_main;


    ActionBarDrawerToggle mDrawerToggle;

    FrameLayout fm_main;
    Fragment mFragment;

    AppCompatDelegate mDelegate;


    boolean isDark;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        mSP = PreferenceManager.getDefaultSharedPreferences(this);
        isDark = mSP.getBoolean(Preferences.CB_DARKTHEME.toString(), false);
        if (isDark)
            this.setTheme(R.style.darkTheme);
        else
            this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_search);
        dr_main = findViewById(R.id.dr_main);
        top_bar = findViewById(R.id.top_bar);
        nv_main = findViewById(R.id.nv_main);
        fm_main = findViewById(R.id.main_fragment);

        if (saveInstanceState == null) {
            mFragment = new searchFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_fragment, mFragment, "sf");
            ft.commit();
            searchFragInterface = (searchFragment) mFragment;
        } else {
            mFragment = getSupportFragmentManager().getFragment(saveInstanceState, "curr");
        }


        setSupportActionBar(top_bar);


        dbUtil = new DBUtil(this);
        am = this.getAssets();
        webUtil = new WebUtil();
        fileUtil = new FileUtil(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, dr_main, R.string.freq, R.string.jlpt) {

        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        dr_main.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        nv_main.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked())
                    return false;
                Intent intent = null;
                FragmentTransaction ft;
                switch (item.getItemId()) {
                    case R.id.dr_search:
                        mFragment = new searchFragment();
                        searchFragInterface = (searchFragment) mFragment;
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_fragment, mFragment, "curr");
                        ft.commit();
                        break;
                    case R.id.dr_vocab:
                        mFragment = new vocabFragment();
                        vocabFragInterface = (vocabFragInterface) mFragment;
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_fragment, mFragment, "curr");
                        ft.commit();
                        break;
                    case R.id.dr_settings:
                        intent = new Intent(searchActivity.this, preferencesActivity.class);
                        break;
                }
                if (intent != null) {
                    onPause();
                    startActivity(intent);
                }
                return true;
            }
        });
        nv_main.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (dr_main.isDrawerOpen(GravityCompat.START)) {
            dr_main.closeDrawer(GravityCompat.START);
            mDrawerToggle.syncState();
        }
            super.onBackPressed();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && !dr_main.isDrawerOpen(GravityCompat.START)) {
            dr_main.openDrawer(GravityCompat.START);
            mDrawerToggle.syncState();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void pickSearch(View view) {
        searchFragInterface.pickSearch(view);
    }

    public void showRadKanji(View view) {
        searchFragInterface.showRadKanji(view);
    }

    public void doRadSearch(View view) {
        searchFragInterface.doRadSearch(view);
    }

    public void toInput(View view) {
        searchFragInterface.toInput(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDark != mSP.getBoolean(Preferences.CB_DARKTHEME.toString(), false))
            this.recreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // dbUtil.closeDB();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "curr", mFragment);
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }


    public void doAdd(View view) {
        vocabFragInterface.doAdd(view);
    }

    public DBUtil getDbUtil() {
        return dbUtil;
    }

    public WebUtil getWebUtil() {
        return webUtil;
    }

    public AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    public ActionBarDrawerToggle getmDrawerToggle() {
        return mDrawerToggle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
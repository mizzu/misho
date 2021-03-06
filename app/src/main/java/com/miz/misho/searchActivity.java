package com.miz.misho;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miz.misho.Enum.Permissions;
import com.miz.misho.Enum.Preferences;
import com.miz.misho.Objects.VocabList;
import com.miz.misho.Utilties.DBUtil;
import com.miz.misho.Utilties.FileUtil;
import com.miz.misho.Utilties.WebUtil;
import com.miz.misho.Utilties.XMLUtil;

import java.io.IOException;
import java.util.ArrayList;

public class searchActivity extends AppCompatActivity {

    AssetManager am;
    WebUtil webUtil;
    DBUtil dbUtil;
    FileUtil fileUtil;
    XMLUtil xmlUtil;
    SharedPreferences mSP;
    searchFragInterface searchFragInterface;
    vocabFragInterface vocabFragInterface;
    TextView toolbar_title;
    Context mContext;


    Typeface font;


    LinearLayout lo_main;
    android.support.v7.widget.Toolbar top_bar;

    NavigationView nv_main;
    DrawerLayout dr_main;


    ActionBarDrawerToggle mDrawerToggle;

    FrameLayout fm_main;
    Fragment mFragment;

    AppCompatDelegate mDelegate;

    ArrayList<VocabList> quickAdd;

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
        mContext = this;
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
            if(mFragment instanceof searchFragment)
                searchFragInterface = (searchFragment) mFragment;
            else if(mFragment instanceof vocabFragment)
                vocabFragInterface = (vocabFragment) mFragment;
        }


        setSupportActionBar(top_bar);


        dbUtil = new DBUtil(this);
        am = this.getAssets();
        webUtil = new WebUtil();
        fileUtil = new FileUtil(this);
        try {
            xmlUtil = new XMLUtil(this);
            quickAdd = xmlUtil.getQuickAddList();
        } catch (Exception e) {
            Log.d("Misho", "Error initializing settings xml");
        }
        if (quickAdd == null) {
            quickAdd = new ArrayList<>();
        }
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
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.d("Misho", "No permission for writing to external directory");
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Permissions.MISHO_WRITE_TO_EXTERNAL_STORAGE.getVal());
                        } else {
                            mFragment = new vocabFragment();
                            vocabFragInterface = (vocabFragInterface) mFragment;
                            ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.main_fragment, mFragment, "curr");
                            ft.commit();
                        }
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


    public void createToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks permissions.
     * @param requestCode see {@link Permissions}
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permissions.MISHO_WRITE_TO_EXTERNAL_STORAGE.getVal()) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        createToast("Write to External Storage permissions required for vocabulary");
                        nv_main.setCheckedItem(R.id.dr_search);
                    } else {
                        FragmentTransaction ft;
                        mFragment = new vocabFragment();
                        vocabFragInterface = (vocabFragInterface) mFragment;
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_fragment, mFragment, "curr");
                        ft.commit();
                    }

                }
            }
        }
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

    /**
     * Catches back press to handle if the navigation menu is open or to go up a directory in the
     * vocabulary fragment.
     */
    @Override
    public void onBackPressed() {
        if (dr_main.isDrawerOpen(GravityCompat.START)) {
            dr_main.closeDrawer(GravityCompat.START);
            mDrawerToggle.syncState();
            return;
        }
        if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof vocabFragment) {
            if(!((vocabFragment)mFragment).relpath.equals(fileUtil.getRootdir())) {
                ((vocabFragment)mFragment).goUpDir();
                return;
            }
        }
            super.onBackPressed();
    }

    /**
     * Catches button presses and handles them if needed.
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && !dr_main.isDrawerOpen(GravityCompat.START)) {
            dr_main.openDrawer(GravityCompat.START);
            mDrawerToggle.syncState();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Callback to handle if the search button is pressed.
     * @param view
     */
    public void pickSearch(View view) {
        searchFragInterface.pickSearch(view);
    }

    /**
     * Callback to handle if the radical search button is pressed.
     * @param view
     */
    public void showRadKanji(View view) {
        searchFragInterface.showRadKanji(view);
    }


    /**
     * Callback to handle if one of the radical buttons is pressed.
     * @param view
     */
    public void doRadSearch(View view) {
        searchFragInterface.doRadSearch(view);
    }


    /**
     * Callback to handle if one of the kanji results from the radical search is pressed.
     * @param view
     */
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

    public XMLUtil getXmlUtil() {
        return xmlUtil;
    }

    public ArrayList<VocabList> getQuickAdd() {
        return quickAdd;
    }

    public void setQuickAdd(ArrayList<VocabList> quickAdd) {
        this.quickAdd = quickAdd;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
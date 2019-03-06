package com.miz.misho;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miz.misho.Enum.Preferences;
import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.KEntry;
import com.miz.misho.Objects.VocabList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class studyActivity extends AppCompatActivity {

    SharedPreferences mSP;
    boolean isDark;
    Toolbar top_bar;
    ArrayList<Object> vocabList;

    FrameLayout study_frame;

    Fragment mFragment;

    boolean isEntry;
    boolean isDef;

    int score_pos;
    int score_neg;

    TextView list_remaining;
    TextView score_pos_item;
    TextView score_neg_item;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        mSP = PreferenceManager.getDefaultSharedPreferences(this);
        isDark = mSP.getBoolean(Preferences.CB_DARKTHEME.toString(), false);
        if (isDark)
            this.setTheme(R.style.darkTheme);
        else
            this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_study);
        top_bar = findViewById(R.id.top_study_bar);
        study_frame = findViewById(R.id.study_fragment);

        setSupportActionBar(top_bar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list_remaining = findViewById(R.id.items_left);
        score_pos_item = findViewById(R.id.score_pos);
        score_neg_item = findViewById(R.id.score_neg);

        if (saveInstanceState == null) {
            score_pos = 0;
            score_neg = 0;

            vocabList = (ArrayList<Object>) getIntent().getSerializableExtra("LIST");
            Collections.shuffle(vocabList);


            mFragment = new cardfrontFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            if(vocabList.get(0) instanceof DEntry) {
                b.putSerializable("ENTRY", (DEntry) vocabList.get(0));
                isEntry = true;
            } else {
                b.putSerializable("KENTRY", (KEntry) vocabList.get(0));
                isEntry = false;
            }
            mFragment.setArguments(b);
            ft.add(R.id.study_fragment, mFragment, "top");
            ft.commit();
        } else {
            mFragment = getSupportFragmentManager().getFragment(saveInstanceState, "DEF");
            score_pos = saveInstanceState.getInt("score_pos");
            score_neg = saveInstanceState.getInt("score_neg");
            vocabList = (ArrayList<Object>)saveInstanceState.getSerializable("list");
        }

        list_remaining.setText(Integer.toString(vocabList.size()));
        score_pos_item.setText(Integer.toString(score_pos));
        score_neg_item.setText(Integer.toString(score_neg));

    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState)
    {
        outState.putInt("score_pos", score_pos);
        outState.putInt("score_neg", score_neg);
        outState.putSerializable("list", vocabList);
        super.onSaveInstanceState(outState);
    }

    public void finishActivity() {
        this.finish();
    }


    public void getNewCard() {
        if(vocabList.isEmpty()) {
            AlertDialog.Builder bl = new AlertDialog.Builder(this);
            bl.setTitle("Score");
            double acc;
            DecimalFormat f = new DecimalFormat("#0.00%");
            if(score_neg >= score_pos)
                acc = 0;
            else
                acc = (((double)score_pos-(double)score_neg)/(double)score_pos);
            bl.setMessage("Positive: " + score_pos +"\n" +
                        "Negative: " + score_neg + "\n" +
                        "Accuracy: " + f.format(acc));
            bl.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishActivity();
                }
            });
            bl.show();
            return;
        }
        mFragment = new cardfrontFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        if(vocabList.get(0) instanceof DEntry) {
            b.putSerializable("ENTRY", (DEntry) vocabList.get(0));
            isEntry = true;
        } else {
            b.putSerializable("KENTRY", (KEntry) vocabList.get(0));
            isEntry = false;
        }
        mFragment.setArguments(b);
        if(getSupportFragmentManager().findFragmentByTag("DEF") != null)
            ft.remove(getSupportFragmentManager().findFragmentByTag("DEF"));
        ft.replace(R.id.study_fragment, mFragment, "top");
        ft.commit();
        isDef = false;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doKnow(View view){
        vocabList.remove(0);
        score_pos++;
        updateScore();
        getNewCard();
    }

    public void doNotKnow(View view) {
        vocabList.add(vocabList.get(0));
        vocabList.remove(0);
        score_neg++;
        updateScore();
        getNewCard();
    }

    public void updateScore() {
        list_remaining.setText(Integer.toString(vocabList.size()));
        score_pos_item.setText(Integer.toString(score_pos));
        score_neg_item.setText(Integer.toString(score_neg));
    }

    public void toggleDef(View view) {
        if (!isDef) {
            if (isEntry) {
                entryviewFragment evf = new entryviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("ENTRY", (DEntry) vocabList.get(0));
                tof.putSerializable("ISVOCAB", true);
                evf.setArguments(tof);
                cardfrontFragment cff;
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.study_fragment, evf, "DEF")
                        .addToBackStack(null);
                if ((cff = (cardfrontFragment) getSupportFragmentManager().findFragmentById(R.id.study_fragment)) != null) {
                    ft.hide(cff);
                }
                ft.commit();
                isDef = true;
            } else {
                kanjiviewFragment kvf = new kanjiviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("KANJI", (KEntry) vocabList.get(0));
                tof.putSerializable("ISVOCAB", true);
                kvf.setArguments(tof);
                cardfrontFragment cff;
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.study_fragment, kvf, "DEF")
                        .addToBackStack(null);
                if ((cff = (cardfrontFragment) getSupportFragmentManager().findFragmentById(R.id.study_fragment)) != null) {
                    ft.hide(cff);
                }
                ft.commit();
                isDef = true;
            }
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getSupportFragmentManager().findFragmentByTag("DEF"));
            ft.show(mFragment);
            ft.commit();
            isDef = false;
        }
    }




}

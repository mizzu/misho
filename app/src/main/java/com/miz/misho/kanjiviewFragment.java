package com.miz.misho;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miz.misho.Objects.KEntry;

import org.apache.commons.lang3.StringUtils;

public class kanjiviewFragment extends android.support.v4.app.Fragment {
    KEntry entry;
    boolean isDark;
    SharedPreferences mSP;
    searchActivity mActivity;

    boolean isVocab;

    Toolbar top_bar;
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kanji_view, container, false);
        mView = view;
        mActivity = ((searchActivity) getActivity());
        TextView kanji_display = view.findViewById(R.id.kanji_display);

        TextView jlpt_title = view.findViewById(R.id.jlpt_title);
        TextView jlpt_ph = view.findViewById(R.id.jlpt_ph);

        TextView grade_title = view.findViewById(R.id.grade_title);
        TextView grade_ph = view.findViewById(R.id.grade_ph);

        TextView stroke_title = view.findViewById(R.id.stroke_title);
        TextView stroke_ph = view.findViewById(R.id.stroke_ph);

        TextView meaning_title = view.findViewById(R.id.meaning_title);
        TextView meaning_ph = view.findViewById(R.id.meaning_ph);

        TextView kunyomi_title = view.findViewById(R.id.kunyomi_title);
        TextView kunyomi_ph = view.findViewById(R.id.kunyomi_ph);

        TextView onyomi_title = view.findViewById(R.id.onyomi_title);
        TextView onyomi_ph = view.findViewById(R.id.onyomi_ph);

        TextView nanori_title = view.findViewById(R.id.nanori_title);
        TextView nanori_ph = view.findViewById(R.id.nanori_ph);

        TextView freq_title = view.findViewById(R.id.freq_title);
        TextView freq_ph = view.findViewById(R.id.freq_ph);



        entry = (KEntry) this.getArguments().getSerializable("KANJI");
        isVocab = (Boolean) this.getArguments().getSerializable("ISVOCAB");
        mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(false);
        //mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        kanji_display.setText(entry.getKanji());
        if(entry.getJlpt() != 0)
            jlpt_ph.setText(Integer.toString(entry.getJlpt()));
        if(entry.getGrade() != 0)
            grade_ph.setText(Integer.toString(entry.getGrade()));
        stroke_ph.setText(Integer.toString(entry.getStroke_count()));
        if(entry.getFreq() != 100000)
            freq_ph.setText(Integer.toString(entry.getFreq()));

        for(String s : entry.getMeaning().toString().replaceAll("\\[|\\]", "").split(";")) {
            if(StringUtils.isEmpty(meaning_ph.getText())){
                meaning_ph.append(s.trim());
                continue;
            }
            meaning_ph.append("\n");
            meaning_ph.append(s.trim());
        }

        for(String s : entry.getKunyomi().toString().replaceAll("\\[|\\]", "").split(";")) {
            if(StringUtils.isEmpty(kunyomi_ph.getText())){
                kunyomi_ph.append(s.trim());
                continue;
            }
            kunyomi_ph.append("\n");
            kunyomi_ph.append(s.trim());
        }

        for(String s : entry.getOnyomi().toString().replaceAll("\\[|\\]", "").split(";")) {
            if(StringUtils.isEmpty(onyomi_ph.getText())){
                onyomi_ph.append(s.trim());
                continue;
            }
            onyomi_ph.append("\n");
            onyomi_ph.append(s.trim());
        }

        for(String s : entry.getNanyomi().toString().replaceAll("\\[|\\]", "").split(";")) {
            if(StringUtils.isEmpty(nanori_ph.getText())){
                nanori_ph.append(s.trim());
                continue;
            }
            nanori_ph.append("\n");
            nanori_ph.append(s.trim());
        }

        if(jlpt_ph.getText().toString().isEmpty())
        {
            jlpt_ph.setVisibility(TextView.GONE);
            jlpt_title.setVisibility(TextView.GONE);
        }

        if(grade_ph.getText().toString().isEmpty())
        {
            grade_ph.setVisibility(TextView.GONE);
            grade_title.setVisibility(TextView.GONE);
        }

        if(stroke_ph.getText().toString().isEmpty())
        {
            stroke_ph.setVisibility(TextView.GONE);
            stroke_title.setVisibility(TextView.GONE);
        }

        if(meaning_ph.getText().toString().isEmpty())
        {
            meaning_ph.setVisibility(TextView.GONE);
            meaning_title.setVisibility(TextView.GONE);
        }

        if(kunyomi_ph.getText().toString().isEmpty())
        {
            kunyomi_ph.setVisibility(TextView.GONE);
            kunyomi_title.setVisibility(TextView.GONE);
        }

        if(onyomi_ph.getText().toString().isEmpty())
        {
            onyomi_ph.setVisibility(TextView.GONE);
            onyomi_title.setVisibility(TextView.GONE);
        }

        if(nanori_ph.getText().toString().isEmpty())
        {
            nanori_ph.setVisibility(TextView.GONE);
            nanori_title.setVisibility(TextView.GONE);
        }

        if(freq_ph.getText().toString().isEmpty())
        {
            freq_ph.setVisibility(TextView.GONE);
            freq_title.setVisibility(TextView.GONE);
        }
    return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isVocab)
            inflater.inflate(R.menu.entry_view_menu, menu);
        //else
        //    inflater.inflate(R.menu.vocab_entry_view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isVocab) {
            switch (item.getItemId()) {
                case R.id.entry_add:
                    vocabFragment kvf = new vocabFragment();
                    Bundle tof = new Bundle();
                    tof.putSerializable("TOADD", entry);
                    kvf.setArguments(tof);
                    kanjiviewFragment sf;
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.main_fragment, kvf)
                            .addToBackStack(null);
                    if ((sf = (kanjiviewFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                        ft.hide(sf);
                    }
                    ft.commit();
                    break;
                case android.R.id.home:
                    mActivity.onBackPressed();
                    break;
            }
        } else {
            switch (item.getItemId()) {
                //case R.id.vocab_entry_delete:
                //    break;
                case android.R.id.home:
                    mActivity.onBackPressed();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(!isVocab)
            mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(true);
    }

}

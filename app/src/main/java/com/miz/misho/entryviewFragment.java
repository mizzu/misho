package com.miz.misho;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miz.misho.Objects.DEntry;
import com.miz.misho.Utilties.FileUtil;

import org.apache.commons.lang3.StringUtils;

public class entryviewFragment extends android.support.v4.app.Fragment {
    DEntry entry;
    boolean isDark;
    searchActivity mActivity;

    RecyclerView.Adapter rSenseAdapter;
    RecyclerView rSenseView;
    RecyclerView.LayoutManager rSenseManager;

    LinearLayout ll_keb;
    LinearLayout ll_reb;
    android.support.v7.widget.Toolbar top_bar;
    LinearLayout ev_wholder;

    boolean isVocab;
    int vocabPosition;

    View mView;
    FileUtil fileUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_view, container, false);
        mView = view;
        mActivity = ((searchActivity) getActivity());
        rSenseView = view.findViewById(R.id.sense_lv);
        rSenseManager = new LinearLayoutManager(getActivity());
        rSenseView.setLayoutManager(rSenseManager);
        rSenseAdapter = new rSenseAdapter();
        rSenseView.setAdapter(rSenseAdapter);
        TextView keb = view.findViewById(R.id.keb_main);
        TextView reb = view.findViewById(R.id.reb_main);
        TextView keb_other = view.findViewById(R.id.keb_other);
        TextView reb_other = view.findViewById(R.id.reb_other);
        ll_keb = view.findViewById(R.id.ll_keb);
        ev_wholder = view.findViewById(R.id.ev_wholder);
        ll_reb = view.findViewById(R.id.ll_reb);
        top_bar = view.findViewById(R.id.top_bar);
        fileUtil = mActivity.getFileUtil();

        mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(false);
        //mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
/*
        if(isDark){
            rSenseView.setBackgroundColor(getResources().getColor(R.color.colorBGDT));
            keb.setTextColor(getResources().getColor(R.color.colorFontDT));
            keb_other.setTextColor(getResources().getColor(R.color.colorFontDT));
            reb.setTextColor(getResources().getColor(R.color.colorFontDT));
            reb_other.setTextColor(getResources().getColor(R.color.colorFontDT));
            ll_keb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkDT));
            ll_reb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkDT));
            top_bar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDT));
            ev_wholder.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkDT));
        } else {
            rSenseView.setBackgroundColor(getResources().getColor(R.color.colorBG));
        }
        */


        entry = (DEntry) this.getArguments().getSerializable("ENTRY");
        isVocab = (Boolean) this.getArguments().getSerializable("ISVOCAB");
        keb.setText(entry.kreading.get(0));
        reb.setText(entry.reading.get(0));
        for (int i = 1; i < entry.kreading.size(); i++) {
            keb_other.append("[" + entry.kreading.get(i) + "]");
        }

        for (int i = 1; i < entry.reading.size(); i++) {
            reb_other.append("[" + entry.reading.get(i) + "]");
        }
        reb_other.setMovementMethod(new ScrollingMovementMethod());
        keb_other.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!isVocab)
            mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    class rSenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class cardViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_sense, tv_pos, tv_gloss, tv_dial, tv_field, tv_lsource,
                    tv_sinf, tv_stagk, tv_stagr, tv_ant, tv_xref, tvt_pos, tvt_gloss, tvt_dial,
                    tvt_field, tvt_lsource, tvt_sinf, tvt_stagk, tvt_ant, tvt_xref;
            public CardView card;

            public cardViewHolder(View view) {
                super(view);
                tv_sense = view.findViewById(R.id.sense_ph);
                tv_pos = view.findViewById(R.id.pos_ph);
                tv_gloss = view.findViewById(R.id.gloss_ph);
                tv_dial = view.findViewById(R.id.dial_ph);
                tv_field = view.findViewById(R.id.field_ph);
                tv_lsource = view.findViewById(R.id.lsource_ph);
                tv_sinf = view.findViewById(R.id.sinf_ph);
                tv_stagk = view.findViewById(R.id.stagk_ph);
                tv_stagr = view.findViewById(R.id.stagr_ph);
                tv_ant = view.findViewById(R.id.ant_ph);
                tv_xref = view.findViewById(R.id.xref_ph);
                tvt_pos = view.findViewById(R.id.pos_title);
                tvt_gloss = view.findViewById(R.id.gloss_title);
                tvt_dial = view.findViewById(R.id.dial_title);
                tvt_field = view.findViewById(R.id.field_title);
                tvt_lsource = view.findViewById(R.id.lsource_title);
                tvt_sinf = view.findViewById(R.id.sinf_title);
                tvt_stagk = view.findViewById(R.id.stag_title);
                tvt_ant = view.findViewById(R.id.ant_title);
                tvt_xref = view.findViewById(R.id.xref_title);
                card = view.findViewById(R.id.card_sense);
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_sense, parent, false);
            return new cardViewHolder(rad);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            cardViewHolder cvh = (cardViewHolder) holder;
            cvh.tv_sense.setText("Sense " + Integer.toString(position + 1) + ":");
            cvh.tv_pos.setText(entry.senses.get(position).getPos().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_pos.getText().toString().isEmpty()) {
                cvh.tv_pos.setVisibility(TextView.GONE);
                cvh.tvt_pos.setVisibility(TextView.GONE);
            }
            cvh.tv_gloss.setText(entry.senses.get(position).getGloss().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_gloss.getText().toString().isEmpty()) {
                cvh.tv_gloss.setVisibility(TextView.GONE);
                cvh.tvt_gloss.setVisibility(TextView.GONE);
            }
            cvh.tv_dial.setText(entry.senses.get(position).getDial().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_dial.getText().toString().isEmpty()) {
                cvh.tv_dial.setVisibility(TextView.GONE);
                cvh.tvt_dial.setVisibility(TextView.GONE);
            }
            cvh.tv_field.setText(entry.senses.get(position).getField().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_field.getText().toString().isEmpty()) {
                cvh.tv_field.setVisibility(TextView.GONE);
                cvh.tvt_field.setVisibility(TextView.GONE);
            }
            cvh.tv_lsource.setText(entry.senses.get(position).getLsource().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_lsource.getText().toString().isEmpty()) {
                cvh.tv_lsource.setVisibility(TextView.GONE);
                cvh.tvt_lsource.setVisibility(TextView.GONE);
            }
            cvh.tv_sinf.setText(entry.senses.get(position).getS_inf().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_sinf.getText().toString().isEmpty()) {
                cvh.tv_sinf.setVisibility(TextView.GONE);
                cvh.tvt_sinf.setVisibility(TextView.GONE);
            }
            cvh.tv_stagk.setText(entry.senses.get(position).getS_inf().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_stagk.getText().toString().isEmpty()) {
                cvh.tv_stagk.setVisibility(TextView.GONE);
            }
            cvh.tv_stagr.setText(entry.senses.get(position).getStagr().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_stagr.getText().toString().isEmpty()) {
                cvh.tv_stagr.setVisibility(TextView.GONE);
            }
            if (cvh.tv_stagr.getVisibility() == TextView.GONE && cvh.tv_stagk.getVisibility() == TextView.GONE) {
                cvh.tvt_stagk.setVisibility(TextView.GONE);
            }
            cvh.tv_ant.setText(entry.senses.get(position).getAnt().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_ant.getText().toString().isEmpty()) {
                cvh.tv_ant.setVisibility(TextView.GONE);
                cvh.tvt_ant.setVisibility(TextView.GONE);
            }
            cvh.tv_xref.setText(entry.senses.get(position).getXref().toString().replaceAll("\\[|\\]", ""));
            if (cvh.tv_xref.getText().toString().isEmpty()) {
                cvh.tv_xref.setVisibility(TextView.GONE);
                cvh.tvt_xref.setVisibility(TextView.GONE);
            }
            if (isDark)
                cvh.card.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkDT));
        }

        @Override
        public int getItemCount() {
            return entry.senses.size();
        }

    }

    public void createToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isVocab)
            inflater.inflate(R.menu.entry_view_menu, menu);
     //   else
     //       inflater.inflate(R.menu.vocab_entry_view_menu, menu);
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
                    entryviewFragment sf;
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.main_fragment, kvf)
                            .addToBackStack(null);
                    if ((sf = (entryviewFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
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
}

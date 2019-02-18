package com.miz.misho;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miz.misho.Enum.Permissions;
import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.KEntry;
import com.miz.misho.Objects.PosTextView;
import com.miz.misho.Objects.VocabList;
import com.miz.misho.Utilties.FileUtil;

import java.util.ArrayList;


public class vocabViewFragment extends Fragment {


    RecyclerView rVocabListView;
    RecyclerView.LayoutManager rVocabListManager;
    RecyclerView.Adapter rVocabListAdapter;
    TextView toolbar_title;


    FileUtil fileUtil;
    ArrayList<Object> list;
    VocabList vlist;
    Boolean isEntry;
    String path;
    searchActivity mActivity;



    ActionMode mActionMode;
    boolean[] selected;
    ActionMode.Callback amc = new ActionMode.Callback() {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.batch_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.batch_cancel:
                    resetStates(false);
                    mode.finish();
                    return true;
                case R.id.batch_confirm:
                    resetStates(true);
                        try {
                            fileUtil.overwriteEList(vlist.getName(), list, path);
                        } catch (Exception e) {
                            createToast("Error overwriting list");
                        }
                    mode.finish();
                    return true;
                    default:
                        return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vocab, container, false);
        isEntry = true;
        mActivity = (searchActivity)getActivity();
        toolbar_title = mActivity.findViewById(R.id.toolbar_title);

        list = (ArrayList<Object>) this.getArguments().getSerializable("VLIST");
        vlist = (VocabList) this.getArguments().getSerializable("VOLIST");
        path = (String) this.getArguments().getSerializable("PATH");
        selected = new boolean[list.size()];

        rVocabListView = view.findViewById(R.id.rVocabView);

        rVocabListManager = new LinearLayoutManager(getActivity());
        rVocabListView.setLayoutManager(rVocabListManager);

        rVocabListView.addItemDecoration(new DividerItemDecoration(rVocabListView.getContext(), DividerItemDecoration.VERTICAL));
        rVocabListAdapter = new vEntryListView();
        rVocabListView.setAdapter(rVocabListAdapter);


        setHasOptionsMenu(true);
        toolbar_title.setText("List: "+vlist.getName());
        fileUtil = ((searchActivity) this.getActivity()).getFileUtil();
        mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(false);
        //mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vocabview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vocabview_delete:
                mActionMode = mActivity.getDelegate().startSupportActionMode(amc);
                break;
            case android.R.id.home:
                mActivity.onBackPressed();
                break;
        }
        return true;
    }

    public void createToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        toolbar_title.setText("");
        mActivity.getmDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    public void resetStates(boolean b) {
            for(int i = (selected.length-1); i > -1; i--) {
                if (selected[i]) {
                    selected[i] = false;
                    if (b) {
                        list.remove(i);
                    } else
                        rVocabListAdapter.notifyItemChanged(i);
                }
            }
            if(b) {
                selected = new boolean[list.size()];
                rVocabListAdapter.notifyDataSetChanged();
            }
        }


    public void addPos(LinearLayout ll, ArrayList<String> pos) {
        for (String s : pos) {
            String sw = s.replaceAll("\\[|\\]", "");
            switch (sw) {
                case "noun (common) (futsuumeishi)":
                    ll.addView(new PosTextView(getActivity(), "noun (common)", getResources().getDrawable(R.drawable.normal_common)));
                    break;
                case "Godan verb - -aru special class":
                case "Godan verb with `bu\\' ending":
                case "Godan verb with `gu\\' ending":
                case "Godan verb with `ku\\' ending":
                case "Godan verb - Iku/Yuku special class":
                case "Godan verb with `mu\\' ending":
                case "Godan verb with `nu\\' ending":
                case "Godan verb with `ru\\' ending":
                case "Godan verb with `su\\' ending":
                case "Godan verb with `tsu\\' ending":
                case "Godan verb with `u\\' ending":
                case "Godan verb with `u\\' ending (special class)":
                    ll.addView(new PosTextView(getActivity(), "Godan", getResources().getDrawable(R.drawable.goichi_verb)));
                    break;

                case "Ichidan verb":
                case "Ichidan verb - kureru special class":
                    ll.addView(new PosTextView(getActivity(), "Ichidan", getResources().getDrawable(R.drawable.goichi_verb)));
                    break;
                case "noun or participle which takes the aux. verb suru":
                case "su verb - precursor to the modern suru":
                case "suru verb - special class":
                case "suru verb - irregular":
                    ll.addView(new PosTextView(getActivity(), "Suru V.", getResources().getDrawable(R.drawable.suru_verb)));
                    break;
            }
        }
    }

    class vEntryListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class dictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public TextView tv_word, tv_read, tv_pos, tv_defs;
            public LinearLayout entry_layout, pos_resph;

            public dictViewHolder(View view) {
                super(view);
                tv_word = view.findViewById(R.id.text_word);
                tv_read = view.findViewById(R.id.text_reading);
                //tv_pos = view.findViewById(R.id.text_pos);
                tv_defs = view.findViewById(R.id.text_defs);
                entry_layout = view.findViewById(R.id.item_layout);
                pos_resph = view.findViewById(R.id.pos_resph);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionMode != null) {
                    view.setSelected(!view.isSelected());
                    selected[getAdapterPosition()] = true;
                    return;
                }
                entryviewFragment evf = new entryviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("ENTRY", ((DEntry) list.get(this.getAdapterPosition())));
                tof.putSerializable("ISVOCAB", true);
                //tof.putSerializable("POSITION", this.getAdapterPosition());
                evf.setArguments(tof);
                vocabViewFragment vvf;
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, evf)
                        .addToBackStack(null);
                if ((vvf = (vocabViewFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(vvf);
                }
                ft.commit();

            }

            @Override
            public boolean onLongClick(View view) {
                if(mActionMode == null) {
                    mActionMode = ((searchActivity)getActivity()).getDelegate().startSupportActionMode(amc);
                    view.setSelected(!view.isSelected());
                    selected[getAdapterPosition()] = true;
                    return true;
                }
                return false;
            }
        }

        class kdictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public TextView ktv, kdtv;
            public LinearLayout kanji_result;

            public kdictViewHolder(View view) {
                super(view);
                ktv = view.findViewById(R.id.text_kanji);
                kdtv = view.findViewById(R.id.text_kdefs);
                kanji_result = view.findViewById(R.id.kanji_result);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionMode != null) {
                    view.setSelected(!view.isSelected());
                    selected[getAdapterPosition()] = true;
                    return;
                }
                kanjiviewFragment kvf = new kanjiviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("KANJI", ((KEntry)list.get(this.getAdapterPosition())));
                tof.putSerializable("ISVOCAB", true);
                //tof.putSerializable("POSITION", this.getAdapterPosition());
                kvf.setArguments(tof);
                vocabViewFragment vvf;
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, kvf)
                        .addToBackStack(null);
                if ((vvf = (vocabViewFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(vvf);
                }
                ft.commit();

            }


            @Override
            public boolean onLongClick(View view) {
                if(mActionMode == null) {
                    mActionMode = ((searchActivity)getActivity()).getDelegate().startSupportActionMode(amc);
                    view.setSelected(!view.isSelected());
                    selected[getAdapterPosition()] = true;
                    return true;
                }
                return false;
            }


        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
                    return new vEntryListView.dictViewHolder(rad);
                case 0:
                    View radd = LayoutInflater.from(parent.getContext()).inflate(R.layout.kanji_result, parent, false);
                    return new vEntryListView.kdictViewHolder(radd);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    vEntryListView.dictViewHolder dvh = (vEntryListView.dictViewHolder) holder;
                    DEntry temp = (DEntry) list.get(position);
                    dvh.tv_word.setText(temp.kreading.get(0));
                    String read = "(" + temp.reading.get(0) + ")";
                    dvh.tv_read.setText(read);
                    if (dvh.pos_resph.getChildCount() != 0) {
                        dvh.pos_resph.removeAllViews();
                    }
                    addPos(dvh.pos_resph, temp.senses.get(0).getPos());
                    StringBuilder s;
                    s = new StringBuilder();
                    for (int i = 0; i < temp.senses.get(0).getGloss().size(); i++) {
                        if (i != 0)
                            s.append("; ");
                        s.append(temp.senses.get(0).getGloss(i));
                    }
                    dvh.tv_defs.setText(s.toString().replaceAll("\\[|\\]", ""));
                    dvh.entry_layout.setSelected(selected[position]);
                    break;
                case 0:
                    vEntryListView.kdictViewHolder kdvh = (vEntryListView.kdictViewHolder) holder;
                    kdvh.ktv.setText(((KEntry) list.get(position)).getKanji());
                    kdvh.kdtv.setText(((KEntry) list.get(position)).getMeaning().toString().replaceAll("\\[|\\]", ""));
                    kdvh.kanji_result.setSelected(selected[position]);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position) instanceof DEntry)
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}

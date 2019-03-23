package com.miz.misho;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.miz.misho.Enum.Preferences;
import com.miz.misho.Exceptions.InvalidRomaji;
import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.KEntry;
import com.miz.misho.Objects.PosTextView;
import com.miz.misho.Objects.Radical;
import com.miz.misho.Objects.VocabList;
import com.miz.misho.Utilties.DBUtil;
import com.miz.misho.Utilties.FileUtil;
import com.miz.misho.Utilties.RadUtil;
import com.miz.misho.Utilties.WebUtil;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;


import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Fragment that handles the main searching function.
 */
public class searchFragment extends android.support.v4.app.Fragment implements searchFragInterface {

    AssetManager am;
    WebUtil webUtil;
    DBUtil dbUtil;
    FileUtil fileUtil;
    RadUtil ru;
    searchActivity mActivity;

    ArrayList<DEntry> results;
    ArrayList<KEntry> kresult;
    ArrayList<DEntry> oldres;
    ArrayList<KEntry> oldkres;

    Spinner dds;
    Spinner kjs;
    PopupMenu dropdown_settings;
    Button settings_imagebutton;
    SharedPreferences mSP;
    TabLayout tl;
    Typeface font;

    ArrayList<Radical> kanjiList;
    ArrayList<Radical> radList;

    ASynchRadSearch asrs;
    ASynchSearchKDB askb;
    ASynchSearchDB asdb;
    ASynchSearchWeb asws;

    Romkanify romkanify;
    ToggleButton romajiCheck;

    EditText searchInput;
    ToggleButton radButton;

    LinearLayout lo_main;
    LinearLayout lo_bot;
    android.support.v7.widget.Toolbar top_bar;

    NavigationView nv_main;
    DrawerLayout dr_main;


    RecyclerView.Adapter rRadAdapter;
    RecyclerView rRadView;
    RecyclerView.LayoutManager rRadManager;

    RecyclerView.Adapter rKanAdapter;
    RecyclerView rKanView;
    RecyclerView.LayoutManager rKanManager;

    RecyclerView.Adapter rMainAdapter;
    RecyclerView rMainView;
    RecyclerView.LayoutManager rMainManager;
    searchRecyclerViewAdapter searchRecyclerViewAdapter;

    ActionBarDrawerToggle mDrawerToggle;

    View mView;
    View rad_divider;


    boolean[] radselected;

    boolean isDark;

    //Cursor dEntryC;
    //Cursor kEntryC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        mView = view;
        mActivity = ((searchActivity)getActivity());
        radButton = view.findViewById(R.id.btn_radshow);
        searchInput = view.findViewById(R.id.txt_search);


        dbUtil = mActivity.getDbUtil();
        fileUtil = mActivity.getFileUtil();
        am = mActivity.getAssets();
        webUtil = mActivity.getWebUtil();
        ru = new RadUtil();

        mSP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isDark = mSP.getBoolean(Preferences.CB_DARKTHEME.toString(), false);
        dds = view.findViewById(R.id.ddSearch);
        lo_main = view.findViewById(R.id.lo_main);
        lo_bot = view.findViewById(R.id.lo_bottom);
        rad_divider = view.findViewById(R.id.rad_divider);

        rKanView = view.findViewById(R.id.lv_kanji);
        rRadView = view.findViewById(R.id.lv_rads);
        rMainView = view.findViewById(R.id.sv_result);

        rMainManager = new LinearLayoutManager(getActivity());
        rMainView.setLayoutManager(rMainManager);

        if(savedInstanceState != null) {
            kresult = (ArrayList<KEntry>) savedInstanceState.getSerializable("klist");
            results = (ArrayList<DEntry>) savedInstanceState.getSerializable("list");
        } else {
            results = new ArrayList<>();
            kresult = new ArrayList<>();
        }

        rMainView.addItemDecoration(new DividerItemDecoration(rMainView.getContext(), DividerItemDecoration.VERTICAL));
        rMainAdapter = new searchRecyclerViewAdapter();
        rMainView.setAdapter(rMainAdapter);

        ArrayAdapter<String> dda =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        new String[]{"Contains", "Starts With", "Ends With", "Match Only"});
        dds.setAdapter(dda);
        dds.setSelection(3);

        kjs = view.findViewById(R.id.kjSearch);
        ArrayAdapter<String> kja =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        new String[]{"Kanji", "Kunyomi", "Onyomi"});
        kjs.setAdapter(kja);
        kjs.setSelection(0);


        tl = view.findViewById(R.id.tl_main);

        tl.addTab(tl.newTab().setText(R.string.tango));
        tl.addTab(tl.newTab().setText(R.string.kanji));
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                rMainAdapter.notifyDataSetChanged();
                if (tl.getSelectedTabPosition() == 0) {
                    kjs.setVisibility(View.GONE);
                    dds.setVisibility(View.VISIBLE);
                } else {
                    if(mSP.getString("search_preference", "Offline JMDict").equalsIgnoreCase("Jisho")) {
                        tl.getTabAt(0).select();
                        return;
                    }
                    dds.setVisibility(View.GONE);
                    kjs.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (isDark) {
            rKanView.setBackgroundColor(getResources().getColor(R.color.colorBGDT));
            rRadView.setBackgroundColor(getResources().getColor(R.color.colorBGDT));
        } else {
            rRadView.setBackgroundColor(getResources().getColor(R.color.colorBG));
            rKanView.setBackgroundColor(getResources().getColor(R.color.colorBG));

        }

        rKanView.setVisibility(View.GONE);
        rRadView.setVisibility(View.GONE);
        rad_divider.setVisibility(View.GONE);


        rRadManager = new GridLayoutManager(getActivity(), 9);
        ((GridLayoutManager) rRadManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (ru.getRadList().get(position).getStrokes() > 100)
                    return 9;
                else
                    return 1;
            }
        });

        romkanify = new Romkanify();
        romajiCheck = view.findViewById(R.id.romajiBox);

        if(savedInstanceState != null) {
            kanjiList = (ArrayList<Radical>) savedInstanceState.getSerializable("kanlist");
            radselected = (boolean []) savedInstanceState.getSerializable("rsel");
            romajiCheck.setChecked(savedInstanceState.getBoolean("romaji"));
            radButton.setChecked(savedInstanceState.getBoolean("rad"));
            radList = (ArrayList<Radical>) savedInstanceState.getSerializable("radlist");
            tl.getTabAt(savedInstanceState.getInt("tabpos")).select();
            if(radButton.isChecked()) {
             showRadKanji(view);
            }
        } else {
            radselected = new boolean[ru.getRadList().size()];
            kanjiList = new ArrayList<>();
            radList = new ArrayList<>();
            tl.getTabAt(0).select();
        }

        rRadView.setLayoutManager(rRadManager);
        rRadAdapter = new radListView();
        rRadView.setAdapter(rRadAdapter);

        rKanManager = new GridLayoutManager(getActivity(), 9);
        rKanView.setLayoutManager(rKanManager);
        rKanAdapter = new kanListView();
        rKanView.setAdapter(rKanAdapter);


        searchInput.setTypeface(font);

        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nsjpr.otf");

        radButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (int i = 0; i < radselected.length; i++) {
                    radselected[i] = false;
                }
                radList.clear();
                kanjiList.clear();
                rRadAdapter.notifyDataSetChanged();
                rKanAdapter.notifyDataSetChanged();
                return true;
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mSP.getBoolean(Preferences.AUTO_SEARCH.toString(), false) && !(mSP.getString("search_preference", "Offline JMDict").equalsIgnoreCase("Jisho")))
                    pickSearch(view);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return view;
    }

    /**
     * Saves data when recreate is called
     * @param outState
     */
    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        outState.putSerializable("klist", kresult);
        outState.putSerializable("list", results);
        outState.putSerializable("rsel", radselected);
        outState.putBoolean("rad", radButton.isChecked());
        outState.putBoolean("romaji", romajiCheck.isChecked());
        outState.putSerializable("kanlist", kanjiList);
        outState.putSerializable("radlist", radList);
        outState.putInt("tabpos", tl.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }

    public void createToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ASyncTask to search in the normal entry table.
     */
    public class ASynchSearchDB extends AsyncTask<Void, Void, Integer> {
        private String kanified;

        @Override
        protected Integer doInBackground(Void... v) {
            EditText edit = mView.findViewById(R.id.txt_search);
            String text = edit.getText().toString().trim();
            int gs = getSearch(text.toCharArray());
            if (romajiCheck.isChecked()) {
                try {
                    text = romkanify.kanify(text.toLowerCase());
                    kanified = text;
                } catch (InvalidRomaji e) {
                    return 1;
                }
            }
            switch (gs) {
                case 1:
                    results = dbUtil.keb_Query(text, dds.getSelectedItemPosition(), mSP.getString(Preferences.MAX_RESULTS.toString(), "100"));
                    break;
                case 2:
                    results = dbUtil.reb_Query(text, dds.getSelectedItemPosition(), mSP.getString(Preferences.MAX_RESULTS.toString(), "100"));
                    break;
                case 3:
                    results = dbUtil.gloss_Query(text, dds.getSelectedItemPosition(), mSP.getString(Preferences.MAX_RESULTS.toString(), "100"));
                    break;
            }
            return 0;
        }

        /*
                protected void onProgressUpdate(Integer... progress) {
                }
        */
        protected void onPostExecute(Integer res) {
            if (res == 1) {
                createToast("Invalid Romaji");
                return;
            }

            if (mSP.getBoolean("pop_ups_romaji", false) && romajiCheck.isChecked())
                createToast("Searched for \"" + kanified + "\"");
            rMainAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSP.getString("search_preference", "Offline JMDict").equalsIgnoreCase("Jisho"))
            tl.getTabAt(0).select();
    }

    /**
     * ASyncTask used for Jisho.org.
     */
    public class ASynchSearchWeb extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... v) {
//        InputMethodManager inputManager = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
            EditText edit = mView.findViewById(R.id.txt_search);
            String text = edit.getText().toString().trim();
            try {
                results = webUtil.search(text);
            } catch (MalformedURLException e) {
                return 1;
            } catch (IOException e) {
                return 2;
            } catch (JSONException e) {
                return 3;
            }
            return 0;
        }

        /*
                protected void onProgressUpdate(Integer... progress) {
                }
        */
        protected void onPostExecute(Integer v) {
            switch (v) {
                case 1:
                    createToast("Malformed URL");
                    return;
                case 2:
                    createToast("URL IO Error");
                    return;
                case 3:
                    createToast("Error parsing JSON result");
                    return;
            }
            rMainAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Checks for an internet connection.
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Search for the web.
     * @param view
     */
    public void webSearch(View view) {
        if (asws != null) {
            if (asws.getStatus() == AsyncTask.Status.RUNNING || asws.getStatus() == AsyncTask.Status.PENDING)
                asws.cancel(true);
        }
        if (!results.isEmpty()) {
            results.clear();
            rMainAdapter.notifyDataSetChanged();
        }
        if (!isConnected()) {
            if (mSP.getBoolean("pop_ups_jishonoc", false))
                createToast("No available connection for Jisho");
            return;
        }

        asws = new ASynchSearchWeb();
        asws.execute();
    }

    /**
     * A method that attempts to read what kind of search is required.
     * If text contains kanji/hiragana text, returns 1.
     * If text contains hiragana/katakana text, returns 2.
     * If text contains only romaji, returns 3.
     * If text contains Kanji and romaji or Hiragana/Katakana plus romaji, returns -1 (doesn't search)
     * @param text text to be searched
     * @return type of search required
     */
    public int getSearch(char[] text) {
        if (romajiCheck.isChecked())
            return 2;
        boolean contains_K = false;
        boolean contains_HK = false;
        boolean contains_R = false;
        for (char c : text) {
            if (Character.UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                contains_K = true;
            if (Character.UnicodeBlock.of(c) == UnicodeBlock.HIRAGANA || Character.UnicodeBlock.of(c) == UnicodeBlock.KATAKANA)
                contains_HK = true;
            if (c <= 255)
                contains_R = true;
        }
        if ((contains_K && contains_R) || (contains_HK && contains_R))
            return -1;
        else if (contains_K)
            return 1;
        else if (contains_HK)
            return 2;
        else if (contains_R)
            return 3;
        else return -1;
    }

    /**
     * Method that shows/hides the radical/kanji search views.
     * @param view
     */
    public void showRadKanji(View view) {
        if (rRadView.getVisibility() == View.GONE && rKanView.getVisibility() == View.GONE) {
            rRadView.setVisibility(View.VISIBLE);
            rKanView.setVisibility(View.VISIBLE);
            rad_divider.setVisibility(View.VISIBLE);

        } else {
            rRadView.setVisibility(View.GONE);
            rKanView.setVisibility(View.GONE);
            rad_divider.setVisibility(View.GONE);
        }
    }


    /**
     * Method to search in the database.
     * @param view
     */
    public void dbSearch(View view) {
        if (asdb != null) {
            if (asdb.getStatus() == AsyncTask.Status.RUNNING || asdb.getStatus() == AsyncTask.Status.PENDING)
                asdb.cancel(true);
        }
        asdb = new ASynchSearchDB();
        asdb.execute();
    }

    /**
     * ASyncTask to search in the kanji table.
     */
    public class ASynchSearchKDB extends AsyncTask<Void, Void, Integer> {
        private String kanified;

        @Override
        protected Integer doInBackground(Void... v) {
//        InputMethodManager inputManager = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
            EditText edit = mView.findViewById(R.id.txt_search);
            String text = edit.getText().toString().trim();
            switch (kjs.getSelectedItemPosition()) {
                case 0:
                    kresult = dbUtil.kanji_Query(text);
                    break;
                case 1:
                    if (romajiCheck.isChecked()) {
                        try {
                            text = romkanify.kanify(text.toLowerCase());
                            kanified = text;
                        } catch (InvalidRomaji e) {
                            return 1;
                        }
                    }
                    kresult = dbUtil.kun_Query(text);
                    break;
                case 2:
                    if (romajiCheck.isChecked()) {
                        try {
                            text = romkanify.kanify(text.toLowerCase());
                            kanified = text;
                        } catch (InvalidRomaji e) {
                            return 1;
                        }
                    }
                    kresult = dbUtil.ony_Query(romkanify.katakanify(text));
                    break;
            }
            return 0;
        }

        /*
                protected void onProgressUpdate(Integer... progress) {
                }
        */
        protected void onPostExecute(Integer v) {
            if (v == 1) {
                createToast("Invalid Romaji");
                return;
            }
            if (mSP.getBoolean("pop_ups_romaji", false) && romajiCheck.isChecked())
                createToast("Searched for \"" + kanified + "\"");
            rMainAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to search in the Kanji table
     * @param view
     */
    public void kdbSearch(View view) {
        if (askb != null) {
            if (askb.getStatus() == AsyncTask.Status.RUNNING || askb.getStatus() == AsyncTask.Status.PENDING)
                askb.cancel(true);
        }
        if (!kresult.isEmpty()) {
            kresult.clear();
            rMainAdapter.notifyDataSetChanged();
        }
        askb = new ASynchSearchKDB();
        askb.execute();
    }


    /**
     * Gets the search depending if using Jisho or Offline JMDict
     * @param view
     */
    public void pickSearch(View view) {
        if (tl.getSelectedTabPosition() == 0) {
            if (mSP.getString("search_preference", "Offline JMDict").equalsIgnoreCase("Jisho")) {
                webSearch(view);
            } else {
                dbSearch(view);
            }
        } else if (tl.getSelectedTabPosition() == 1) {
            if (mSP.getString("search_preference", "Offline JMDict").equalsIgnoreCase("Jisho")) {
            } else {
                kdbSearch(view);
            }
        }
    }

    /*
    class mainListViewC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class dictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            }

            @Override
            public void onClick(View view) {

                entryviewFragment evf = new entryviewFragment();
                Bundle tof = new Bundle();
                dEntryC.move(this.getAdapterPosition());
                tof.putSerializable("ENTRY", dbUtil.rowToEntry(dEntryC));
                evf.setArguments(tof);
                searchFragment sf;
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, evf)
                        .addToBackStack(null);
                if ((sf = (searchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(sf);
                }
                ft.commit();

            }
        }

        class kdictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView ktv, kdtv;

            public kdictViewHolder(View view) {
                super(view);
                ktv = view.findViewById(R.id.text_kanji);
                kdtv = view.findViewById(R.id.text_kdefs);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                kanjiviewFragment kvf = new kanjiviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("KANJI", kresult.get(this.getAdapterPosition()));
                kvf.setArguments(tof);
                searchFragment sf;
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, kvf)
                        .addToBackStack(null);
                if ((sf = (searchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(sf);
                }
                ft.commit();
                //startActivity(kanjiIntent);

            }

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
                    return new dictViewHolder(rad);
                case 0:
                    View radd = LayoutInflater.from(parent.getContext()).inflate(R.layout.kanji_result, parent, false);
                    return new kdictViewHolder(radd);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    dEntryC.moveToPosition(position);
                    dictViewHolder dvh = (dictViewHolder) holder;
                    dvh.tv_word.setText(dEntryC.getString(dEntryC.getColumnIndex("keb")).split(";")[0]);
                    String read = "(" + dEntryC.getString(dEntryC.getColumnIndex("reb")).split(";")[0] + ")";
                    dvh.tv_read.setText(read);
                    if (dvh.pos_resph.getChildCount() != 0) {
                        dvh.pos_resph.removeAllViews();
                    }
                    ArrayList<String> pos = new ArrayList<>();
                    pos.addAll(Arrays.asList(dEntryC.getString(dEntryC.getColumnIndex("pos")).split(Pattern.quote("|"))));
                    addPos(dvh.pos_resph, pos);
                    StringBuilder s;
                    s = new StringBuilder();
                    String[] ss = dEntryC.getString(dEntryC.getColumnIndex("gloss")).split(Pattern.quote("|"))[0].split(";");
                    for (int i = 0; i < ss.length; i++) {
                        if (i != 0)
                            s.append("; ");
                        s.append(ss[i]);
                    }
                    dvh.tv_defs.setText(s.toString().replaceAll("\\[|\\]", ""));
                    break;
                case 0:
                    kdictViewHolder kdvh = (kdictViewHolder) holder;
                    kdvh.ktv.setText(kresult.get(position).getKanji());
                    kdvh.kdtv.setText(kresult.get(position).getMeaning().toString().replaceAll("\\[|\\]", ""));
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (tl.getSelectedTabPosition() == 0)
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            if (tl.getSelectedTabPosition() == 0)
                if(dEntryC != null)
                    return dEntryC.getCount();
                else
                    return 0;
            else
                if(dEntryC != null)
                    return kEntryC.getCount();
                else
                    return 0;
        }
    }
*/

    /**
     * Recycler view for the radical list.
     */
    class radListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class radViewHolder extends RecyclerView.ViewHolder {
            public ToggleButton kanji;

            public radViewHolder(View view) {
                super(view);
                kanji = view.findViewById(R.id.btn_rad);
            }
        }

        class radViewHolderStroke extends RecyclerView.ViewHolder {
            public TextView stroke;

            public radViewHolderStroke(View view) {
                super(view);
                stroke = view.findViewById(R.id.tv_stroke);
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.radbutton, parent, false);
                    return new radViewHolder(rad);
                case 1:
                    View radd = LayoutInflater.from(parent.getContext()).inflate(R.layout.stroke_title, parent, false);
                    return new radViewHolderStroke(radd);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    radViewHolder rvh = (radViewHolder) holder;
                    Radical r = ru.getRadList().get(position);
                    String but_text = r.getRadical();
                    rvh.kanji.setTag(r);
                    rvh.kanji.setSelected(radselected[position]);
                    rvh.kanji.setChecked(radselected[position]);
                    rvh.kanji.setText(but_text);
                    rvh.kanji.setTextOff(but_text);
                    rvh.kanji.setTextOn(but_text);
                    rvh.kanji.setTypeface(font);
                    break;
                case 1:
                    radViewHolderStroke rvhs = (radViewHolderStroke) holder;
                    rvhs.stroke.setText(ru.getRadList().get(position).getRadical());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (ru.getRadList().get(position).getStrokes() > 100)
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            return ru.getRadList().size();
        }

    }

    /**
     * Recycler view for the kanji list above the radical list.
     */
    class kanListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class kanViewHolder extends RecyclerView.ViewHolder {
            public Button kanji;

            public kanViewHolder(View view) {
                super(view);
                kanji = view.findViewById(R.id.btn_kan);
            }
        }

        class kanViewHolderStroke extends RecyclerView.ViewHolder {
            public TextView stroke;

            public kanViewHolderStroke(View view) {
                super(view);
                stroke = view.findViewById(R.id.tv_stroke);
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.kanjibutton, parent, false);
                    return new kanViewHolder(rad);
                case 1:
                    View radd = LayoutInflater.from(parent.getContext()).inflate(R.layout.stroke_title, parent, false);
                    return new kanViewHolderStroke(radd);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    kanViewHolder rvh = (kanViewHolder) holder;
                    Radical r = kanjiList.get(position);
                    String but_text = r.getRadical();
                    rvh.kanji.setTag(r);
                    rvh.kanji.setText(but_text);
                    rvh.kanji.setTypeface(font);
                    break;
                case 1:
                    kanViewHolderStroke rvhs = (kanViewHolderStroke) holder;
                    rvhs.stroke.setText(kanjiList.get(position).getRadical());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (kanjiList.get(position).getStrokes() > 100)
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            return kanjiList.size();
        }

    }

    /**
     * Searched the kanji table using the radicals selected
     */
    public class ASynchRadSearch extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... v) {
//        InputMethodManager inputManager = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
            try {
                kanjiList.addAll(dbUtil.rad_Query(radList));
            } catch (Exception e) {
            }
            return 1;
        }

        /*
                protected void onProgressUpdate(Integer... progress) {
                }
        */
        protected void onPostExecute(Integer result) {

            ((GridLayoutManager) rKanManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (kanjiList.get(position).getStrokes() > 100)
                        return 9;
                    else
                        return 1;
                }
            });
            rKanAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Method called when searching for a radical. (Called when a radical button is
     * selected/deselected)
     * @param view
     */
    public void doRadSearch(View view) {
        Radical r = (Radical) view.getTag();
        radselected[ru.getRadList().indexOf(r)] = !radselected[ru.getRadList().indexOf(r)];
        if (radselected[ru.getRadList().indexOf(r)]) {
            radList.add(r);
        } else {
            radList.remove(r);
        }
        if (radList.size() >= 1) {
            if (asrs != null) {
                if (asrs.getStatus() == AsyncTask.Status.RUNNING || asrs.getStatus() == AsyncTask.Status.PENDING)
                    asrs.cancel(true);
            }
            if (!kanjiList.isEmpty()) {
                kanjiList.clear();
                rKanAdapter.notifyDataSetChanged();
            }
            asrs = new ASynchRadSearch();
            asrs.execute();
        } else if (radList.size() == 0) {
            if (asrs != null) {
                if (asrs.getStatus() == AsyncTask.Status.RUNNING || asrs.getStatus() == AsyncTask.Status.PENDING)
                    asrs.cancel(true);
            }
            kanjiList.clear();
            rKanAdapter.notifyDataSetChanged();
        }
    }

    /**
     * When a kanji is tapped in the radical search menu, enters it where the cursor is.
     * @param view
     */
    public void toInput(View view) {
        Button b = (Button) view;
        StringBuilder s = new StringBuilder();
        int i = searchInput.getSelectionStart();
        s.append(searchInput.getText().toString());
        s.insert(searchInput.getSelectionStart(), b.getText());
        searchInput.setText(s.toString());
        searchInput.setSelection(i + 1);
    }

    /**
     * ASyncTask used to add an entry to a list using QuickList function.
     */
    class addToEListASynchTask extends AsyncTask<Void, Void, Integer> {
        VocabList vl;
        Object entry;

        public addToEListASynchTask(VocabList vl, Object entry){
            this.vl = vl;
            this.entry = entry;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                fileUtil.addToEList(new File(vl.getPath() + fileUtil.getEntryend()), entry);
            } catch (Exception e) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch(result) {
                case 0:createToast("Added to " + vl.getName());
                    return;
                case 1:createToast("Error adding to list");
            }
        }
    }

    /**
     * Called when the shishkebab icon is pressed on the entry result.
     * @param view
     * @param position
     * @return
     */
    public boolean doOptions(View view, final int position){
                final ArrayList<VocabList> qvl = mActivity.getQuickAdd();
                if(qvl.isEmpty()) {
                    createToast("No QuickAdd lists available");
                    return false;
                }
                PopupMenu pm = new PopupMenu(getContext(), view);
                int i = 0;
                for(VocabList vl : qvl) {
                    pm.getMenu().add(0, i, 0, vl.getName());
                    i++;
                }
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        addToEListASynchTask addToEListASynchTask = null;
                        if(tl.getSelectedTabPosition() == 0) {
                            addToEListASynchTask = new addToEListASynchTask(qvl.get(menuItem.getItemId()), results.get(position));
                        } else if (tl.getSelectedTabPosition() == 1) {
                            addToEListASynchTask = new addToEListASynchTask(qvl.get(menuItem.getItemId()), kresult.get(position));
                        }
                        if (addToEListASynchTask != null)
                            addToEListASynchTask.execute();
                        return true;
                    }
                });
                pm.show();
                return false;
            }

    /**
     * RecyclerView adapter used for the main search.
     */
    public class searchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class dictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tv_word, tv_read, tv_pos, tv_defs;
            public LinearLayout entry_layout, pos_resph;
            ImageButton options;

            public dictViewHolder(View view) {
                super(view);
                tv_word = view.findViewById(R.id.text_word);
                tv_read = view.findViewById(R.id.text_reading);
                //tv_pos = view.findViewById(R.id.text_pos);
                tv_defs = view.findViewById(R.id.text_defs);
                entry_layout = view.findViewById(R.id.item_layout);
                pos_resph = view.findViewById(R.id.pos_resph);
                options = view.findViewById(R.id.entry_quickadd);
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doOptions(view, getAdapterPosition());
                    }
                });
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                entryviewFragment evf = new entryviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("ENTRY", results.get(this.getAdapterPosition()));
                tof.putSerializable("ISVOCAB", false);
                evf.setArguments(tof);
                searchFragment sf;
                android.support.v4.app.FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, evf)
                        .addToBackStack(null);
                if ((sf = (searchFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(sf);
                }
                ft.commit();

            }
        }

        class kdictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView ktv, kdtv;
            public ImageButton koptions;

            public kdictViewHolder(View view) {
                super(view);
                ktv = view.findViewById(R.id.text_kanji);
                kdtv = view.findViewById(R.id.text_kdefs);
                koptions = view.findViewById(R.id.kentry_more);
                koptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doOptions(view, getAdapterPosition());
                    }
                });
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                kanjiviewFragment kvf = new kanjiviewFragment();
                Bundle tof = new Bundle();
                tof.putSerializable("KANJI", kresult.get(this.getAdapterPosition()));
                tof.putSerializable("ISVOCAB", false);
                kvf.setArguments(tof);
                searchFragment sf;
                android.support.v4.app.FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, kvf)
                        .addToBackStack(null);
                if ((sf = (searchFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(sf);
                }
                ft.commit();
                //startActivity(kanjiIntent);
            }

        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
                    return new dictViewHolder(rad);
                case 0:
                    View radd = LayoutInflater.from(parent.getContext()).inflate(R.layout.kanji_result, parent, false);
                    return new kdictViewHolder(radd);
            }
            return null;
        }

        /**
         * Function used to add the colored parts of speech icons
         * @param ll
         * @param pos
         */
        public void addPos(LinearLayout ll, ArrayList<String> pos) {
            for (String s : pos) {
                String sw = s.replaceAll("\\[|\\]", "");
                switch (sw) {
                    case "noun (common) (futsuumeishi)":
                        ll.addView(new PosTextView(mActivity, "noun (common)", mActivity.getResources().getDrawable(R.drawable.normal_common)));
                        break;
                    case "Godan verb - -aru special class":
                    case "Godan verb with `bu\' ending":
                    case "Godan verb with `gu\' ending":
                    case "Godan verb with `ku\' ending":
                    case "Godan verb - Iku/Yuku special class":
                    case "Godan verb with `mu\' ending":
                    case "Godan verb with `nu\' ending":
                    case "Godan verb with `ru\' ending":
                    case "Godan verb with `su\' ending":
                    case "Godan verb with `tsu\' ending":
                    case "Godan verb with `u\' ending":
                    case "Godan verb with `u\' ending (special class)":
                        ll.addView(new PosTextView(mActivity, "Godan", mActivity.getResources().getDrawable(R.drawable.goichi_verb)));
                        break;

                    case "Ichidan verb":
                    case "Ichidan verb - kureru special class":
                        ll.addView(new PosTextView(mActivity, "Ichidan", mActivity.getResources().getDrawable(R.drawable.goichi_verb)));
                        break;
                    case "noun or participle which takes the aux. verb suru":
                    case "su verb - precursor to the modern suru":
                    case "suru verb - special class":
                    case "suru verb - irregular":
                        ll.addView(new PosTextView(mActivity, "Suru V.", mActivity.getResources().getDrawable(R.drawable.suru_verb)));
                        break;
                        /*
                        <!ENTITY adj-i "adjective (keiyoushi)">
    <!ENTITY adj-ix "adjective (keiyoushi) - yoi/ii class">
    <!ENTITY adj-na "adjectival nouns or quasi-adjectives (keiyodoshi)">
    <!ENTITY adj-no "nouns which may take the genitive case particle `no'">
    <!ENTITY adj-pn "pre-noun adjectival (rentaishi)">
    <!ENTITY adj-t "`taru' adjective">
    <!ENTITY adj-f "noun or verb acting prenominally">
    <!ENTITY adv "adverb (fukushi)">
    <!ENTITY adv-to "adverb taking the `to' particle">

    <!ENTITY n "noun (common) (futsuumeishi)">
    <!ENTITY n-adv "adverbial noun (fukushitekimeishi)">
    <!ENTITY n-suf "noun, used as a suffix">
    <!ENTITY n-pref "noun, used as a prefix">
    <!ENTITY n-t "noun (temporal) (jisoumeishi)">

    <!ENTITY hon "honorific or respectful (sonkeigo) language">
    <!ENTITY hum "humble (kenjougo) language">
                         */
                }
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    dictViewHolder dvh = (dictViewHolder) holder;
                    DEntry temp = results.get(position);
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
                    break;
                case 0:
                    kdictViewHolder kdvh = (kdictViewHolder) holder;
                    kdvh.ktv.setText(kresult.get(position).getKanji());
                    kdvh.kdtv.setText(kresult.get(position).getMeaning().toString().replaceAll("\\[|\\]", ""));
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (tl.getSelectedTabPosition() == 0)
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            if (tl.getSelectedTabPosition() == 0)
                return results.size();
            else
                return kresult.size();
        }
    }
}

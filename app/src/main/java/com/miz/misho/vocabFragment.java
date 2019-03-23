package com.miz.misho;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.miz.misho.Enum.Permissions;
import com.miz.misho.Objects.VocabList;
import com.miz.misho.Utilties.FileUtil;
import com.miz.misho.Utilties.XMLUtil;

import org.apache.commons.io.FileExistsException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class vocabFragment extends Fragment implements vocabFragInterface {


    RecyclerView rVocabView;
    RecyclerView.LayoutManager rVocabManager;
    RecyclerView.Adapter rVocabAdapter;

    XMLUtil xmlUtil;
    FileUtil fileUtil;
    ArrayList<VocabList> vl;
    ArrayList<String> selectedPaths;
    String relpath;
    Object toAdd;
    searchActivity mActivity;

    ActionMode mActionModeBatchAdd;

    //call back used when batch add is pressed
    ActionMode.Callback amcBatchAdd = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.batch_add, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.batch_cancel:
                    mode.finish();
                    return true;
                case R.id.batch_confirm:
                    try {
                        fileUtil.batchAdd(selectedPaths, toAdd);
                    } catch (Exception e) {
                        createToast("Error batch adding to list");
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionModeBatchAdd = null;
            getActivity().getSupportFragmentManager().popBackStackImmediate();

        }
    };

    ActionMode mActionModeBatchSelect;
    //call back when batch editing files
    ActionMode.Callback amcBatchSelect = new ActionMode.Callback() {
        boolean changed = false;


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.batch_select, menu);
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
                    //
                    mode.finish();
                    return true;
                case R.id.batch_delete:
                    try {
                        fileUtil.batchDelete(selectedPaths);
                        boolean verify = false;
                        for(String s : selectedPaths) {
                            for(VocabList vl : mActivity.getQuickAdd()) {
                                if(vl.getPath().equals(s))
                                    verify = true;
                            }
                        }
                        if(verify)
                            mActivity.setQuickAdd(xmlUtil.getQuickAddList());
                    } catch (Exception e) {
                        createToast("Error batch deleting list");
                    }
                    changed = true;
                    mode.finish();
                    return true;
                case R.id.batch_move:
                    try {
                        fileUtil.batchMove(selectedPaths, relpath);
                        boolean verify = false;
                        for(String s : selectedPaths) {
                            for(VocabList vl : mActivity.getQuickAdd()) {
                                if(vl.getPath().equals(s)) {
                                    verify = true;
                                    xmlUtil.updateQuickAddNamePath(vl.getName(), vl.getPath(), vl.getName(), relpath + File.separator + vl.getName() );
                                }
                            }
                        }
                        if(verify)
                            mActivity.setQuickAdd(xmlUtil.getQuickAddList());
                    } catch (Exception e) {
                        createToast("Error batch deleting list");
                    }
                    changed = true;
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionModeBatchSelect = null;
            selectedPaths.clear();
            if(changed)
                rescan();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Misho", "No permission for writing to external directory");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Permissions.MISHO_WRITE_TO_EXTERNAL_STORAGE.getVal());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vocab, container, false);
        mActivity = (searchActivity)getActivity();
        selectedPaths = new ArrayList<>();

        rVocabView = view.findViewById(R.id.rVocabView);
        rVocabManager = new LinearLayoutManager(getActivity());
        rVocabView.setLayoutManager(rVocabManager);

        rVocabView.addItemDecoration(new DividerItemDecoration(rVocabView.getContext(), DividerItemDecoration.VERTICAL));
        rVocabAdapter = new vListView();
        rVocabView.setAdapter(rVocabAdapter);

        try {
            toAdd =  this.getArguments().getSerializable("TOADD");
        } catch (NullPointerException e) {
            toAdd = null;
        }
        setHasOptionsMenu(true);
        xmlUtil = mActivity.getXmlUtil();
        fileUtil = mActivity.getFileUtil();
        fileUtil.createRootIfNotExists();
        relpath = fileUtil.getRootdir();
        vl = fileUtil.scanFiles(relpath);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(toAdd != null) {
            mActionModeBatchAdd = mActivity.getDelegate().startSupportActionMode(amcBatchAdd);
        }
        return view;
    }

    /**
     * Called when in an action mode.
     * @param path
     * @return
     */
    public boolean addRemoveSelected(String path) {
        if(selectedPaths.contains(path)) {
            selectedPaths.remove(path);
            return false;
        } else {
            selectedPaths.add(path);
            return true;
        }
    }

    /**
     * RecyclerView for the vocabulary lists.
     */
    class vListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class vListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public TextView name, size;
            public LinearLayout vl_layout;
            public ImageButton options;

            public vListViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                size = view.findViewById(R.id.text_vlsize);
                vl_layout = view.findViewById(R.id.vl_layout);
                options = view.findViewById(R.id.vl_more);

                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doListOptions(view, getAdapterPosition());
                    }
                });

                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionModeBatchSelect != null) {
                    vl_layout.setSelected(addRemoveSelected(relpath+File.separator+vl.get(getAdapterPosition()).getName()));
                    return;
                }
                if(mActionModeBatchAdd != null) {
                    vl_layout.setSelected(addRemoveSelected(relpath+File.separator+vl.get(getAdapterPosition()).getName()));
                    return;
                }
                vocabViewFragment vvf = new vocabViewFragment();
                Bundle tof = new Bundle();
                try {
                    tof.putSerializable("VLIST", fileUtil.getEntries(vl.get(this.getAdapterPosition()).getName(), relpath));
                    tof.putSerializable("VOLIST", vl.get(this.getAdapterPosition()));
                    tof.putSerializable("PATH", relpath);
                } catch (Exception e) {
                    createToast("Error retrieving vocab list");
                    return;
                }
                vvf.setArguments(tof);
                vocabFragment vf;
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_fragment, vvf)
                        .addToBackStack(null);
                if ((vf = (vocabFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)) != null) {
                    ft.hide(vf);
                }
                ft.commit();
            }

            @Override
            public boolean onLongClick(View view) {
                if(mActionModeBatchSelect == null) {
                    mActionModeBatchSelect = mActivity.getDelegate().startSupportActionMode(amcBatchSelect);
                    view.setSelected(!view.isSelected());
                    selectedPaths.add(relpath+File.separator+vl.get(getAdapterPosition()).getName());
                    return true;
                }
                return false;
            }
        }

        class vFolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public TextView name, size;
            public LinearLayout vl_layout;
            public ImageButton options;

            public vFolderHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                size = view.findViewById(R.id.text_vlsize);
                vl_layout = view.findViewById(R.id.vl_layout);
                options = view.findViewById(R.id.vl_more);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                /*
                if(mActionModeBatchSelect != null) {
                    vl_layout.setSelected(addRemoveSelected(relpath+File.separator+vl.get(getAdapterPosition()).getName()));
                    return;
                }
                */
                relpath = relpath+File.separator+vl.get(getAdapterPosition()).getName();
                rescan();
            }

            @Override
            public boolean onLongClick(View view) {
                /*
                if(mActionModeBatchSelect == null) {
                    mActionModeBatchSelect = mActivity.getDelegate().startSupportActionMode(amcBatchSelect);
                    view.setSelected(!view.isSelected());
                    selectedPaths.add(relpath+File.separator+vl.get(getAdapterPosition()).getName());
                    return true;
                }
                */
                return false;
            }
        }

        class vBackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView name, size;
            public ImageButton options;

            public vBackHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                options = view.findViewById(R.id.vl_more);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                goUpDir();
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    View rad = LayoutInflater.from(parent.getContext()).inflate(R.layout.vl_result, parent, false);
                    return new vListViewHolder(rad);
                case 1:
                    View fhrad = LayoutInflater.from(parent.getContext()).inflate(R.layout.vl_result, parent, false);
                    return new vFolderHolder(fhrad);
                case 2:
                    View bh = LayoutInflater.from(parent.getContext()).inflate(R.layout.vl_result, parent, false);
                    return new vBackHolder(bh);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {

                case 0:
                    vListViewHolder vlv = (vListViewHolder) holder;
                    vlv.name.setText(vl.get(position).getName());
                    vlv.vl_layout.setSelected(selectedPaths.contains(relpath+File.separator+vl.get(position).getName()));
                    break;
                case 1:
                    vFolderHolder vfh = (vFolderHolder) holder;
                    vfh.name.setText(vl.get(position).getName() + "(dir)");
                    vfh.options.setVisibility(View.GONE);
                    vfh.vl_layout.setSelected(selectedPaths.contains(relpath+File.separator+vl.get(position).getName()));
                    break;
                case 2:
                    vBackHolder vbh = (vBackHolder) holder;
                    vbh.options.setVisibility(View.GONE);
                    vbh.name.setText(vl.get(position).getName());
            }
            //vlv.size.setText(Long.toString(vl.get(position).getSize()));
        }

        @Override
        public int getItemCount() {
            return vl.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (vl.get(position).getSize() == -1)
                return 1;
            else if (vl.get(position).getSize() == -2)
                return 2;
            else return 0;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vocab_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Rescan to find the files in the current directory (relpath)
     */
    public void rescan() {
        vl = fileUtil.scanFiles(relpath);
        rVocabAdapter.notifyDataSetChanged();
    }

    public void createToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the add button is pressed.
     * Used to add a new folder/list to the current directory.
     * @param view
     */
    public void doAdd(View view) {
        if((ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            createToast("No permission to write to disk");
            return;
        }
        AlertDialog.Builder bl = new AlertDialog.Builder(view.getContext());
        bl.setTitle("Add Folder/List");
        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        ArrayList<Boolean> b = new ArrayList<>();
        b.add(false);
        bl.setView(input);
        bl.setSingleChoiceItems(new String[]{"Folder", "List"}, 1, null);
        bl.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //placeholder
            }
        });
        bl.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog dl = bl.create();
        dl.show();
        dl.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView lw = dl.getListView();
                try {
                    if(fileUtil.getForbiddenCharsPattern().matcher(input.getText().toString()).find()) {
                        createToast("File/Directory cannot contain the characters:\n " + fileUtil.getForbiddenChars());
                        return;
                    } else if (input.getText().toString().trim().isEmpty()) {
                        createToast("Empty name");
                        return;
                    }
                    if(lw.getCheckedItemPosition() == 0)
                        fileUtil.mkDir(input.getText().toString(), relpath);
                    else
                        fileUtil.addEList(input.getText().toString(), relpath);
                    dl.dismiss();
                } catch (FileExistsException e) {
                    createToast("Folder/List already exists");
                } catch (IOException e) {
                    createToast("Error writing to disk\n(No permission or space)");
                    dl.dismiss();
                }
                rescan();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vocab_add:
                doAdd(getView());
                break;
            case R.id.vocab_batch:
                mActionModeBatchSelect = mActivity.getDelegate().startSupportActionMode(amcBatchSelect);
                break;
        }
        return true;
    }

    /**
     * Goes up a directory.
     */
    public void goUpDir() {
        relpath = relpath.substring(0, relpath.lastIndexOf('/'));
        rescan();
    }

    /**
     * Called when the shishkebab on the vocabulary list is pressed.
     * @param view
     * @param position
     */
    public void doListOptions(View view, final int position){
        PopupMenu pm = new PopupMenu(mActivity, view);
        pm.getMenuInflater().inflate(R.menu.vocab_list_options, pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.popup_addtoquick:
                        for(VocabList ch : mActivity.getQuickAdd()) {
                            if(ch.getPath().equalsIgnoreCase(vl.get(position).getPath())) {
                                createToast("List already in quickadd");
                                return true;
                            }
                        }
                        try {
                            xmlUtil.updateQuickAddNamePath("", "", vl.get(position).getName(), relpath + File.separator + vl.get(position).getName());
                            mActivity.setQuickAdd(xmlUtil.getQuickAddList());
                        } catch (Exception e) {
                            createToast("Error updating XML file");
                        }
                        return true;
                }
                return false;
            }
        });
        pm.show();
    }


    @Override
    public void onResume() {
        rescan();
        super.onResume();
    }
}

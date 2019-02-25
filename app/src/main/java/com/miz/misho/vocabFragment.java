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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.miz.misho.Enum.Permissions;
import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.VocabList;
import com.miz.misho.Utilties.FileUtil;

import java.io.File;
import java.util.ArrayList;


public class vocabFragment extends Fragment implements vocabFragInterface {


    RecyclerView rVocabView;
    RecyclerView.LayoutManager rVocabManager;
    RecyclerView.Adapter rVocabAdapter;


    FileUtil fileUtil;
    ArrayList<VocabList> vl;
    ArrayList<String> selectedPaths;
    String relpath;
    Object toAdd;
    searchActivity mActivity;

    ActionMode mActionModeBatchAdd;
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

    ActionMode mActionModeBatchDelete;
    ActionMode.Callback amcBatchDelete = new ActionMode.Callback() {


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
                    //
                    mode.finish();
                    return true;
                case R.id.batch_confirm:
                    try {
                        fileUtil.batchDelete(selectedPaths);
                    } catch (Exception e) {
                        createToast("Error batch deleting list");
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionModeBatchDelete = null;
            selectedPaths.clear();
            rescan();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Misho", "No permission for writing to external directory");
        }
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Permissions.MISHO_WRITE_TO_EXTERNAL_STORAGE.getVal());

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

    public boolean addRemoveSelected(String path) {
        if(selectedPaths.contains(path)) {
            selectedPaths.remove(path);
            return false;
        } else {
            selectedPaths.add(path);
            return true;
        }
    }

    class vListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class vListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public TextView name, size;
            public LinearLayout vl_layout;

            public vListViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                size = view.findViewById(R.id.text_vlsize);
                vl_layout = view.findViewById(R.id.vl_layout);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionModeBatchDelete != null) {
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
                if(mActionModeBatchDelete == null) {
                    mActionModeBatchDelete = mActivity.getDelegate().startSupportActionMode(amcBatchDelete);
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

            public vFolderHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                size = view.findViewById(R.id.text_vlsize);
                vl_layout = view.findViewById(R.id.vl_layout);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionModeBatchDelete != null) {
                    vl_layout.setSelected(addRemoveSelected(relpath+File.separator+vl.get(getAdapterPosition()).getName()));
                    return;
                }
                relpath = relpath+File.separator+vl.get(getAdapterPosition()).getName();
                rescan();
            }

            @Override
            public boolean onLongClick(View view) {
                if(mActionModeBatchDelete == null) {
                    mActionModeBatchDelete = mActivity.getDelegate().startSupportActionMode(amcBatchDelete);
                    view.setSelected(!view.isSelected());
                    selectedPaths.add(relpath+File.separator+vl.get(getAdapterPosition()).getName());
                    return true;
                }
                return false;
            }
        }

        class vBackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView name, size;

            public vBackHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_vlname);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(mActionModeBatchDelete != null) {
                    return;
                }
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
                    vfh.vl_layout.setSelected(selectedPaths.contains(relpath+File.separator+vl.get(position).getName()));
                    break;
                case 2:
                    vBackHolder vbh = (vBackHolder) holder;
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

    public void rescan() {
        vl = fileUtil.scanFiles(relpath);
        rVocabAdapter.notifyDataSetChanged();
    }

    public void createToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void doAdd(View view) {
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
                        createToast("File/Directory cannot contain the characters: " + fileUtil.getForbiddenChars());
                        return;
                    } else if (input.getText().toString().trim().isEmpty()) {
                        createToast("Empty name");
                        return;
                    }
                    if(lw.getCheckedItemPosition() == 0)
                        fileUtil.mkDir(input.getText().toString(), relpath);
                    else
                        fileUtil.addEList(input.getText().toString(), relpath);
                } catch (Exception e) {
                    createToast("Adding new folder/list failed");
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
            case R.id.vocab_delete:
                mActionModeBatchDelete = mActivity.getDelegate().startSupportActionMode(amcBatchDelete);
                break;
        }
        return true;
    }

    public void goUpDir() {
        relpath = relpath.substring(0, relpath.lastIndexOf('/'));
        rescan();
    }


    @Override
    public void onResume() {
        rescan();
        super.onResume();
    }
}

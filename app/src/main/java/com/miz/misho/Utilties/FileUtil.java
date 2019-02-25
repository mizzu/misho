package com.miz.misho.Utilties;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.VocabList;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FileUtil {
Context mContext;
private String rootdir;
private final String entryend = ".deml";
private final String forbiddenChars = "|\\?*<\":>+[]/'.";
private final Pattern forbiddenCharsPattern = Pattern.compile("[\\\\|?*<\":>+\\[\\]/'.]+");


public FileUtil(Context context){
    mContext = context;
   rootdir = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+"MishoLists";
}


    public String getForbiddenChars() {
        return forbiddenChars;
    }

    public Pattern getForbiddenCharsPattern() {
        return forbiddenCharsPattern;
    }

    public String getRootdir() {
        return rootdir;
    }

    public boolean createRootIfNotExists() {
        File dir = new File(rootdir);
        if(!dir.exists()){
            return dir.mkdir();
        }
        return true;
    }


    public void addEList(String name, String dir) throws IOException {
        createRootIfNotExists();
        File toAdd = new File(dir+File.separator+name+entryend);
        if(toAdd.exists())
            throw new FileExistsException();
        ArrayList<DEntry> toFile = new ArrayList<>();
        ObjectOutputStream toFileOOS = new ObjectOutputStream(new FileOutputStream(toAdd));
        toFileOOS.writeObject(toFile);
        toFileOOS.flush();
        toFileOOS.close();
    }

    public void overwriteEList(String name, ArrayList<Object> list, String dir) throws IOException {
        createRootIfNotExists();
        File toAdd = new File(dir+File.separator+name+entryend);
        ArrayList<Object> toFile = list;
        ObjectOutputStream toFileOOS = new ObjectOutputStream(new FileOutputStream(toAdd));
        toFileOOS.writeObject(toFile);
        toFileOOS.flush();
        toFileOOS.close();
    }

    public boolean mkDir(String name, String dir) throws FileExistsException {
    File toDir = new File(dir+File.separator+name);
    if(toDir.exists() && toDir.isDirectory())
        throw new FileExistsException();
    return toDir.mkdir();
    }

    public boolean deleteEList(String name, String dir) throws FileNotFoundException {
    File toDelete = new File(dir+File.separator+name+entryend);
    if(!toDelete.exists())
        throw new FileNotFoundException();
    return toDelete.delete();
    }

    public boolean addToEList(File f, Object e) throws IOException, ClassNotFoundException {
    ObjectInputStream toAppendOIS = new ObjectInputStream(new FileInputStream(f));
    ArrayList<Object> curr = (ArrayList<Object>) toAppendOIS.readObject();
    curr.add(e);
    toAppendOIS.close();
    ObjectOutputStream toAppendOOS = new ObjectOutputStream(new FileOutputStream(f, false));
    toAppendOOS.writeObject(curr);
    toAppendOOS.flush();
    toAppendOOS.close();
    return true;
}

    public boolean removeFromEList(String dir, int position) throws IOException, ClassNotFoundException {
        File f = new File(dir);
        ObjectInputStream toDelete = new ObjectInputStream(new FileInputStream(f));
        ArrayList<Object> curr = (ArrayList<Object>) toDelete.readObject();
        toDelete.close();
        curr.remove(position);
        ObjectOutputStream toAppendOOS = new ObjectOutputStream(new FileOutputStream(f, false));
        toAppendOOS.writeObject(curr);
        toAppendOOS.flush();
        toAppendOOS.close();
        return true;
    }

    public void batchDelete(ArrayList<String> paths) throws IOException {
    for(String s : paths) {
        File f = new File(s);
        if(f.exists() && f.isDirectory())
            FileUtils.deleteDirectory(f);
        else if (!f.exists()) {
            Log.d("Misho", s + " path may not be directory");
            f = new File(s + entryend);
            if(!f.exists()) {
                Log.d("Misho", s + " path is not directory or file");
                continue;
            } else
                f.delete();
        }
    }
}

    public void batchAdd(ArrayList<String> paths, Object entry) throws IOException, ClassNotFoundException {
        for(String s : paths) {
            File f = new File(s+entryend);
            if(!f.exists())
                continue;
            addToEList(f, entry);
        }
    }

    public ArrayList<VocabList> scanFiles(String dir) {
    File root = new File(dir);
    ArrayList<VocabList> vl = new ArrayList<>();
        for(File f : root.listFiles()) {
            if(f.getName().endsWith(entryend)) {
             vl.add(new VocabList(f.getName().substring(0, f.getName().length()-5), 0));
            } else if(f.isDirectory()) {
                vl.add(new VocabList(f.getName(), -1));
            }
        }
        if(!dir.equals(rootdir))
            vl.add(0, new VocabList("..", -2));
        return vl;
    }

    public String[] scanFileNames() {
        File root = new File(rootdir);
        ArrayList<String> vl = new ArrayList<>();
        for(File f : root.listFiles()) {
            if(f.getName().endsWith(entryend)) {
                vl.add(f.getName().substring(0, f.getName().length()-5));
            }
        }
        return vl.toArray(new String[]{});
    }

    public ArrayList<DEntry> getEntries(String name, String dir) throws IOException,ClassNotFoundException {
        File toGet = new File(dir+File.separator+name+entryend);
        ObjectInputStream toGetOIS = new ObjectInputStream(new FileInputStream(toGet));
        ArrayList<DEntry> curr = (ArrayList<DEntry>) toGetOIS.readObject();
        toGetOIS.close();
        return curr;
    }
}

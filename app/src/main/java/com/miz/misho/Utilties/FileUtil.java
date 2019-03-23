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
//File ending to distinguish it from other files
private final String entryend = ".deml";

//Forbidden characters in the android system
private final String forbiddenChars = "|\\?*<\":>+[]/'.";

//Regular expression that searched for the above characters
private final Pattern forbiddenCharsPattern = Pattern.compile("[\\\\|?*<\":>+\\[\\]/'.]+");

//name of root directory in public storage
private final String rootdirname = "MishoLists";

    public String getEntryend() {
        return entryend;
    }

    public FileUtil(Context context){
    mContext = context;
   rootdir = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+rootdirname;
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



    /**
     * Adds a new file with given name in the given directory.
     * @param name name of list
     * @param dir directory of list
     * @throws IOException
     */
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

    /**
     * Overwrites an entry list. (Used when changed are made after deleting)
     * @param name name of list
     * @param list new list of entries
     * @param dir directory of list
     * @throws IOException
     */
    public void overwriteEList(String name, ArrayList<Object> list, String dir) throws IOException {
        createRootIfNotExists();
        File toAdd = new File(dir+File.separator+name+entryend);
        ArrayList<Object> toFile = list;
        ObjectOutputStream toFileOOS = new ObjectOutputStream(new FileOutputStream(toAdd));
        toFileOOS.writeObject(toFile);
        toFileOOS.flush();
        toFileOOS.close();
    }

    /**
     * Creates a directory with the given name in the given directory
     * @param name
     * @param dir
     * @return true/false if it has been successfully created
     * @throws FileExistsException
     */
    public boolean mkDir(String name, String dir) throws FileExistsException {
    File toDir = new File(dir+File.separator+name);
    if(toDir.exists() && toDir.isDirectory())
        throw new FileExistsException();
    return toDir.mkdir();
    }

    //batch delete is used in favor of deleteEList
    public boolean deleteEList(String name, String dir) throws FileNotFoundException {
    File toDelete = new File(dir+File.separator+name+entryend);
    if(!toDelete.exists())
        throw new FileNotFoundException();
    return toDelete.delete();
    }

    /**
     * Adds an entry (DEntry or KEntry object e) to a list (File f)
     * @param f File to add it to
     * @param e DEntry or KEntry object
     * @return true or false if it has completed the operation successfully
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Using the given paths, attempts to delete each file
     * @param paths ArrayList of paths of files to be deleted.
     * @throws IOException
     */
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

    /**
     * Batch moves the files at the given 'paths' to the 'destination' directory.
     * @param paths paths of files to be moved
     * @param destination destination directory
     * @throws IOException
     */
    public void batchMove(ArrayList<String> paths, String destination) throws IOException{
    for(String s : paths) {
        File f = new File(s+entryend);
        if(f.exists() && !f.isDirectory()) {
            FileUtils.moveFile(f, new File(destination + s.substring(s.lastIndexOf(File.separator, s.length()-1))+entryend));
        }
    }
}

    /**
     * Batch adds entry to files at the given paths.
     * @param paths paths of lists to add the entry to
     * @param entry DEntry or KEntry object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void batchAdd(ArrayList<String> paths, Object entry) throws IOException, ClassNotFoundException {
        for(String s : paths) {
            File f = new File(s+entryend);
            if(!f.exists())
                continue;
            addToEList(f, entry);
        }
    }

    /**
     * Gets the names of the files ending with the 'end of filename' ending.
     * @param dir directory to search in
     * @return list of files in the directory
     */
    public ArrayList<VocabList> scanFiles(String dir) {
    File root = new File(dir);
    ArrayList<VocabList> vl = new ArrayList<>();
        for(File f : root.listFiles()) {
            if(f.getName().endsWith(entryend)) {
             vl.add(new VocabList(f.getName().substring(0, f.getName().length()-entryend.length()), f.getPath().substring(0, f.getPath().length()-entryend.length()), 0));
            } else if(f.isDirectory()) {
                vl.add(new VocabList(f.getName(), f.getPath().substring(0, f.getPath().length()-entryend.length()), -1));
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

    /**
     * Gets the entries from the filename 'name' and directory 'dir'
     * @param name Name of file
     * @param dir Directory of file
     * @return ArrayList of the entries in the file.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ArrayList<DEntry> getEntries(String name, String dir) throws IOException,ClassNotFoundException {
        File toGet = new File(dir+File.separator+name+entryend);
        ObjectInputStream toGetOIS = new ObjectInputStream(new FileInputStream(toGet));
        ArrayList<DEntry> curr = (ArrayList<DEntry>) toGetOIS.readObject();
        toGetOIS.close();
        return curr;
    }
}

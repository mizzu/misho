package com.miz.misho.Objects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miz.misho.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JMDataBaseHelper extends SQLiteOpenHelper {
    private static JMDataBaseHelper sInstance;
    private Context mc;
    private String DB_PATH;
    public SQLiteDatabase mdb;
    private static final String DB_NAME = "kjmde.db";
    private static final int DB_VERSION = 2;

    public JMDataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mc = context;
        DB_PATH = mc.getFilesDir().getPath() + File.separator + BuildConfig.APPLICATION_ID + "/databases/";
        if (checkDB()) {
            this.getWritableDatabase();
            try {
                openDB();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            createDB();
        }
    }

    public static synchronized JMDataBaseHelper getInstance(Context context){
        if(sInstance == null) {
            sInstance = new JMDataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public void createDB() {
        this.getReadableDatabase();
        try {
            copyDB();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkDB() {
        File db = null;
        try {
            String path = DB_PATH + DB_NAME;
            db = new File(path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return db.exists();
    }

    private void copyDB() throws IOException {
        InputStream dbi = mc.getAssets().open(DB_NAME);
        String out = DB_PATH + DB_NAME;
        File f = new File(DB_PATH);
        if(!f.exists())
            f.mkdirs();
        OutputStream outs = new FileOutputStream(out);
        byte[] buff = new byte[1024];
        int length;
        while ((length = dbi.read(buff)) > 0) {
            outs.write(buff, 0, length);
        }


        outs.flush();
        outs.close();
        dbi.close();
    }

    public void openDB() {
        mdb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void close() {
        if (mdb != null) {
            mdb.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            File odb = new File(DB_PATH + DB_NAME);
            try {
                odb.delete();
                copyDB();
            } catch (IOException e) {
                System.out.println("oh no");
            }
        }
    }
}
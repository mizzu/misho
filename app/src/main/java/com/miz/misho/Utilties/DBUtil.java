package com.miz.misho.Utilties;

import android.content.Context;
import android.database.Cursor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.ESense;
import com.miz.misho.Objects.JMDataBaseHelper;
import com.miz.misho.Objects.KEntry;
import com.miz.misho.Objects.Radical;

public class DBUtil {

    private JMDataBaseHelper DBH;


    public DBUtil(Context context) {
            DBH = JMDataBaseHelper.getInstance(context);
    }

    // 0 = contains
    // 1 = starts with
    // 2 = ends with
    // 3 = match only

    private String reb_SQL =
            "SELECT * FROM dictionary WHERE reb LIKE ? " +
                    "OR reb LIKE ? " +
                    "OR reb LIKE ? " +
                    "OR reb LIKE ?"+
                    "ORDER BY nf ASC LIMIT ?";

    private String keb_SQL =
            "SELECT * FROM dictionary WHERE keb LIKE ? " +
                    "OR keb LIKE ? " +
                    "OR keb LIKE ? " +
                    "OR keb LIKE ?"+
                    "ORDER BY nf ASC LIMIT ?";

    private String gloss_SQL =
            "SELECT * FROM dictionary WHERE gloss LIKE ? " +
                    "OR gloss LIKE ? " +
                    "OR gloss LIKE ? " +
                    "OR gloss LIKE ?" +
                    "ORDER BY nf ASC LIMIT ?";

    private String kanji_SQL =
            "SELECT * FROM kdictionary WHERE kanji = ?";

    private String kun_SQL =
            "SELECT * FROM kdictionary WHERE kunyomi LIKE ?";

    private String ony_SQL =
            "SELECT * FROM kdictionary WHERE onyomi LIKE ?";

    private String krad_SQL =
            "SELECT kanji,stroke_count,radicals FROM kdictionary WHERE radicals LIKE ? ";

    private String krad_SQLF = "ORDER BY stroke_count ASC";

    private String krad_extra = "AND radicals LIKE ? ";

    //add queriies for...
    //kanji
    //onyomi
    //kunyomi
    //nanori


    private String[] params = {"%x%", "x%", "%x", "x"};


    public ArrayList<DEntry> gloss_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(gloss_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%", "[" + params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) + "]", "[" + params[pos].replace("x", text) + "]", limit});
        return rowsToEntries(c);
    }

    public ArrayList<DEntry> keb_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(keb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text)  , params[pos].replace("x", text), limit});
        return rowsToEntries(c);
    }

    public ArrayList<DEntry> reb_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(reb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) ,  params[pos].replace("x", text), limit});
        return rowsToEntries(c);
    }

    public Cursor gloss_QueryCursor(String text, int pos, String limit) {
        return DBH.mdb.rawQuery(gloss_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%", "[" + params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) + "]", "[" + params[pos].replace("x", text) + "]", limit});
    }

    public Cursor keb_QueryCursor(String text, int pos, String limit) {
        return  DBH.mdb.rawQuery(keb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text)  , params[pos].replace("x", text), limit});
    }

    public Cursor reb_QueryCursor(String text, int pos, String limit) {
        return  DBH.mdb.rawQuery(reb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) ,  params[pos].replace("x", text), limit});
    }

    public ArrayList<KEntry> kanji_Query(String kanji) {
        Cursor c = DBH.mdb.rawQuery(kanji_SQL, new String[] {kanji});
        return  rowsToKEntries(c);
    }

    public ArrayList<KEntry> kun_Query(String kun) {
        Cursor c = DBH.mdb.rawQuery(kun_SQL, new String[] {"%"+kun+"%"});
        return  rowsToKEntries(c);
    }

    public ArrayList<KEntry> ony_Query(String ony) {
        Cursor c = DBH.mdb.rawQuery(ony_SQL, new String[] {"%"+ony+"%"});
        return  rowsToKEntries(c);
    }

    public ArrayList<Radical> rad_Query(ArrayList<Radical> rad) {
        Cursor c;
        if(rad.size() == 1) {
            c = DBH.mdb.rawQuery(krad_SQL+krad_SQLF, new String[]{"%" + rad.get(0).getRadical() + "%"});
        } else{
            ArrayList<String> to_sql = new ArrayList<>();
            StringBuilder s = new StringBuilder();
            s.append(krad_SQL);
            int i = 1;
            for(Radical r : rad) {
                if(i != 1)
                    s.append(krad_extra);
                to_sql.add("%"+r.getRadical()+"%");
                i++;
            }
            s.append(krad_SQLF);
            String[] sf = new String[to_sql.size()];
            c = DBH.mdb.rawQuery(s.toString(), to_sql.toArray(sf));
        }
        return rowsToKStrings(c);
    }

    ArrayList<Radical> rowsToKStrings(Cursor c) {
        boolean isFirst = true;
        int curr_stroke = 0;
        ArrayList<Radical> rb = new ArrayList<>();
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                Radical r = new Radical(c.getString(c.getColumnIndex("kanji")), c.getInt(c.getColumnIndex("stroke_count")));
                if(isFirst) {
                    curr_stroke = r.getStrokes();
                    isFirst = false;
                    rb.add(new Radical(Integer.toString(curr_stroke), curr_stroke+100));
                }
                if(r.getStrokes() != curr_stroke) {
                    curr_stroke = r.getStrokes();
                    rb.add(new Radical(Integer.toString(curr_stroke), curr_stroke+100));
                }
                rb.add(r);
                c.moveToNext();
            }
        }
        return rb;
    }

    ArrayList<KEntry> rowsToKEntries(Cursor c) {
        ArrayList<KEntry> kr = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                KEntry tmp = new KEntry();
                ArrayList<String> tmpmean = new ArrayList<>();

                ArrayList<String> tmpkunyomi = new ArrayList<>();

                ArrayList<String> tmponyomi = new ArrayList<>();

                ArrayList<String> tmpnanori = new ArrayList<>();

                tmp.setKanji(c.getString(c.getColumnIndex("kanji")));
                tmp.setJlpt(c.getInt(c.getColumnIndex("jlpt")));
                tmp.setFreq(c.getInt(c.getColumnIndex("freq")));
                tmp.setGrade(c.getInt(c.getColumnIndex("grade")));
                tmp.setStroke_count(c.getInt(c.getColumnIndex("stroke_count")));

                tmpkunyomi.addAll(Arrays.asList(c.getString(c.getColumnIndex("kunyomi")).split(";")));
                tmponyomi.addAll(Arrays.asList(c.getString(c.getColumnIndex("onyomi")).split(";")));
                tmpnanori.addAll(Arrays.asList(c.getString(c.getColumnIndex("nanori")).split(";")));
                tmpmean.addAll(Arrays.asList(c.getString(c.getColumnIndex("meanings")).split(";")));

                tmp.setKunyomi(tmpkunyomi);
                tmp.setOnyomi(tmponyomi);
                tmp.setNanori(tmpnanori);
                tmp.setMeaning(tmpmean);

                kr.add(tmp);
                c.moveToNext();
            }
        }
        return kr;
    }

    ArrayList<DEntry> rowsToEntries(Cursor cursor) {
        ArrayList<DEntry> rsEnts = new ArrayList<>();
        DEntry temp = new DEntry();
        ArrayList<ESense> etemp = new ArrayList<>();
        // TimingLogger timings = new TimingLogger("TAG", "rte");
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ArrayList<String> gloss = new ArrayList<>();
                ArrayList<String> pos = new ArrayList<>();
                ArrayList<String> misc = new ArrayList<>();
                ArrayList<String> xref = new ArrayList<>();
                ArrayList<String> s_inf = new ArrayList<>();
                ArrayList<String> ant = new ArrayList<>();
                ArrayList<String> lsource = new ArrayList<>();
                ArrayList<String> field = new ArrayList<>();
                ArrayList<String> dial = new ArrayList<>();
                ArrayList<String> stagr = new ArrayList<>();
                ArrayList<String> stagk = new ArrayList<>();
                //System.out.println(cursor.getInt(cursor.getColumnIndex("nf")));
                gloss.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("gloss")).split(Pattern.quote("|"))));
                pos.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("pos")).split(Pattern.quote("|"))));
                misc.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("misc")).split(Pattern.quote("|"))));
                xref.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("xref")).split(Pattern.quote("|"))));
                s_inf.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("s_inf")).split(Pattern.quote("|"))));
                ant.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("ant")).split(Pattern.quote("|"))));
                lsource.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("lsource")).split(Pattern.quote("|"))));
                stagk.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("stagk")).split(Pattern.quote("|"))));
                stagr.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("stagr")).split(Pattern.quote("|"))));
                field.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("field")).split(Pattern.quote("|"))));
                dial.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("dial")).split(Pattern.quote("|"))));

                for (int i = 0; i < gloss.size(); i++) {
                    ESense es = new ESense();
                    es.getGloss().addAll(Arrays.asList(gloss.get(i).split(";")));
                    es.getPos().addAll(Arrays.asList(pos.get(i).split(";")));
                    es.getMisc().addAll(Arrays.asList(misc.get(i).split(";")));
                    es.getXref().addAll(Arrays.asList(xref.get(i).split(";")));
                    es.getS_inf().addAll(Arrays.asList(s_inf.get(i).split(";")));
                    es.getAnt().addAll(Arrays.asList(ant.get(i).split(";")));
                    es.getLsource().addAll(Arrays.asList(lsource.get(i).split(";")));
                    es.getStagk().addAll(Arrays.asList(stagk.get(i).split(";")));
                    es.getStagr().addAll(Arrays.asList(stagr.get(i).split(";")));
                    es.getField().addAll(Arrays.asList(field.get(i).split(";")));
                    es.getDial().addAll(Arrays.asList(dial.get(i).split(";")));

                    etemp.add(es);
                }

                temp.senses = etemp;
            temp.entry_seq = cursor.getInt(cursor.getColumnIndex("entry"));
            temp.kreading.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("keb")).split(";")));
            temp.reading.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("reb")).split(";")));
            rsEnts.add(temp);
            temp = new DEntry();
            etemp = new ArrayList<>();
            cursor.moveToNext();
        }
    }
    //timings.addSplit("rte end");
    //timings.dumpToLog();
        return rsEnts;
}


    public DEntry rowToEntry(Cursor cursor) {
        DEntry temp = new DEntry();
        ArrayList<ESense> etemp = new ArrayList<>();
        ArrayList<String> gloss = new ArrayList<>();
        ArrayList<String> pos = new ArrayList<>();
        ArrayList<String> misc = new ArrayList<>();
        ArrayList<String> xref = new ArrayList<>();
        ArrayList<String> s_inf = new ArrayList<>();
        ArrayList<String> ant = new ArrayList<>();
        ArrayList<String> lsource = new ArrayList<>();
        ArrayList<String> field = new ArrayList<>();
        ArrayList<String> dial = new ArrayList<>();
        ArrayList<String> stagr = new ArrayList<>();
        ArrayList<String> stagk = new ArrayList<>();
        //System.out.println(cursor.getInt(cursor.getColumnIndex("nf")));
        gloss.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("gloss")).split(Pattern.quote("|"))));
        pos.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("pos")).split(Pattern.quote("|"))));
        misc.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("misc")).split(Pattern.quote("|"))));
        xref.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("xref")).split(Pattern.quote("|"))));
        s_inf.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("s_inf")).split(Pattern.quote("|"))));
        ant.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("ant")).split(Pattern.quote("|"))));
        lsource.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("lsource")).split(Pattern.quote("|"))));
        stagk.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("stagk")).split(Pattern.quote("|"))));
        stagr.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("stagr")).split(Pattern.quote("|"))));
        field.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("field")).split(Pattern.quote("|"))));
        dial.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("dial")).split(Pattern.quote("|"))));

        for (int i = 0; i < gloss.size(); i++) {
            ESense es = new ESense();
            es.getGloss().addAll(Arrays.asList(gloss.get(i).split(";")));
            es.getPos().addAll(Arrays.asList(pos.get(i).split(";")));
            es.getMisc().addAll(Arrays.asList(misc.get(i).split(";")));
            es.getXref().addAll(Arrays.asList(xref.get(i).split(";")));
            es.getS_inf().addAll(Arrays.asList(s_inf.get(i).split(";")));
            es.getAnt().addAll(Arrays.asList(ant.get(i).split(";")));
            es.getLsource().addAll(Arrays.asList(lsource.get(i).split(";")));
            es.getStagk().addAll(Arrays.asList(stagk.get(i).split(";")));
            es.getStagr().addAll(Arrays.asList(stagr.get(i).split(";")));
            es.getField().addAll(Arrays.asList(field.get(i).split(";")));
            es.getDial().addAll(Arrays.asList(dial.get(i).split(";")));

            etemp.add(es);
        }

        temp.senses = etemp;
        temp.entry_seq = cursor.getInt(cursor.getColumnIndex("entry"));
        temp.kreading.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("keb")).split(";")));
        temp.reading.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("reb")).split(";")));
        return temp;
    }


    public void closeDB() {
        DBH.close();
    }
}

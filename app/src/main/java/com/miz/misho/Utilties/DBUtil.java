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


/**
 * Database utility. Used for querying the database.
 */
public class DBUtil {
    private JMDataBaseHelper DBH;


    public DBUtil(Context context) {
            DBH = JMDataBaseHelper.getInstance(context);
    }


    // hiragana/katakana reading
    private String reb_SQL =
            "SELECT * FROM dictionary WHERE reb LIKE ? " +
                    "OR reb LIKE ? " +
                    "OR reb LIKE ? " +
                    "OR reb LIKE ?"+
                    "ORDER BY nf ASC LIMIT ?";

    //kanji reading
    private String keb_SQL =
            "SELECT * FROM dictionary WHERE keb LIKE ? " +
                    "OR keb LIKE ? " +
                    "OR keb LIKE ? " +
                    "OR keb LIKE ?"+
                    "ORDER BY nf ASC LIMIT ?";

    //glossary/english meaning
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

    // pos is position of the spinner that contains the search options.

    // 0 = contains
    // 1 = starts with
    // 2 = ends with
    // 3 = match only

    /**
     * Query for the English meaning.
     * @param text text to be searched.
     * @param pos position of spinner.
     * @param limit limit that's set in the preferences activity
     * @return ArrayList of Dictionary Entry objects.
     */
    public ArrayList<DEntry> gloss_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(gloss_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%", "[" + params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) + "]", "[" + params[pos].replace("x", text) + "]", limit});
        return rowsToEntries(c);
    }


    /**
     * Query using the kanji reading.
     * @param text text to be searched.
     * @param pos position of spinner.
     * @param limit limit that's set in the preferences activity
     * @return ArrayList of Dictionary Entry objects.
     */
    public ArrayList<DEntry> keb_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(keb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text)  , params[pos].replace("x", text), limit});
        return rowsToEntries(c);
    }


    /**
     * Query for the hiragana/katakana reading.
     * @param text text to be searched.
     * @param pos position of spinner.
     * @param limit limit that's set in the preferences activity
     * @return ArrayList of Dictionary Entry objects.
     */
    public ArrayList<DEntry> reb_Query(String text, int pos, String limit) {
        Cursor c = DBH.mdb.rawQuery(reb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) ,  params[pos].replace("x", text), limit});
        return rowsToEntries(c);
    }

    @Deprecated
    public Cursor gloss_QueryCursor(String text, int pos, String limit) {
        return DBH.mdb.rawQuery(gloss_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%", "[" + params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) + "]", "[" + params[pos].replace("x", text) + "]", limit});
    }

    @Deprecated
    public Cursor keb_QueryCursor(String text, int pos, String limit) {
        return  DBH.mdb.rawQuery(keb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text)  , params[pos].replace("x", text), limit});
    }

    @Deprecated
    public Cursor reb_QueryCursor(String text, int pos, String limit) {
        return  DBH.mdb.rawQuery(reb_SQL, new String[]{"%;" + params[pos].replace("x", text) + ";%",  params[pos].replace("x", text) + ";%", "%;" + params[pos].replace("x", text) ,  params[pos].replace("x", text), limit});
    }

    /**
     * Query to look up a specific kanji character. Should always result with an array list of
     * size 1. (The kanji character itself is the primary key in the database)
     * @param kanji
     * @return An arrayList of Kanji Entries
     */
    public ArrayList<KEntry> kanji_Query(String kanji) {
        Cursor c = DBH.mdb.rawQuery(kanji_SQL, new String[] {kanji});
        return  rowsToKEntries(c);
    }

    /**
     * Kunyomi query.
     * @param kun Kunyomi string
     * @return Kanji containing the queried kunyomi
     */
    public ArrayList<KEntry> kun_Query(String kun) {
        Cursor c = DBH.mdb.rawQuery(kun_SQL, new String[] {"%"+kun+"%"});
        return  rowsToKEntries(c);
    }

    /**
     * Onyomi query.
     * @param kun Onyomi string
     * @return Kanji containing the queried onyomi
     */
    public ArrayList<KEntry> ony_Query(String ony) {
        Cursor c = DBH.mdb.rawQuery(ony_SQL, new String[] {"%"+ony+"%"});
        return  rowsToKEntries(c);
    }

    /**
     * Queries for kanji using radicals.
     * @param rad ArrayList of radicals to search with
     * @return an ArrayList of kanji from the resulting cursor.
     */
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

    /**
     * Used in radical search to return resulting kanji.
     * @param c cursor from query
     * @return returns resulting kanji in an arraylist of radicals from radical search
     */
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


    /**
     * Returns an arraylist of Kanji Entry objects from the passed cursor.
     * @param cursor from database query
     * @return an arraylist of Kanji Entry Objects
     */
    ArrayList<KEntry> rowsToKEntries(Cursor cursor) {
        ArrayList<KEntry> kr = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                KEntry tmp = new KEntry();
                ArrayList<String> tmpmean = new ArrayList<>();

                ArrayList<String> tmpkunyomi = new ArrayList<>();

                ArrayList<String> tmponyomi = new ArrayList<>();

                ArrayList<String> tmpnanori = new ArrayList<>();

                tmp.setKanji(cursor.getString(cursor.getColumnIndex("kanji")));
                tmp.setJlpt(cursor.getInt(cursor.getColumnIndex("jlpt")));
                tmp.setFreq(cursor.getInt(cursor.getColumnIndex("freq")));
                tmp.setGrade(cursor.getInt(cursor.getColumnIndex("grade")));
                tmp.setStroke_count(cursor.getInt(cursor.getColumnIndex("stroke_count")));

                tmpkunyomi.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("kunyomi")).split(";")));
                tmponyomi.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("onyomi")).split(";")));
                tmpnanori.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("nanori")).split(";")));
                tmpmean.addAll(Arrays.asList(cursor.getString(cursor.getColumnIndex("meanings")).split(";")));

                tmp.setKunyomi(tmpkunyomi);
                tmp.setOnyomi(tmponyomi);
                tmp.setNanori(tmpnanori);
                tmp.setMeaning(tmpmean);

                kr.add(tmp);
                cursor.moveToNext();
            }
        }
        return kr;
    }

    /**
     * Returns an arraylist of Database Entry objects from the passed cursor.
     * @param cursor from database query
     * @return an arraylist of Dictionary Entry Objects
     */
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


    @Deprecated
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

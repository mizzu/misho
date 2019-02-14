package com.miz.misho.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class DEntry implements Serializable{

    public ArrayList<ESense> senses;
    public ArrayList<String> reading;
    public ArrayList<String> kreading;
    public int entry_seq;
    public int re_pri;

    public DEntry() {
        senses = new ArrayList<>();
        reading = new ArrayList<>();
        kreading = new ArrayList<>();
        re_pri = 1000;
        entry_seq = 0;
    }

    public int getEntry_seq() {
        return entry_seq;
    }

    public void setEntry_seq(int entry_seq) {
        this.entry_seq = entry_seq;
    }

    public int getRe_pri() {
        return re_pri;
    }

    public void setRe_pri(int re_pri) {
        this.re_pri = re_pri;
    }

    public ArrayList<ESense> getSenses() {
        return senses;
    }

    public void setSenses(ArrayList<ESense> senses) {
        this.senses = senses;
    }

    public void addToSenses(ESense e) {
        senses.add(e);
    }

    public ESense getSense(int position) {
        return senses.get(position);
    }

    public ArrayList<String> getReading() {
        return reading;
    }

    public void setReading(ArrayList<String> reading) {
        this.reading = reading;
    }

    public void addToReading(String s) {
        reading.add(s);
    }

    public String getReading(int position) {
        return reading.get(position);
    }

    public ArrayList<String> getKreading() {
        return kreading;
    }

    public void setKreading(ArrayList<String> kreading) {
        this.kreading = kreading;
    }


    public void addToKReading(String s) {
        kreading.add(s);
    }

    public String getKReading(int position) {
        return kreading.get(position);
    }
}

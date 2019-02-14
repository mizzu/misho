package com.miz.misho.Objects;

import java.util.ArrayList;

public class RadBlocks {
    private int strokes;
    private ArrayList<Radical> radlist;

    public RadBlocks(int strokes) {
        this.strokes = strokes;
        radlist = new ArrayList<>();
    }

    public RadBlocks(int strokes, ArrayList<Radical> rads) {
        this.strokes = strokes;
        radlist = rads;
    }

    public boolean addRad(Radical r) {
        if(radlist.contains(r))
            return false;
        else
            radlist.add(r);
        return true;
    }

    public void setStrokes(int strokes) {
        this.strokes = strokes;
    }

    public void setRadlist(ArrayList<Radical> radlist) {
        this.radlist = radlist;
    }

    public int getStrokes() {
        return strokes;
    }

    public ArrayList<Radical> getRadlist() {
        return radlist;
    }
}

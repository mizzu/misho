package com.miz.misho.Objects;

import java.io.Serializable;

/**
 * Holds radical information. Also used for the radical search to hold kanji elements.
 */
public class Radical implements Serializable {
    private String radical;
    private int strokes;

    public Radical (String rad, int strokes) {
        this.strokes = strokes;
        this.radical = rad;
    }

    public void setRadical(String radical) {
        this.radical = radical;
    }

    public void setStrokes(int strokes) {
        this.strokes = strokes;
    }

    public int getStrokes() {
        return strokes;
    }

    public String getRadical() {
        return radical;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof Radical))
            return false;
        return ((Radical) o).getRadical().equals(this.getRadical());
    }

}

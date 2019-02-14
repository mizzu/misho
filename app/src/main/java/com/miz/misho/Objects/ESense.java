package com.miz.misho.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class ESense implements Serializable {

    private ArrayList<String> pos;
    private ArrayList<String> gloss;
    private ArrayList<String> misc;
    private ArrayList<String> xref;
    private ArrayList<String> s_inf;
    private ArrayList<String> ant;
    private ArrayList<String> lsource;
    private ArrayList<String> field;
    private ArrayList<String> dial;
    private ArrayList<String> stagr;
    private ArrayList<String> stagk;

    public ESense() {
        pos = new ArrayList<>();
        gloss = new ArrayList<>();
        misc = new ArrayList<>();
        xref = new ArrayList<>();
        s_inf = new ArrayList<>();
        ant = new ArrayList<>();
        lsource = new ArrayList<>();
        stagr = new ArrayList<>();
        stagk = new ArrayList<>();
        field = new ArrayList<>();
        dial = new ArrayList<>();
    }

    public ArrayList<String> getPos() {
        return pos;
    }

    public void setPos(ArrayList<String> pos) {
        this.pos = pos;
    }

    public void addToPos(String s)
    {
        pos.add(s);
    }

    public String getPos(Integer position)
    {
        return pos.get(position);
    }

    public ArrayList<String> getGloss() {
        return gloss;
    }

    public void setGloss(ArrayList<String> gloss) {
        this.gloss = gloss;
    }

    public void addToGloss(String s)
    {
        gloss.add(s);
    }

    public String getGloss(Integer position)
    {
        return gloss.get(position);
    }

    public ArrayList<String> getMisc() {
        return misc;
    }

    public void setMisc(ArrayList<String> misc) {
        this.misc = misc;
    }

    public void addToMisc(String s)
    {
        misc.add(s);
    }

    public String getMisc(Integer position)
    {
        return misc.get(position);
    }

    public ArrayList<String> getXref() {
        return xref;
    }

    public void setXref(ArrayList<String> xref) {
        this.xref = xref;
    }

    public void addToXref(String s)
    {
        xref.add(s);
    }

    public String getXref(Integer position)
    {
        return xref.get(position);
    }

    public ArrayList<String> getS_inf() {
        return s_inf;
    }

    public void setS_inf(ArrayList<String> s_inf) {
        this.s_inf = s_inf;
    }

    public void addToS_inf(String s)
    {
        s_inf.add(s);
    }

    public String getS_inf(Integer position)
    {
        return s_inf.get(position);
    }

    public ArrayList<String> getAnt() {
        return ant;
    }

    public void setAnt(ArrayList<String> ant) {
        this.ant = ant;
    }

    public void addToAnt(String s)
    {
        ant.add(s);
    }

    public String getAnt(Integer position)
    {
        return ant.get(position);
    }

    public ArrayList<String> getLsource() {
        return lsource;
    }

    public void setLsource(ArrayList<String> lsource) {
        this.lsource = lsource;
    }

    public void addToLsource(String s)
    {
        lsource.add(s);
    }

    public String getLsource(Integer position)
    {
        return lsource.get(position);
    }

    public ArrayList<String> getField() {
        return field;
    }

    public void setField(ArrayList<String> field) {
        this.field = field;
    }

    public void addToField(String s)
    {
        field.add(s);
    }

    public String getField(Integer position)
    {
        return field.get(position);
    }

    public ArrayList<String> getDial() {
        return dial;
    }

    public void setDial(ArrayList<String> dial) {
        this.dial = dial;
    }

    public void addToDial(String s)
    {
        dial.add(s);
    }

    public String getDial(Integer position)
    {
        return dial.get(position);
    }

    public ArrayList<String> getStagr() {
        return stagr;
    }

    public void setStagr(ArrayList<String> stagr) {
        this.stagr = stagr;
    }

    public void addToStagr(String s)
    {
        stagr.add(s);
    }

    public String getStagr(Integer position)
    {
        return stagr.get(position);
    }

    public ArrayList<String> getStagk() {
        return stagk;
    }

    public void setStagk(ArrayList<String> stagk) {
        this.stagk = stagk;
    }

    public void addToStagk(String s)
    {
        stagk.add(s);
    }

    public String getStagk(Integer position)
    {
        return stagk.get(position);
    }
}
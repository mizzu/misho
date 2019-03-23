package com.miz.misho.Objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Kanji - Entry object. Meant to hold information as an object from kdict file.
 */
public class KEntry implements Serializable {
    private String kanji;
    private ArrayList<String> onyomi;
    private ArrayList<String> kunyomi;
    private ArrayList<String> nanori;
    private ArrayList<String> meaning;
    private ArrayList<String> radicals;
    private int grade;
    private int stroke_count;
    private int jlpt;
    private int freq;


    public KEntry() {
        onyomi = new ArrayList<>();
        kunyomi = new ArrayList<>();
        nanori = new ArrayList<>();
        meaning = new ArrayList<>();
        radicals = new ArrayList<>();
        kanji = "";
        grade = 0;
        stroke_count = 0;
        jlpt = 0;
        freq = 0;

    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public int getFreq() {
        return freq;
    }

    public int getGrade() {
        return grade;
    }

    public int getJlpt() {
        return jlpt;
    }

    public int getStroke_count() {
        return stroke_count;
    }

    public ArrayList<String> getKunyomi() {
        return kunyomi;
    }

    public ArrayList<String> getMeaning() {
        return meaning;
    }

    public ArrayList<String> getNanyomi() {
        return nanori;
    }

    public ArrayList<String> getOnyomi() {
        return onyomi;
    }

    public ArrayList<String> getRadicals() {
        return radicals;
    }

    public void addKunyomi(String s) {
        kunyomi.add(s);
    }

    public void addOnyomi(String s) {
        onyomi.add(s);
    }

    public void addNanori(String s) {
        nanori.add(s);
    }

    public void addMeaning(String s) {
        meaning.add(s);
    }

    public void addRadical(String s) {
        radicals.add(s);
    }


    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void setJlpt(int jlpt) {
        this.jlpt = jlpt;
    }

    public void setKunyomi(ArrayList<String> kunyomi) {
        this.kunyomi = kunyomi;
    }

    public void setMeaning(ArrayList<String> meaning) {
        this.meaning = meaning;
    }

    public void setNanori(ArrayList<String> nanori) {
        this.nanori = nanori;
    }

    public void setOnyomi(ArrayList<String> onyomi) {
        this.onyomi = onyomi;
    }

    public void setRadicals(ArrayList<String> radicals) {
        this.radicals = radicals;
    }

    public void setStroke_count(int stroke_count) {
        this.stroke_count = stroke_count;
    }

}


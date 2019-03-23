package com.miz.misho.Objects;

import java.io.Serializable;

/**
 * Vocab list object that holds relevant information.
 */
public class VocabList implements Serializable {
    private String name;
    private long size;
    private String path;

    public VocabList(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public VocabList(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public VocabList(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}

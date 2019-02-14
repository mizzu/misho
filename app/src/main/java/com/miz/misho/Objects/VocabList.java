package com.miz.misho.Objects;

import java.io.Serializable;

public class VocabList implements Serializable {
    private String name;
    private long size;

    public VocabList(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}

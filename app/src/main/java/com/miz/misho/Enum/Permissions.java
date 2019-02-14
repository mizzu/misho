package com.miz.misho.Enum;

public enum Permissions {
    MISHO_WRITE_TO_EXTERNAL_STORAGE(1);
    private final int permission;

    Permissions(final int val) {
        this.permission= val;
    }

    public int getVal() {
        return permission;
    }
}

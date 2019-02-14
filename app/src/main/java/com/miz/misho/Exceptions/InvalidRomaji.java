package com.miz.misho.Exceptions;


public class InvalidRomaji extends Exception {
    public InvalidRomaji() {
    }

    public InvalidRomaji(String message) {
        super(message);
    }
}
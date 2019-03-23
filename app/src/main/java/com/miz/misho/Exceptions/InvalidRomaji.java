package com.miz.misho.Exceptions;

/**
 * Called when an invalid romaji input is detected when trying to convert
 */
public class InvalidRomaji extends Exception {
    public InvalidRomaji() {
    }

    public InvalidRomaji(String message) {
        super(message);
    }
}
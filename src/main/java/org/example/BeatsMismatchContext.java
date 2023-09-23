package org.example;

import java.util.List;

public class BeatsMismatchContext extends MismatchContext {
    private final List<String[]> beats;

    public BeatsMismatchContext(BeatBox beatBox, List<String[]> beats, String checkMark) {
        super(beatBox, checkMark);
        this.beats = beats;
    }

    List<String[]> beats() {
        return beats;
    }
}

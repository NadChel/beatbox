package org.example;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MismatchContext {
    private final BeatBox beatBox;
    private final String checkMark;

    BeatBox beatBox() {
        return this.beatBox;
    }

    String checkMark() {
        return this.checkMark;
    }
}

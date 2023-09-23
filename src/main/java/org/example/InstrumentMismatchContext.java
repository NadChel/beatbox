package org.example;

import java.util.Arrays;

public final class InstrumentMismatchContext extends MismatchContext {
    private final String name;
    private final String[] beats;

    public InstrumentMismatchContext(BeatBox beatBox, String name,
                                     String[] beats, String checkMark) {
        super(beatBox, checkMark);
        this.name = name;
        this.beats = beats;
    }

    String name() {
        return name;
    }

    String[] beats() {
        return beats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InstrumentMismatchContext) obj;
        return this.beatBox().equals(that.beatBox()) &&
                this.name.equals(that.name) &&
                Arrays.equals(this.beats(), that.beats()) &&
                this.checkMark().equals(that.checkMark());
    }

    @Override
    public int hashCode() {
        int result = beatBox().hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(beats());
        result = 31 * result + checkMark().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InstrumentMismatchContext[" +
                "beatBox=" + beatBox() + ", " +
                "name=" + name + ", " +
                "beats=" + Arrays.toString(beats()) + ", " +
                "checkMark=" + checkMark() + ']';
    }

}

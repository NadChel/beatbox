package org.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.example.MidiInstrument.*;

public final class InstrumentFactory {
    public static Collection<MidiInstrument> getKathysInstruments() {
        /*
        Instruments used in Kathy Sierra's book Head First Java
         */
        return List.of(
                ACOUSTIC_BASS_DRUM, CLOSED_HI_HAT, OPEN_HI_HAT, ACOUSTIC_SNARE,
                CRASH_CYMBAL_1, HAND_CLAP, HIGH_TOM, HIGH_BONGO,
                MARACAS, LONG_WHISTLE, LOW_CONGA, COWBELL,
                VIBRASLAP, LOW_MID_TOM, HIGH_AGOGO, OPEN_HIGH_CONGA
        );
    }

    public static Collection<MidiInstrument> getAllInstrumentsInStandardOrder() {
        return Arrays.asList(MidiInstrument.values());
    }
}

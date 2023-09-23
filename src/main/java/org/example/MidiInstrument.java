package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum MidiInstrument {
    FILTER_SNAP("Filter Snap", 27), SLAP_NOISE("Slap Noise", 28),
    SCRATCH_PUSH("Scratch Push", 29), SCRATCH_PULL("Scratch Pull", 30),
    DRUMSTICKS("Drumsticks", 31), CLICK_BOISE("Click Boise", 32),
    METRONOME_CLICK("Metronome Click", 33), METRONOME_BELL("Metronome Bell", 34),
    ACOUSTIC_BASS_DRUM("Acoustic Bass Drum", 35), ELECTRIC_BASS_DRUM("Electric Bass Drum", 36),
    SIDE_STICK("Side Stick", 37), ACOUSTIC_SNARE("Acoustic Snare", 38),
    HAND_CLAP("Hand Clap", 39), ELECTRIC_SNARE("Electric Snare", 40),
    LOW_FLOOR_TOM("Low Floor Tom", 41), CLOSED_HI_HAT("Closed Hi-hat", 42),
    HIGH_FLORR_TOM("High Floor Tom", 43), PEDAL_HI_HAT("Pedal Hi-hat", 44),
    LOW_TOM("Low Tom", 45), OPEN_HI_HAT("Open Hi-hat", 46),
    LOW_MID_TOM("Low-Mid Tom", 47), HIGH_MID_TOM("High-Mid Tom", 48),
    CRASH_CYMBAL_1("Crash Cymbal 1", 49), HIGH_TOM("High Tom", 50),
    RIDE_CYMBAL_1("Ride Cymbal 1", 51), CHINESE_CYMBAL("Chinese Cymbal", 52),
    RIDE_BELL("Ride Bell", 53), TAMBOURINE("Tambourine", 54),
    SPLASH_CYMBAL("Splash Cymbal", 55), COWBELL("Cowbell", 56),
    CRASH_CYMBAL_2("Crash Cymbal 2", 57), VIBRASLAP("Vibraslap", 58),
    RIDE_CYMBAL_2("Ride Cymbal 2", 59), HIGH_BONGO("High Bongo", 60),
    LOW_BONGO("Low Bongo", 61), MUTE_HIGH_CONGA("Mute High Conga", 62),
    OPEN_HIGH_CONGA("Open High Conga", 63), LOW_CONGA("Low Conga", 64),
    HIGH_TIMBALE("High Timbale", 65), LOW_TIMBALE("Low Timbale", 66),
    HIGH_AGOGO("High Agogo", 67), LOW_AGOGO("Low Agogo", 68),
    CABASA("Cabasa", 69), MARACAS("Maracas", 70),
    SHORT_WHISTLE("Short Whistle", 71), LONG_WHISTLE("Long Whistle", 72),
    SHORT_GUIRO("Short Guiro", 73), LONG_GUIRO("Long Guiro", 74),
    CLAVES("Claves", 75), HIGH_WOODBLOCK("High Woodblock", 76),
    LOW_WOODBLOCK("Low Woodblock", 77), MUTE_CUICA("Mute Cuica", 78),
    OPEN_CUICA("Open Cuica", 79), MUTE_TRIANGLE("Mute Triangle", 80),
    OPEN_TRIANGLE("Open Triangle", 81), SHAKER("Shaker", 82),
    JINGLE_BELL("Jingle Bell", 83), BELL_TREE("Bell Tree", 84),
    CASTANETS("Castanets", 85), MUTED_SURDO("Muted Surdo", 86),
    OPEN_SURDO("Open Surdo", 87);

    private final String standardName;
    private final int firstByte;

    static Optional<MidiInstrument> getForName(String instrumentName) {
        return Arrays.stream(values())
                .filter(instrument -> instrument.standardName.equalsIgnoreCase(instrumentName))
                .findFirst();
    }

    @Override
    public String toString() {
        return standardName;
    }
}
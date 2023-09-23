package org.example.beatBox;

import org.example.demos.sequencer.beatBox.BeatBox;
import org.example.demos.sequencer.beatBox.BeatBoxConfiguration;
import org.example.demos.sequencer.beatBox.InstrumentFactory;
import org.example.demos.sequencer.beatBox.PressedButtonStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.example.demos.sequencer.beatBox.MidiInstrument.ACOUSTIC_SNARE;
import static org.example.demos.sequencer.beatBox.MidiInstrument.DRUMSTICKS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeatBoxTest {
    @Test
    public void testValidInstruments() {
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setInstruments(List.of(DRUMSTICKS, ACOUSTIC_SNARE))
                .build();
        var instruments = beatBox.getInstruments();
        assertEquals(2, instruments.size());
        Stream.of(DRUMSTICKS, ACOUSTIC_SNARE)
                .forEach(name -> assertTrue(instruments.contains(name)));
    }

    @Test
    public void testGettingDefaultInstrumentsIfNoInstrumentsPassed() {
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setInstruments(Collections.emptyList())
                .build();
        assertEquals(InstrumentFactory.getKathysInstruments().stream().toList(),
                beatBox.getInstruments().stream().toList());
    }

    @Test
    public void testSettingNumberOfBeats() {
        int numberOfBeats = 20;
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setNumberOfBeats(numberOfBeats)
                .build();
        assertEquals(numberOfBeats, beatBox.getNumberOfBeats());
    }

    @Test
    public void testSettingSeparator() {
        String customSeparator = "====>";
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setSeparator(customSeparator)
                .build();
        assertEquals(customSeparator, beatBox.getSeparator());
    }

    @Test
    public void testSettingCheckMark() {
        String customCheckMark = "+";
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setCheckMark(customCheckMark)
                .build();
        assertEquals(customCheckMark, beatBox.getCheckMark());
    }

    @Test
    public void testSettingBlankMark() {
        String customBlankMark = "-";
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setBlankMark(customBlankMark)
                .build();
        assertEquals(customBlankMark, beatBox.getBlankMark());
    }

    @Test
    public void testSettingTempo() {
        float delta = 0.0001f;
        float customTempo = 201;
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setTempoInBPM(customTempo)
                .build();
        assertEquals(customTempo, beatBox.getTempo(), delta);
    }

    @Test
    public void testSettingPressedButtonBehavior() {
        PressedButtonStrategy pressedButtonStrategy = PressedButtonStrategy.SELECT;
        BeatBox beatBox = BeatBoxConfiguration.configure()
                .setPressedButtonStrategy(pressedButtonStrategy)
                .build();
        assertEquals(pressedButtonStrategy, beatBox.getPressedButtonStrategy());
    }
}

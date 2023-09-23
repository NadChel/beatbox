package org.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

@Slf4j
public class BeatBoxConfiguration {
    @Getter(value = AccessLevel.PACKAGE)
    private Map<MidiInstrument, List<JCheckBox>> instrumentMap =
            instrumentsToInstrumentMap(InstrumentFactory.getKathysInstruments());
    @Getter
    private int numberOfBeats = 16;
    @Getter
    private float tempoInBPM = 120;
    @Getter
    private PressedButtonStrategy pressedButtonStrategy = PressedButtonStrategy.TOGGLE;
    @Getter
    private MissingInstrumentStrategy missingInstrumentStrategy = MissingInstrumentStrategy.ADD_IF_BEATS_NOT_BLANK;
    @Getter
    private MissingBeatsStrategy missingBeatsStrategy = MissingBeatsStrategy.ADD_IF_BEATS_NOT_BLANK;
    @Getter
    private ExcessBeatsStrategy excessBeatsStrategy = ExcessBeatsStrategy.TRIM_ALWAYS;
    @Getter
    private String separator = ":";
    @Getter
    private String checkMark = "X";
    @Getter
    private String blankMark = "O";
    private boolean didUnsuccessfulInstrumentSettingOccur;

    public static BeatBoxConfiguration configure() {
        return new BeatBoxConfiguration();
    }

    public Collection<MidiInstrument> getInstruments() {
        return instrumentMap.keySet();
    }

    public BeatBoxConfiguration setInstruments(@NotNull MidiInstrument... instruments) {
        setInstruments(Arrays.asList(instruments));
        return this;
    }

    public BeatBoxConfiguration setInstruments(@NotNull Collection<MidiInstrument> instruments) {
        BeatBoxUtils.requireNonNull(instruments, "instruments");
        var customInstrumentMap = instrumentsToInstrumentMap(instruments);
        if (!customInstrumentMap.isEmpty()) {
            instrumentMap = customInstrumentMap;
            didUnsuccessfulInstrumentSettingOccur = false;
        } else {
            didUnsuccessfulInstrumentSettingOccur = true;
        }
        return this;
    }

    public BeatBoxConfiguration setNumberOfBeats(int numberOfBeats) {
        BeatBoxUtils.requirePositive(numberOfBeats, "numberOfBeats");
        this.numberOfBeats = numberOfBeats;
        return this;
    }

    public BeatBoxConfiguration setTempoInBPM(float tempoInBPM) {
        BeatBoxUtils.requirePositive(tempoInBPM, "tempoInBPM");
        this.tempoInBPM = tempoInBPM;
        return this;
    }

    public BeatBoxConfiguration setSeparator(@NotNull String separator) {
        BeatBoxUtils.requireManifestComponentValidity(separator, ManifestComponent.SEPARATOR);
        this.separator = separator;
        return this;
    }

    public BeatBoxConfiguration setCheckMark(@NotNull String checkMark) {
        BeatBoxUtils.requireManifestComponentValidity(checkMark, ManifestComponent.CHECK_MARK);
        this.checkMark = checkMark;
        return this;
    }

    public BeatBoxConfiguration setBlankMark(@NotNull String blankMark) {
        BeatBoxUtils.requireManifestComponentValidity(blankMark, ManifestComponent.BLANK_MARK);
        this.blankMark = blankMark;
        return this;
    }

    public BeatBoxConfiguration setMissingInstrumentStrategy(@NotNull MissingInstrumentStrategy strategy) {
        BeatBoxUtils.requireNonNull(strategy, "missingInstrumentStrategy");
        this.missingInstrumentStrategy = strategy;
        return this;
    }

    public BeatBoxConfiguration setMissingBeatsStrategy(@NotNull MissingBeatsStrategy strategy) {
        BeatBoxUtils.requireNonNull(strategy, "missingBeatsStrategy");
        this.missingBeatsStrategy = strategy;
        return this;
    }

    public BeatBoxConfiguration setExcessBeatsStrategy(@NotNull ExcessBeatsStrategy strategy) {
        BeatBoxUtils.requireNonNull(strategy, "excessBeatsStrategy");
        this.excessBeatsStrategy = strategy;
        return this;
    }

    public BeatBoxConfiguration setPressedButtonStrategy(@NotNull PressedButtonStrategy pressedButtonStrategy) {
        BeatBoxUtils.requireNonNull(pressedButtonStrategy, "pressedButtonBehavior");
        this.pressedButtonStrategy = pressedButtonStrategy;
        return this;
    }

    public BeatBox build() {
        if (didUnsuccessfulInstrumentSettingOccur) {
            log.warn("No instruments found. Default instruments are going to be applied");
        }
        return new BeatBox(this);
    }

    private Map<MidiInstrument, List<JCheckBox>> instrumentsToInstrumentMap(Collection<MidiInstrument> instruments) {
        Map<MidiInstrument, List<JCheckBox>> instrumentMap = new LinkedHashMap<>(instruments.size());
        for (MidiInstrument instrument : instruments) {
            instrumentMap.put(instrument, new ArrayList<>(numberOfBeats));
        }
        return instrumentMap;
    }
}

package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public enum MissingInstrumentStrategy implements Strategy {
    ADD_ALWAYS {
        @Override
        boolean handle(InstrumentMismatchContext c) {
            var missingInstrumentName = c.name();
            var isInstrumentAdded = tryAndAddMissingInstrument(c);
            if (isInstrumentAdded) {
                log.info(String.format(LOG_FORMAT, missingInstrumentName,
                        INSTRUMENT_SUCCESSFULLY_ADDED_MSG, INSTRUMENT_MISMATCH_FIXED_MSG));
            } else {
                if (areBeatsNonBlank(c.beats(), c.checkMark())) {
                    log.warn(String.format(LOG_FORMAT, missingInstrumentName,
                            INSTRUMENT_COULDNT_BE_ADDED_MSG, INSTRUMENT_MISMATCH_WILL_HAVE_EFFECT_MSG));
                } else {
                    log.info(String.format(LOG_FORMAT, missingInstrumentName,
                            INSTRUMENT_COULDNT_BE_ADDED_MSG, INSTRUMENT_MISMATCH_IRRELEVANT_MSG));
                }
            }
            return isInstrumentAdded;
        }
    }, ADD_IF_BEATS_NOT_BLANK {
        @Override
        boolean handle(InstrumentMismatchContext context) {
            var missingInstrumentName = context.name();
            var beats = context.beats();
            var checkMark = context.checkMark();
            if (areBeatsNonBlank(beats, checkMark)) {
                var isInstrumentAdded = tryAndAddMissingInstrument(context);
                if (isInstrumentAdded) {
                    log.info(String.format(LOG_FORMAT, missingInstrumentName,
                            INSTRUMENT_SUCCESSFULLY_ADDED_MSG, INSTRUMENT_MISMATCH_FIXED_MSG));
                } else {
                    log.warn(String.format(LOG_FORMAT, missingInstrumentName,
                            INSTRUMENT_COULDNT_BE_ADDED_MSG, INSTRUMENT_MISMATCH_WILL_HAVE_EFFECT_MSG));
                }
                return isInstrumentAdded;
            }
            log.info(String.format(LOG_FORMAT, missingInstrumentName,
                    NO_ACTION_TAKEN_MSG, INSTRUMENT_MISMATCH_IRRELEVANT_MSG));
            return false;
        }
    }, ADD_NEVER {
        @Override
        boolean handle(InstrumentMismatchContext context) {
            var missingInstrumentName = context.name();
            var beats = context.beats();
            var checkMark = context.checkMark();
            if (areBeatsNonBlank(beats, checkMark)) {
                log.warn(String.format(LOG_FORMAT, missingInstrumentName, NO_ACTION_TAKEN_MSG,
                        INSTRUMENT_MISMATCH_WILL_HAVE_EFFECT_MSG));
            } else {
                log.info(String.format(LOG_FORMAT, missingInstrumentName, NO_ACTION_TAKEN_MSG,
                        INSTRUMENT_MISMATCH_IRRELEVANT_MSG));
            }
            return false;
        }
    };
    // instrument name; action; result
    static final String LOG_FORMAT = "%s was missing from the current instrument map. %s. %s";
    static final String INSTRUMENT_SUCCESSFULLY_ADDED_MSG = "The missing instrument was successfully added";
    static final String INSTRUMENT_COULDNT_BE_ADDED_MSG = "The missing instrument couldn't be added " +
            "(likely because it's not supported by the MIDI standard)";
    static final String INSTRUMENT_MISMATCH_FIXED_MSG = "The instrument's beats will be copied";
    static final String INSTRUMENT_MISMATCH_WILL_HAVE_EFFECT_MSG = "The beats for the instrument in the pattern " +
            "were not blank – the sound won't be able to fully match the pattern";
    static final String INSTRUMENT_MISMATCH_IRRELEVANT_MSG = "No effect on the sound is expected: the beats for the instrument were blank";

    abstract boolean handle(InstrumentMismatchContext context);

    boolean tryAndAddMissingInstrument(InstrumentMismatchContext context) {
        String missingInstrumentName = context.name();
        var beatBox = context.beatBox();
        Optional<MidiInstrument> optionalInstrument = MidiInstrument.getForName(missingInstrumentName);
        if (optionalInstrument.isPresent()) {
            beatBox.addNewInstrument(optionalInstrument.get());
            return true;
        }
        return false;
    }

    boolean areBeatsNonBlank(String[] beats, String checkMark) {
        return Arrays.asList(beats).contains(checkMark);
    }
}
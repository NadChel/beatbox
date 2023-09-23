package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public enum MissingBeatsStrategy implements BeatsStrategy {
    ADD_ALWAYS {
        @Override
        public void handle(BeatsMismatchContext context) {
            BeatBox beatBox = context.beatBox();
            int lengthInApp = beatBox.getNumberOfBeats();
            int lengthInPattern = context.beats().get(0).length;
            beatBox.addMoreBeats(lengthInPattern - lengthInApp);
            log.info(String.format(LOG_FORMAT, lengthInApp, lengthInPattern,
                    BEATS_ADDED_MSG, BEAT_MISMATCH_FIXED_MSG));
        }
    },
    ADD_IF_BEATS_NOT_BLANK {
        @Override
        public void handle(BeatsMismatchContext context) {
            var patternBeats = context.beats();
            int lengthInApp = context.beatBox().getNumberOfBeats();
            int lengthInPattern = patternBeats.get(0).length;
            if (areAdditionalBeatsInPatternBlank(patternBeats, context.checkMark(), lengthInApp)) {
                log.info(String.format(LOG_FORMAT, lengthInApp, lengthInPattern,
                        NO_ACTION_TAKEN_MSG, BEAT_MISMATCH_IRRELEVANT_MSG));
            } else {
                context.beatBox().addMoreBeats(lengthInPattern - lengthInApp);
                log.info(String.format(LOG_FORMAT, lengthInApp, lengthInPattern,
                        BEATS_ADDED_MSG, BEAT_MISMATCH_FIXED_MSG));
            }
        }
    },
    ADD_NEVER {
        @Override
        public void handle(BeatsMismatchContext context) {
            List<String[]> patternBeats = context.beats();
            int lengthInApp = context.beatBox().getNumberOfBeats();
            int lengthInPattern = patternBeats.get(0).length;
            if (areAdditionalBeatsInPatternBlank(patternBeats, context.checkMark(), lengthInApp)) {
                log.info(String.format(LOG_FORMAT, lengthInApp, lengthInPattern,
                        NO_ACTION_TAKEN_MSG, BEAT_MISMATCH_IRRELEVANT_MSG));
            } else {
                log.warn(String.format(LOG_FORMAT, lengthInApp, lengthInPattern,
                        NO_ACTION_TAKEN_MSG, BEAT_MISMATCH_WILL_HAVE_EFFECT_MSG));
            }
        }
    };

    // number in instrument map; number in pattern; action; result
    private static final String LOG_FORMAT = "The number of beats per instrument in the current " +
            "instrument map (%d) was lower than the number of beats per instrument in the pattern (%d). %s. %s";
    private static final String BEATS_ADDED_MSG = "Missing beats were added";
    private static final String BEAT_MISMATCH_FIXED_MSG = "Beat pattern will be copied";
    private static final String BEAT_MISMATCH_IRRELEVANT_MSG = "No effect on the sound is expected: additional beats in the pattern were blank";

    public abstract void handle(BeatsMismatchContext context);

    private static boolean areAdditionalBeatsInPatternBlank(List<String[]> patternBeats, String checkMark, int numberOfBeatsInApp) {
        return patternBeats.stream()
                .map(beatRow -> Arrays.copyOfRange(beatRow, numberOfBeatsInApp, beatRow.length))
                .flatMap(Arrays::stream)
                .noneMatch(beat -> beat.equals(checkMark));
    }

}

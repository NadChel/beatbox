package org.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ExcessBeatsStrategy implements BeatsStrategy {
    TRIM_ALWAYS {
        @Override
        public void handle(BeatsMismatchContext context) {
            BeatBox beatBox = context.beatBox();
            int lengthInPattern = context.beats().get(0).length;
            int lengthInApp = beatBox.getNumberOfBeats();
            beatBox.removeBeats(lengthInApp - lengthInPattern);
            log.info(String.format(LOG_FORMAT, lengthInPattern, lengthInApp,
                    BEATS_REMOVED_MSG, BEAT_MISMATCH_FIXED_MSG));
        }
    }, TRIM_NEVER {
        @Override
        public void handle(BeatsMismatchContext context) {
            int lengthInPattern = context.beats().get(0).length;
            int lengthInApp = context.beatBox().getNumberOfBeats();
            log.warn(String.format(LOG_FORMAT, lengthInPattern, lengthInApp,
                    NO_ACTION_TAKEN_MSG, BEAT_MISMATCH_WILL_HAVE_EFFECT_MSG));
        }
    };
    // number in pattern; number in instrument map; action; result
    private static final String LOG_FORMAT = "The number of beats per instrument in the pattern (%d) was lower than the number " +
            "of beats per instrument in the current instrument map (%d). %s. %s";
    private static final String BEATS_REMOVED_MSG = "Excess beats were removed";
    private static final String BEAT_MISMATCH_FIXED_MSG = "The number of beats per instrument is now in line with the beat pattern";

    public abstract void handle(BeatsMismatchContext context);
}
